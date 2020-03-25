package tho.nill.datenlieferung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import entities.Datenaustausch;
import entities.Datenlieferung;
import entities.EingeleseneDatei;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import repositories.DatenaustauschRepository;
import repositories.DatenlieferungProtokollRepository;
import repositories.DatenlieferungRepository;
import repositories.EingeleseneDateiRepository;
import repositories.RechnungAuftragRepository;
import tho.nill.datenlieferung.allgemein.Dateien;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;
import tho.nill.datenlieferung.senden.sftp.JSchWrapper;
import tho.nill.datenlieferung.senden.sftp.JSchWrapper.FileData;
import tho.nill.datenlieferung.senden.sftp.JschWrapperFabric;
import tho.nill.datenlieferung.senden.sftp.SftpConfiguration;
import tho.nill.datenlieferung.senden.sftp.SftpConnectionWrapper;
import tho.nill.datenlieferung.senden.sftp.SftpConnectionWrapperFabric;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;
import tho.nill.datenlieferung.sql.SqlQueryFlux;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SftpEinlesenTest implements Consumer<Object> {
	private String TARGET_SFTP_EINGELESEN;

	private String SFTP_REMOTE;

	@Autowired
	public PlatformTransactionManager transactionManager;

	@Autowired
	TemplateEngine templateEngine;

	@Autowired
	DataSource dataSource;

	@Autowired
	public RechnungAuftragRepository rechnungAuftragRepo;

	@Autowired
	public DatenlieferungRepository datenlieferungenRepo;

	@Autowired
	public DatenaustauschRepository datenaustauschRepo;

	@Autowired
	public EingeleseneDateiRepository eingeleseneDateiRepo;

	@Autowired
	public DatenlieferungProtokollRepository datenlieferungProtokollRepo;

	@Autowired
	public SftpConfiguration config;

	public SFtpServer server;

	private SftpConnectionWrapperFabric<?> wrapperFabrik;

	@Before
	public void init() throws IOException {

		TARGET_SFTP_EINGELESEN = Verzeichnisse.getTestVerz(this, "eingelesen");
		SFTP_REMOTE = Verzeichnisse.getTestVerz(this, "sftp-server");

		wrapperFabrik = new JschWrapperFabric();

		try {
			log.debug("port: " + config.PORT);
			server = new SFtpServer(config.BENUTZERNAME, config.PASSWORT, config.PORT);
			server.before();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		FreigabeDatenlieferungen f = new FreigabeDatenlieferungen(transactionManager, datenlieferungenRepo,
				datenlieferungProtokollRepo, eingeleseneDateiRepo, rechnungAuftragRepo);
		f.freigeben();
		Check.clearDb(datenlieferungenRepo, eingeleseneDateiRepo, datenaustauschRepo);
		Datenlieferung d = new Datenlieferung();
		d.setPhysDateiname("EAPO200");
		d.setDateigröße_nutzdaten(100);
		d.setDateigröße_übertragung(200);
		d.setErstellt(LocalDateTime.now());
		datenlieferungenRepo.save(d);

		Datenaustausch austausch = new Datenaustausch();
		austausch.setLoginNutzer(config.BENUTZERNAME);
		austausch.setLoginPasswort(config.PASSWORT);
		austausch.setHost(config.HOST);
		austausch.setHostVerzeichnis(SFTP_REMOTE);
		austausch.setPort(config.PORT);
		austausch.setVersenderIK(new IK(999999999));
		austausch.setRichtung(Richtung.EINGANG);
		austausch.setVerbindung(Verbindungsart.SFTP);
		austausch.setCodepage(StandardCharsets.UTF_8.name());
		datenaustauschRepo.saveAndFlush(austausch);
		Files.createDirectories(new File(SFTP_REMOTE).toPath());
	}

	@After
	public void deinit() {
		server.after();
	}

	@Test
	public void testDateienErzeugen() {
		TransactionTemplate template = new TransactionTemplate(transactionManager);
		final Consumer<Object> consumer = this;
		template.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					SqlQueryFlux
							.using(dataSource, "select DatenlieferungId from datenlieferung where bestätigt is null")
							.subscribe(consumer);

				} catch (Exception e) {
					log.error("Exception in " + this.getClass().getCanonicalName() + ".performService", e);
					fail();
				}

			}

		});
		try {
			sftpLesen();
			assertEquals(1, eingeleseneDateiRepo.count());
		} catch (Exception e) {
			log.error("Exception in " + this.getClass().getCanonicalName() + ".performService", e);
			fail();
		}

	}

	@Override
	public void accept(Object t) {
		try {
			Long id = (Long) ((Object[]) t)[0];
			Datenlieferung d = datenlieferungenRepo.getOne(id);
			if (d != null) {
				senden(erstellen(d));
			}
		} catch (Exception e) {
			throw new DatenlieferungException(e);
		}
	}

	public File erstellen(@NonNull Datenlieferung datenlieferung) throws FileNotFoundException, IOException {

		final Context context = new Context();
		context.setVariable("datei", datenlieferung.getPhysDateiname());
		context.setVariable("verschl", datenlieferung.getDateigröße_übertragung());
		context.setVariable("original", datenlieferung.getDateigröße_nutzdaten());
		context.setVariable("datum", JJJJMMTTHHMMSS(datenlieferung.getErstellt()));

		String body = templateEngine.process("testeinlesen.txt", context);
		File file = Dateien
				.createFile(Verzeichnisse.getTestDatei(this, "dateien", "" + datenlieferung.getDatenlieferungId()));
		try (FileOutputStream out = Dateien.createOutputStream(file)) {
			out.write(body.getBytes(StandardCharsets.UTF_8));
		}
		return file;
	}

	private String JJJJMMTTHHMMSS(LocalDateTime datum) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd:HHmmss");
		return datum.format(formatter);
	}

	private void senden(File datei) throws IOException {
		try (SftpConnectionWrapper<?> wrapper = wrapperFabrik.create(config.BENUTZERNAME, config.PASSWORT, config.HOST,
				config.PORT, "", SFTP_REMOTE)) {
			log.debug("Upload Name " + datei.getName());
			wrapper.uploadFile(datei.getAbsolutePath(), datei.getName());
			wrapper.close();
		}
	}

	private void sftpLesen() throws IOException {
		List<Datenaustausch> ld = datenaustauschRepo.getVerbindung(Richtung.EINGANG, Verbindungsart.SFTP);
		for (Datenaustausch austausch : ld) {
			sftpLesen(austausch);
		}
	}

	private void sftpLesen(Datenaustausch austausch) throws IOException {
		try (SftpConnectionWrapper<?> wrapper = wrapperFabrik.create(austausch.getLoginNutzer(),
				austausch.getLoginPasswort(), austausch.getHost(), austausch.getPort(), "",
				austausch.getHostVerzeichnis())) {
			List<FileData> fileDescriptions = ((JSchWrapper) wrapper).getFileDataList("."); // austausch.getHostVerzeichnis());
			for (FileData fileDescription : fileDescriptions) {
				sftpLesen(wrapper, austausch, fileDescription);
			}

			wrapper.close();
		}

	}

	private void sftpLesen(SftpConnectionWrapper<?> wrapper, Datenaustausch austausch, FileData fileDescription)
			throws IOException {
		log.debug("Download Name " + fileDescription.getName());
		Files.createDirectories(new File(TARGET_SFTP_EINGELESEN).toPath());
		String localFile = TARGET_SFTP_EINGELESEN + fileDescription.getName();
		String remoteFile = fileDescription.getParentPath() + "/" + fileDescription.getName();
		wrapper.downloadFile(remoteFile, localFile);
		String text = new String(Files.readAllBytes(Paths.get(localFile)), austausch.getCodepage());
		log.debug("Text " + text);
		EingeleseneDatei datei = new EingeleseneDatei();
		datei.setArt(Verbindungsart.SFTP);
		datei.setDaten(text);
		datei.setHost(austausch.getHost());
		datei.setHost(austausch.getHostVerzeichnis());
		datei.setErstellt(LocalDateTime.now());
		eingeleseneDateiRepo.saveAndFlush(datei);
		wrapper.deleteRemoteFile(remoteFile);
	}
}
