package tho.nill.datenlieferung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import entities.Datenlieferung;
import entities.RechnungAuftrag;
import lombok.extern.slf4j.Slf4j;
import repositories.DateiNummerRepository;
import repositories.RechnungAuftragRepository;
import tho.nill.datenlieferung.auftragsdatei.AuftragsFelder;
import tho.nill.datenlieferung.nummernvergabe.Nummervergabe;
import tho.nill.datenlieferung.simpleAttributes.DateiNummerArt;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class NummernHochzählenTest {

	@Autowired
	public RechnungAuftragRepository repo;

	@Autowired
	public DateiNummerRepository nummern;

	@Test
	public void contextLoads() {
	}

	@Test
	public void auftragsRepository() {
		insertAuftrag();
		assertEquals(1, repo.count());
	}

	@Before
	public void init() {
		Check.clearDb(repo, nummern);
	}

	@Transactional
	private void insertAuftrag() {
		RechnungAuftrag auftrag = new RechnungAuftrag();
		auftrag.setDatenAnnahmeIK(new IK(123456789));
		repo.saveAndFlush(auftrag);
	}

	@Test
	@Transactional
	public void hochzählen() {
		Nummervergabe vergabe = new Nummervergabe(nummern);
		IK versender = new IK(123456789);
		try {
			assertEquals(1, vergabe.getNumber(DateiNummerArt.TRANSFERNUMMER_DATENANNAHME, DatenArt.PAR300ABRP,
					new IK(1234567), versender, 2019));
			assertEquals(2, vergabe.getNumber(DateiNummerArt.TRANSFERNUMMER_DATENANNAHME, DatenArt.PAR300ABRP,
					new IK(1234567), versender, 2019));
			assertEquals(3, vergabe.getNumber(DateiNummerArt.TRANSFERNUMMER_DATENANNAHME, DatenArt.PAR300ABRP,
					new IK(1234567), versender, 2019));
			assertEquals(1, vergabe.getNumber(DateiNummerArt.TRANSFERNUMMER_DATENANNAHME, DatenArt.PAR300ABRP,
					new IK(1234568), versender, 2019));
			assertEquals(2, vergabe.getNumber(DateiNummerArt.TRANSFERNUMMER_DATENANNAHME, DatenArt.PAR300ABRP,
					new IK(1234568), versender, 2019));
			assertEquals(3, vergabe.getNumber(DateiNummerArt.TRANSFERNUMMER_DATENANNAHME, DatenArt.PAR300ABRP,
					new IK(1234568), versender, 2019));
		} catch (Exception ex) {
			fail();
		}
	}

	@Test
	@Transactional
	public void nummerVergabe() {
		Datenlieferung datenlieferung = createAbrp();

		Nummervergabe vergabe = new Nummervergabe(nummern);

		vergabe.nummerieren(datenlieferung);

		checkNummervergabe(datenlieferung, 1, 1, 1, 0);

		datenlieferung = createAbrp();
		vergabe.nummerieren(datenlieferung);

		checkNummervergabe(datenlieferung, 2, 2, 2, 0);
	}

	private void checkNummervergabe(Datenlieferung datenlieferung, int transfernerDatenannahme,
			int transfernerVorprüfung, int dateinummer, int korrnummer) {
		assertEquals(transfernerDatenannahme, datenlieferung.getTransfernummer_datenannahme());
		assertEquals(transfernerVorprüfung, datenlieferung.getTransfernummer_vorprüfung());
		assertEquals(dateinummer, datenlieferung.getDateinummer());
		assertEquals(korrnummer, datenlieferung.getKorrekturnummer());
	}

	private Datenlieferung createAbrp() {
		Datenlieferung datenlieferung = new Datenlieferung();
		datenlieferung.setLieferJahr(2019);
		datenlieferung.setVersenderIK(new IK(123456789));
		datenlieferung.setDatenPrüfungsIK(new IK(223456789));
		datenlieferung.setDatenAnnahmeIK(new IK(323456789));
		datenlieferung.setDatenArt(DatenArt.PAR300ABRP);
		return datenlieferung;
	}

	@Test
	public void auftragsdatei() {
		try {
			Datenlieferung d = new Datenlieferung();
			d.setVersenderIK(new IK(123456789));
			d.setDatenAnnahmeIK(new IK(111111111));
			d.setDatenPrüfungsIK(new IK(222222222));
			d.setTransfernummer_datenannahme(123);
			d.setTransfernummer_vorprüfung(12);
			d.setDatenArt(DatenArt.PAR300ABRP);
			d.setDateinummer(8);
			d.setLogDateiname("EAOP00123");
			d.setDateigröße_nutzdaten(4324);
			d.setDateigröße_übertragung(543210);
			d.setErstellt(LocalDateTime.now());
			byte[] werte = AuftragsFelder.getWerte(d);
			String s = new String(werte);
			log.debug(s);
			assertEquals(348, werte.length);

		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

}
