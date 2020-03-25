package tho.nill.datenlieferung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import entities.Datenaustausch;
import entities.Datenlieferung;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import repositories.DatenaustauschRepository;
import repositories.DatenlieferungRepository;
import repositories.EingeleseneDateiRepository;
import tho.nill.datenlieferung.allgemein.Dateien;
import tho.nill.datenlieferung.allgemein.DummyVoid;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.einlesen.email.Pop3Configuration;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;
import tho.nill.datenlieferung.sql.SqlQueryFlux;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RückmeldungTest implements Consumer<Object> {
	@Autowired
	public PlatformTransactionManager transactionManager;

	@Autowired
	public DatenlieferungRepository datenlieferungenRepo;

	@Autowired
	public EingeleseneDateiRepository eingeleseneDateiRepo;

	@Autowired
	public DatenaustauschRepository datenaustauschRepo;

	@Autowired
	public Pop3Configuration config;

	@Autowired
	public RückmeldungenService rückmeldungenService;

	public Pop3Server server;

	@Autowired
	TemplateEngine templateEngine;

	@Autowired
	DataSource dataSource;

	@Before
	public void init() {
		try {
			server = new Pop3Server(config.BENUTZERNAME, config.PASSWORT, config.TO);
			server.before();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		Check.clearDb(eingeleseneDateiRepo, datenlieferungenRepo, datenaustauschRepo);
		IK datenannahmeIK = new IK(999999999);
		IK versenderIK = new IK(888888888);
		DatenArt art = DatenArt.PAR300ABRP;
		Datenlieferung d = new Datenlieferung();
		d.setDatenAnnahmeIK(datenannahmeIK);
		d.setVersenderIK(versenderIK);
		d.setDatenArt(art);
		d.setPhysDateiname("EAPO200");
		d.setDateigröße_nutzdaten(100);
		d.setDateigröße_übertragung(200);
		d.setErstellt(LocalDateTime.now());
		d.setGesendet(LocalDateTime.now());

		log.debug("Datenlieferung Erstellt: " + d.getErstellt());
		datenlieferungenRepo.save(d);

		Datenaustausch da = new Datenaustausch();
		da.setRichtung(Richtung.EINGANG);
		da.setDatenAnnahmeIK(datenannahmeIK);
		da.setVersenderIK(versenderIK);
		da.setDatenArt(art);
		da.setEmailFrom("test@quelle");
		da.setVerbindung(Verbindungsart.EMAIL);
		datenaustauschRepo.saveAndFlush(da);
	}

	@After
	public void deinit() {
		server.after();
	}

	@Test
	public void rückmelden() {
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
				}

			}

		});
		try {
			rückmeldungenService.service(DummyVoid.VOID);
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
				erstellen(d);
				senden(d);
			}
		} catch (Exception e) {
			throw new DatenlieferungException(e);
		}
	}

	public void erstellen(@NonNull Datenlieferung datenlieferung) throws FileNotFoundException, IOException {

		final Context context = new Context();
		context.setVariable("datei", datenlieferung.getPhysDateiname());
		context.setVariable("verschl", datenlieferung.getDateigröße_übertragung());
		context.setVariable("original", datenlieferung.getDateigröße_nutzdaten());
		context.setVariable("datum", JJJJMMTTHHMMSS(datenlieferung.getErstellt()));

		String body = templateEngine.process("testeinlesen.txt", context);
		File file = Dateien
				.createFile(Verzeichnisse.getTestDatei(this, "einlesen", "" + datenlieferung.getDatenlieferungId()));
		try (FileOutputStream out = Dateien.createOutputStream(file)) {
			out.write(body.getBytes(StandardCharsets.UTF_8));
		}
	}

	private void senden(Datenlieferung datenlieferung) throws MessagingException {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(config.HOST);
		sender.setUsername(config.BENUTZERNAME);
		sender.setPassword(config.PASSWORT);
		sender.setPort(25);

		MimeMessage message = sender.createMimeMessage();

//use the true flag to indicate you need a multipart message
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setTo(config.TO);
		helper.setFrom(config.FROM);
		helper.setSentDate(java.sql.Timestamp.valueOf(datenlieferung.getErstellt()));
		helper.setSubject("Testversand");

		final Context context = new Context();
		context.setVariable("datei", datenlieferung.getPhysDateiname());
		context.setVariable("verschl", datenlieferung.getDateigröße_übertragung());
		context.setVariable("original", datenlieferung.getDateigröße_nutzdaten());
		context.setVariable("datum", JJJJMMTTHHMMSS(datenlieferung.getErstellt()));

		String body = templateEngine.process("testeinlesen.txt", context);

		helper.setText(body);
		sender.send(message);

		log.debug("Server count= " + server.getMessages().length);
	}

	private String JJJJMMTTHHMMSS(LocalDateTime datum) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd:HHmmss");
		return datum.format(formatter);
	}

}
