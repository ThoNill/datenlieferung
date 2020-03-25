package tho.nill.datenlieferung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;

import entities.Adresse;
import entities.Datenaustausch;
import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;
import lombok.extern.slf4j.Slf4j;
import repositories.AdresseRepository;
import repositories.DatenaustauschRepository;
import repositories.DatenlieferungRepository;
import repositories.VersenderKeyRepository;
import repositories.ZertifikatRepository;
import tho.nill.datenlieferung.allgemein.Dateinamen;
import tho.nill.datenlieferung.allgemein.PathConfiguration;
import tho.nill.datenlieferung.auftragsdatei.ErzeugeAuftragsDatei;
import tho.nill.datenlieferung.originaldatei.ErzeugeTestDatendatei;
import tho.nill.datenlieferung.senden.email.EMailConfiguration;
import tho.nill.datenlieferung.senden.email.EMailSender;
import tho.nill.datenlieferung.simpleAttributes.AdressArt;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.MonatJahr;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;
import tho.nill.datenlieferung.zertifikate.VerschlüsselnSigneren;
import tho.nill.datenlieferung.zertifikate.ZertifikateLesen;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class EMailVersendenTest {

	@Autowired
	public PathConfiguration pathConfig;

	@Autowired
	TemplateEngine templateEngine;

	@Autowired
	public DatenaustauschRepository datenaustauschRepo;

	@Autowired
	public AdresseRepository adresseRepo;

	@Autowired
	public DatenlieferungRepository datenlieferungenRepo;

	@Autowired
	public ZertifikatRepository zertifikatRepo;

	@Autowired
	public VersenderKeyRepository keyRepo;

	@Autowired
	public EMailConfiguration config;

	public EMailVersendenTest() {
		Security.addProvider(new BouncyCastleProvider());
	}

	public EMailServer server;

	@Before
	public void init() {
		try {
			server = new EMailServer(config.BENUTZERNAME, config.PASSWORT, config.PORT, config.HOST);
			server.before();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		Check.clearDb(datenaustauschRepo, adresseRepo, datenlieferungenRepo, zertifikatRepo, keyRepo);
	}

	@After
	public void deinit() {
		server.after();
	}

	@Test
	public void testEmail() throws IOException {
		try {

			IK datenIk = new IK(111111111);
			IK vorprüfIk = new IK(320720409);
			IK versenderIk = new IK(333333333);

			Datenlieferung datenlieferung = erzeugeTestdaten(datenIk, vorprüfIk, versenderIk);

			ErzeugeTestDatendatei erzeuger = new ErzeugeTestDatendatei(pathConfig.dataPathOriginal,
					pathConfig.dataPathVerschl);
			erzeuger.action(datenlieferung);

			datenlieferungenRepo.saveAndFlush(datenlieferung);

			verschlüsseln(datenlieferung, versenderIk);

			log.info("vor Aufragsdatei");
			ErzeugeAuftragsDatei auftragsDateiErzeuger = new ErzeugeAuftragsDatei(pathConfig.dataPathOriginal,
					pathConfig.dataPathVerschl);
			auftragsDateiErzeuger.action(datenlieferung);
			log.info("nach Aufragsdatei");

			datenlieferungenRepo.saveAndFlush(datenlieferung);

			EMailSender emailSender = new EMailSender(templateEngine, datenaustauschRepo, adresseRepo,
					pathConfig.dataPathOriginal, pathConfig.dataPathVerschl);

			log.info("vor EMail");
			Optional<DatenlieferungProtokoll> error = emailSender.action(datenlieferung);

			prüfen(error);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void prüfen(Optional<DatenlieferungProtokoll> error) throws IOException, MessagingException {
		if (error.isPresent()) {
			log.info("Fehler:" + error.get().toString());
		}
		log.info("nach EMail");
		assertTrue(error.isEmpty());

		MimeMessage[] messages = server.getMessages();
		assertNotNull(messages);
		assertEquals(1, messages.length);
		MimeMultipart mp = (MimeMultipart) messages[0].getContent();
		assertEquals(3, mp.getCount());
		BodyPart bp = mp.getBodyPart(0);
		MimeMultipart mp2 = (MimeMultipart) bp.getContent();
		log.info(mp2.getBodyPart(0).getContent().toString());
	}

	private Datenlieferung erzeugeTestdaten(IK datenIk, IK vorprüfIk, IK versenderIk) {
		Adresse datenAdresse = new Adresse();
		datenAdresse.setIk(datenIk);
		datenAdresse.setArt(AdressArt.FIRMA);
		datenAdresse.setFirma("Datenannahme");
		datenAdresse.setAnsprechpartner("Herr Datenannahme");
		adresseRepo.saveAndFlush(datenAdresse);

		Adresse datenAdresse2 = new Adresse();
		datenAdresse2.setIk(datenIk);
		datenAdresse2.setArt(AdressArt.DATENLIEFERUNG);
		datenAdresse2.setFirma("Datenannahme2");
		datenAdresse2.setAnsprechpartner("Herr Datenannahme2");
		adresseRepo.saveAndFlush(datenAdresse2);

		Adresse vorprüfAdresse = new Adresse();
		vorprüfAdresse.setIk(vorprüfIk);
		vorprüfAdresse.setArt(AdressArt.FIRMA);
		vorprüfAdresse.setFirma("Prüfstelle");
		vorprüfAdresse.setAnsprechpartner("Herr Prüfer");
		adresseRepo.saveAndFlush(vorprüfAdresse);

		Adresse vorprüfAdresse2 = new Adresse();
		vorprüfAdresse2.setIk(vorprüfIk);
		vorprüfAdresse2.setArt(AdressArt.DATENLIEFERUNG);
		vorprüfAdresse2.setFirma("Prüfstelle2");
		vorprüfAdresse2.setAnsprechpartner("Herr Prüfer2");
		adresseRepo.saveAndFlush(vorprüfAdresse2);

		Adresse versenderAdresse = new Adresse();
		versenderAdresse.setIk(versenderIk);
		versenderAdresse.setArt(AdressArt.FIRMA);
		versenderAdresse.setFirma("Versender");
		versenderAdresse.setAnsprechpartner("Herr Versender");
		adresseRepo.saveAndFlush(versenderAdresse);

		Adresse versenderAdresse2 = new Adresse();
		versenderAdresse2.setIk(versenderIk);
		versenderAdresse2.setArt(AdressArt.DATENLIEFERUNG);
		versenderAdresse2.setFirma("Versender2");
		versenderAdresse2.setAnsprechpartner("Herr Versender2");
		adresseRepo.saveAndFlush(versenderAdresse2);

		Datenaustausch datenaustausch = new Datenaustausch();
		datenaustausch.setDatenAnnahmeIK(datenIk);
		datenaustausch.setDatenPrüfungsIK(new IK(0));
		datenaustausch.setDatenArt(DatenArt.PAR300ABRP);
		datenaustausch.setVerbindung(Verbindungsart.EMAIL);
		datenaustausch.setRichtung(Richtung.AUSGANG);
		datenaustausch.setVersenderIK(versenderIk);
		datenaustausch.setHost(config.HOST);
		datenaustausch.setLoginNutzer(config.BENUTZERNAME);
		datenaustausch.setLoginPasswort(config.PASSWORT);
		datenaustausch.setPort(config.PORT);
		datenaustausch.setEmailTo(config.TO);
		datenaustausch.setEmailFrom(config.FROM);
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
		datenlieferung.setLogDateiname(Dateinamen.berechneLogDateinamen(datenlieferung));
		datenlieferung.setPhysDateiname(Dateinamen.berechnePhysDateinamen(datenlieferung));
		datenlieferungenRepo.saveAndFlush(datenlieferung);
		return datenlieferung;
	}

	public void verschlüsseln(Datenlieferung d, IK versenderIK) {
		try {
			erzeugeZertifikate(versenderIK);

			d.setPhysDateiname(Dateinamen.berechnePhysDateinamen(d));
			datenlieferungenRepo.saveAndFlush(d);
			log.info("vor Verschlüsselung");
			VerschlüsselnSigneren vs = new VerschlüsselnSigneren(keyRepo, zertifikatRepo, pathConfig.dataPathOriginal,
					pathConfig.dataPathVerschl);
			vs.action(d);
			log.info("nach Verschlüsselung");
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	private void erzeugeZertifikate(IK versenderIK)
			throws NoSuchAlgorithmException, Exception, IOException, CertificateEncodingException {

		ZertifikateLesen lesen = new ZertifikateLesen(new File("src/test/resources/test-sha256.key").toPath(),
				zertifikatRepo, keyRepo);
		lesen.einlesen();
		lesen.erzeugeTestZertifikate(versenderIK);
	}
}

/*
 * 
 */