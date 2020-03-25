package tho.nill.datenlieferung;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

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
import tho.nill.datenlieferung.allgemein.PathConfiguration;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.senden.cd.AlleCDBelegeAusgeben;
import tho.nill.datenlieferung.simpleAttributes.AdressArt;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.MonatJahr;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CDVersendenTest {

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

	@Before
	public void init() {
		Check.clearDb(datenaustauschRepo, adresseRepo, datenlieferungenRepo);
	}

	@Test
	public void testCD() throws IOException {
		Datenlieferung datenlieferung = testDatenErzeugen();

		AlleCDBelegeAusgeben allesAusgeben = new AlleCDBelegeAusgeben(templateEngine, datenaustauschRepo, adresseRepo,
				datenlieferungenRepo, pathConfig.dataPathOriginal, pathConfig.dataPathVerschl);
		Optional<DatenlieferungProtokoll> error = allesAusgeben.action(datenlieferung);
		prüfen(datenlieferung, error);
	}

	private void prüfen(Datenlieferung datenlieferung, Optional<DatenlieferungProtokoll> error) {
		if (error.isPresent()) {
			log.error(error.get().toString());
		}
		assertTrue(error.isEmpty());
		Verzeichnisse verz = new Verzeichnisse(pathConfig.dataPathOriginal, pathConfig.dataPathVerschl);
		File file = verz.getBrennerFile(datenlieferung.getCdnummer());
		Check.fileExists(file);
		file = verz.getCDBriefFile(datenlieferung);
		Check.fileExists(file);
		file = verz.getBegleitzettelFile(datenlieferung);
		Check.fileExists(file);
	}

	private Datenlieferung testDatenErzeugen() {
		IK datenIk = new IK(111111111);
		IK vorprüfIk = new IK(222222222);
		IK versenderIk = new IK(333333333);

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
		datenaustausch.setVerbindung(Verbindungsart.CD);
		datenaustausch.setRichtung(Richtung.AUSGANG);
		datenaustausch.setVersenderIK(versenderIk);
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

		datenlieferungenRepo.saveAndFlush(datenlieferung);
		return datenlieferung;
	}

}

/*
 * 
 */