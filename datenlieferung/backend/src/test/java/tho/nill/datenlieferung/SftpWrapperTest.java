package tho.nill.datenlieferung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.vfs2.FileObject;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;

import entities.Datenaustausch;
import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import repositories.DatenaustauschRepository;
import repositories.DatenlieferungProtokollRepository;
import repositories.DatenlieferungRepository;
import repositories.EingeleseneDateiRepository;
import repositories.RechnungAuftragRepository;
import repositories.VersenderKeyRepository;
import repositories.ZertifikatRepository;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.auftragsdatei.ErzeugeAuftragsDatei;
import tho.nill.datenlieferung.originaldatei.ErzeugeTestDatendatei;
import tho.nill.datenlieferung.senden.sftp.JSchWrapper;
import tho.nill.datenlieferung.senden.sftp.JSchWrapper.FileData;
import tho.nill.datenlieferung.senden.sftp.JschWrapperFabric;
import tho.nill.datenlieferung.senden.sftp.SftpConfiguration;
import tho.nill.datenlieferung.senden.sftp.SftpSender;
import tho.nill.datenlieferung.senden.sftp.SftpVFSWrapper;
import tho.nill.datenlieferung.senden.sftp.SshjWrapper;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.MonatJahr;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;
import tho.nill.datenlieferung.zertifikate.PrivPubKeys;
import tho.nill.datenlieferung.zertifikate.VerschlüsselnSigneren;
import tho.nill.datenlieferung.zertifikate.ZertifikateLesen;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SftpWrapperTest {

	@Autowired
	public PlatformTransactionManager transactionManager;

	@Autowired
	public DatenaustauschRepository datenaustauschRepo;

	// wegen Cleanup der Datenbank
	@Autowired
	public RechnungAuftragRepository rechnungAuftragRepo;

	@Autowired
	public DatenlieferungRepository datenlieferungenRepo;

	@Autowired
	public VersenderKeyRepository keyRepo;

	@Autowired
	public ZertifikatRepository zertifikatRepo;

	@Autowired
	public DatenlieferungProtokollRepository datenlieferungProtokollRepo;

	@Autowired
	public EingeleseneDateiRepository eingeleseneDateiRepo;

	@Autowired
	public SftpConfiguration config;

	public SFtpServer server;

	public SftpWrapperTest() {
		Security.addProvider(new BouncyCastleProvider());
	}

	@Before
	public void init() {
		try {
			server = new SFtpServer(config.BENUTZERNAME, config.PASSWORT, config.PORT);
			server.before();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		FreigabeDatenlieferungen f = new FreigabeDatenlieferungen(transactionManager, datenlieferungenRepo,
				datenlieferungProtokollRepo, eingeleseneDateiRepo, rechnungAuftragRepo);
		f.freigeben();
		Check.clearDb(datenaustauschRepo, rechnungAuftragRepo, datenlieferungenRepo, zertifikatRepo, keyRepo);
	}

	@After
	public void deinit() {
		server.after();
	}

	@Test
	public void testJschWrapper() throws IOException {
		String remoteSftpSubdir = Verzeichnisse.getTestVerz(this, "testJschWrapper/sftp-server");
		String localSftpSubdir = Verzeichnisse.getTestVerz(this, "testJschWrapper/sftp-local");

		String localFilePathB = localSftpSubdir + "/b.txt";
		String remoteFilePathA = "a.txt";
		String remoteFilePathC = "c.txt";
		String meinText = "äöüß\u20AC"; // \u20AC = Euro-Zeichen
		String charEncoding = "UTF-8";
		(new File(localSftpSubdir)).mkdirs();
		(new File(remoteSftpSubdir)).mkdirs();

		try (FileOutputStream is = new FileOutputStream(localFilePathB)) {
			is.write(meinText.getBytes(charEncoding));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try (JSchWrapper sftpWrapper = new JSchWrapper(config.BENUTZERNAME, config.PASSWORT, config.HOST, config.PORT,
				".", remoteSftpSubdir)) {
			log.info("vor dem hochladen");
			sftpWrapper.uploadFile(localFilePathB, remoteFilePathC);
			log.info("nach dem hochladen");
			prüfeDateienAnzahl(sftpWrapper, "c.txt");
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}

	}

	@Test
	public void testJSchWrapperUploadDownload() throws IOException {
		String remoteSftpSubdir = Verzeichnisse.getTestVerz(this, "testJSchWrapperUploadDownload/sftp-server");
		String localSftpSubdir = Verzeichnisse.getTestVerz(this, "testJSchWrapperUploadDownload/sftp-local");

		String localFilePathB = localSftpSubdir + "/b.txt";
		String remoteFileA = "a.txt";
		String remoteFileC = "c.txt";
		String meinText = "äöüß\u20AC"; // \u20AC = Euro-Zeichen
		String charEncoding = "UTF-8";
		(new File(localSftpSubdir)).mkdirs();
		(new File(remoteSftpSubdir)).mkdirs();

		try (JSchWrapper sftpWrapper = new JSchWrapper(config.BENUTZERNAME, config.PASSWORT, config.HOST, config.PORT,
				localSftpSubdir, remoteSftpSubdir)) {
			log.info("LocalActualDir: " + sftpWrapper.getLocalActualDir());

			try (ByteArrayInputStream is = new ByteArrayInputStream(meinText.getBytes(charEncoding))) {
				sftpWrapper.createRemoteFile(is, remoteFileA);
			}
			sftpWrapper.downloadFile(remoteFileA, localFilePathB);
			sftpWrapper.uploadFile(localFilePathB, remoteFileC);

			prüfeDateienAnzahl(sftpWrapper, remoteFileA);
			prüfeDateienAnzahl(sftpWrapper, remoteFileC);
			Check.fileExists(new File(localFilePathB), meinText.length());
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}

	}

	private void prüfeDateienAnzahl(JSchWrapper sftpWrapper, String filename) throws IOException {
		List<FileData> fileDescriptions = sftpWrapper.getFileDataList(".");
		int anz = 0;
		for (FileData fileDescription : fileDescriptions) {
			log.debug("Parent: " + fileDescription.getParentPath());
			log.debug("JSchWrapper Datei: " + fileDescription.getName());
			if (filename.equals(fileDescription.getName())) {
				anz++;
			}
		}
		assertEquals(1, anz);
	}

	@Test
	public void testSshjWrapper() throws IOException {
		String remoteSftpSubdir = Verzeichnisse.getTestVerz(this, "testSshjWrapper/sftp-server");
		String localSftpSubdir = Verzeichnisse.getTestVerz(this, "testSshjWrapper/sftp-local");

		String localFilePathB = localSftpSubdir + "/b.txt";
		String remoteFileA = "a.txt";
		String remoteFileC = "c.txt";
		String meinText = "äöüß\u20AC"; // \u20AC = Euro-Zeichen
		String charEncoding = "UTF-8";
		(new File(localSftpSubdir)).mkdirs();
		(new File(remoteSftpSubdir)).mkdirs();

		try (FileOutputStream is = new FileOutputStream(localFilePathB)) {
			is.write(meinText.getBytes(charEncoding));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try (SshjWrapper sftpWrapper = new SshjWrapper(config.BENUTZERNAME, config.PASSWORT, config.HOST, config.PORT,
				".", remoteSftpSubdir)) {
			log.info("vor dem hochladen");
			sftpWrapper.uploadFile(localFilePathB, remoteFileC);
			log.info("nach dem hochladen");
			prüfeDateienAnzahl(sftpWrapper, remoteFileC);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}

	}

	private void prüfeDateienAnzahl(SshjWrapper sftpWrapper, String filename) throws IOException {
		List<RemoteResourceInfo> fileDescriptions = sftpWrapper.getFileDataList("");
		int anz = 0;
		for (RemoteResourceInfo fileDescription : fileDescriptions) {
			log.debug("SshjWrapper Datei: " + fileDescription.getName());
			if (filename.equals(fileDescription.getName())) {
				anz++;
			}
		}
		assertEquals(1, anz);
	}

	@Test
	public void testSftpVFSWrapper() throws IOException, NoSuchAlgorithmException {
		String remoteSftpSubdir = Verzeichnisse.getTestVerz(this, "testSftpVFSWrapper/sftp-server");
		String localSftpSubdir = Verzeichnisse.getTestVerz(this, "testSftpVFSWrapper/sftp-local");

		String localFilePathB = localSftpSubdir + "/b.txt";
		String remoteFileA = "a.txt";
		String remoteFileC = "c.txt";
		String meinText = "äöüß\u20AC"; // \u20AC = Euro-Zeichen
		String charEncoding = "UTF-8";
		(new File(localSftpSubdir)).mkdirs();
		(new File(remoteSftpSubdir)).mkdirs();

		try (FileOutputStream is = new FileOutputStream(localFilePathB)) {
			is.write(meinText.getBytes(charEncoding));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		String keysDir = "src/test/resources/";
		keysDir = ".";
		keysDir = erzeugeKeys();
		// String keysDir = erzeugeKeys();

		try (SftpVFSWrapper sftpWrapper = new SftpVFSWrapper(config.BENUTZERNAME, config.PASSWORT, config.HOST,
				config.PORT, new File(".").getAbsolutePath(), remoteSftpSubdir, keysDir)) {
			log.info("vor dem hochladen");
			sftpWrapper.uploadFile(localFilePathB, remoteFileC);
			log.info("nach dem hochladen");
			// prüfeDateienAnzahl(sftpWrapper, remoteFileC);
			assertTrue(new File(remoteSftpSubdir + "/c.txt").exists());
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}

	}

	private String erzeugeKeys() throws NoSuchAlgorithmException, IOException {
		String keysDir = Verzeichnisse.getTestVerz(this, "testSftpVFSWrapper/keys");
		PrivPubKeys keys = new PrivPubKeys();
		KeyPair kp = keys.generateKeyPair();
		keys.saveAsPEM(kp, keysDir + SftpVFSWrapper.KEYNAME);
		// keys.saveKeys(kp, keysDir + "/id_client");
		return keysDir;
	}

	/** Geht nicht, da vom VFS nicht implementiert!!! */
	private void prüfeDateienAnzahl(SftpVFSWrapper sftpWrapper, String filename) throws IOException {
		List<FileObject> fileDescriptions = sftpWrapper.getFileDataList("");
		int anz = 0;
		for (FileObject fileDescription : fileDescriptions) {
			log.debug("SftpVFSWrapper Datei: " + fileDescription.getName());
			if (filename.equals(fileDescription.getName())) {
				anz++;
			}
		}
		assertEquals(1, anz);
	}

	// Siehe auch EMailVersenderTest
	@Test
	public void testSftpSender() throws IOException {
		try {
			String localSftpSubdirOrig = Verzeichnisse.getTestVerz(this, "testSftpSender/sftp-original");
			String localSftpSubdirVerschl = Verzeichnisse.getTestVerz(this, "testSftpSender/sftp-local");
			String remoteSftpSubdir = Verzeichnisse.getTestVerz(this, "testSftpSender/sftp-remote");

			Datenlieferung datenlieferung = testDatenErzeugen(remoteSftpSubdir);

			ErzeugeTestDatendatei erzeugen = new ErzeugeTestDatendatei(localSftpSubdirOrig, localSftpSubdirVerschl);
			Optional<DatenlieferungProtokoll> error = erzeugen.action(datenlieferung);
			if (error.isPresent()) {
				log.info(error.get().toString());
			}

			VerschlüsselnSigneren verschlüsseln = new VerschlüsselnSigneren(keyRepo, zertifikatRepo,
					localSftpSubdirOrig, localSftpSubdirVerschl);

			error = verschlüsseln.action(datenlieferung);
			if (error.isPresent()) {
				log.info(error.get().toString());
			}

			ErzeugeAuftragsDatei auftragsdatei = new ErzeugeAuftragsDatei(localSftpSubdirOrig, localSftpSubdirVerschl);
			error = auftragsdatei.action(datenlieferung);
			if (error.isPresent()) {
				log.info(error.get().toString());
			}

			JschWrapperFabric wrapperFabric = new JschWrapperFabric();
			SftpSender sender = new SftpSender(datenaustauschRepo, wrapperFabric, localSftpSubdirOrig,
					localSftpSubdirVerschl);
			error = sender.action(datenlieferung);
			if (error.isPresent()) {
				log.info(error.get().toString());
			}
			assertTrue(error.isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private Datenlieferung testDatenErzeugen(String serverVerzeichnis) throws Exception {
		IK datenIk = new IK(111111111);
		IK vorprüfIk = new IK(222222222);
		IK versenderIk = new IK(333333333);

		Datenaustausch datenaustausch = new Datenaustausch();
		datenaustausch.setDatenAnnahmeIK(datenIk);
		datenaustausch.setDatenPrüfungsIK(new IK(0));
		datenaustausch.setDatenArt(DatenArt.PAR300ABRP);
		datenaustausch.setVerbindung(Verbindungsart.SFTP);
		datenaustausch.setRichtung(Richtung.AUSGANG);
		datenaustausch.setVersenderIK(versenderIk);
		datenaustausch.setHost(config.HOST);
		datenaustausch.setHostVerzeichnis(serverVerzeichnis);
		datenaustausch.setPort(config.PORT);
		datenaustausch.setLoginNutzer(config.BENUTZERNAME);
		datenaustausch.setLoginPasswort(config.PASSWORT);
		datenaustauschRepo.saveAndFlush(datenaustausch);

		Datenlieferung datenlieferung = new Datenlieferung();
		datenlieferung.setVersenderIK(versenderIk);
		datenlieferung.setDatenAnnahmeIK(datenIk);
		datenlieferung.setDatenPrüfungsIK(vorprüfIk);

		datenlieferung.setDatenArt(DatenArt.PAR300ABRP);
		datenlieferung.setCdnummer(200);
		datenlieferung.setDateinummer(100);
		datenlieferung.setTransfernummer_datenannahme(230);
		datenlieferung.setTransfernummer_vorprüfung(110);
		datenlieferung.setDateigröße_nutzdaten(2000);
		datenlieferung.setDateigröße_übertragung(2500);
		datenlieferung.setErstellt(LocalDateTime.now());
		datenlieferung.setGesendet(LocalDateTime.now());
		datenlieferung.setMj(new MonatJahr(1, 2019));
		datenlieferung.setTestKennzeichen("T");

		datenlieferungenRepo.saveAndFlush(datenlieferung);

		ZertifikateLesen zl = new ZertifikateLesen(new File("src/test/resources/test-sha256.key").toPath(),
				zertifikatRepo, keyRepo);
		zl.erzeugeTestZertifikate(datenlieferung.getVersenderIK());
		zl.erzeugeTestZertifikate(datenlieferung.getDatenPrüfungsIK());

		return datenlieferung;
	}

}

/*
 * 
 */