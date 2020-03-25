package tho.nill.datenlieferung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.util.Optional;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;
import repositories.DateiNummerRepository;
import repositories.DatenlieferungRepository;
import repositories.RechnungAuftragRepository;
import repositories.VersenderKeyRepository;
import repositories.ZertifikatRepository;
import tho.nill.datenlieferung.allgemein.Dateinamen;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.originaldatei.ErzeugeTestDatendatei;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.zertifikate.VerschlüsselnSigneren;
import tho.nill.datenlieferung.zertifikate.ZertifikateLesen;

@RunWith(SpringRunner.class)
@SpringBootTest

public class DatenlieferungTest {

	@Autowired
	public RechnungAuftragRepository repo;

	@Autowired
	public DateiNummerRepository nummern;

	@Autowired
	public ZertifikatRepository zertifikatRepo;

	@Autowired
	public VersenderKeyRepository keyRepo;

	@Autowired
	public DatenlieferungRepository datenlieferungen;

	@Value("${daten.path.original}")
	private String datenPathOriginal;
	@Value("${daten.path.verschl}")
	private String datenPathVerschl;

	public DatenlieferungTest() {
		super();
		Security.addProvider(new BouncyCastleProvider());
	}

	@Before
	public void init() {
		Check.clearDb(repo, nummern, zertifikatRepo, keyRepo, datenlieferungen);
	}

	@Test
	public void datenErstellen() {
		Datenlieferung d = new Datenlieferung();
		d.setDatenArt(DatenArt.PAR300ABRP);
		d.setTransfernummer_datenannahme(345);
		d.setTestKennzeichen("T");
		d.setDateinummer(89);
		berechneDateinamen(d);
		datenlieferungen.saveAndFlush(d);

		File file = erzeugeTestDatei(d);

		assertEquals(1, datenlieferungen.count());
		Check.fileExists(file);
	}

	public File getOrginalFile(Datenlieferung d) {
		return Verzeichnisse
				.createDirectorys(new File(datenPathOriginal + File.separatorChar + d.getDatenlieferungId()));
	}

	private File erzeugeTestDatei(Datenlieferung d) {
		File file = getOrginalFile(d);

		ErzeugeTestDatendatei aktion = new ErzeugeTestDatendatei(datenPathOriginal, datenPathVerschl);
		aktion.action(d);
		datenlieferungen.saveAndFlush(d);
		return file;
	}

	private void berechneDateinamen(Datenlieferung d) {
		d.setLogDateiname(Dateinamen.berechneLogDateinamen(d));
		d.setPhysDateiname(Dateinamen.berechnePhysDateinamen(d));
	}

	@Test
	public void verschlüsseln() {
		try {
			Datenlieferung d = erzeugeTestdaten();

			File file = erzeugeTestDatei(d);
			assertTrue(file.exists());
			VerschlüsselnSigneren vs = new VerschlüsselnSigneren(keyRepo, zertifikatRepo, datenPathOriginal,
					datenPathOriginal);
			Optional<DatenlieferungProtokoll> protokoll = vs.action(d);
			assertTrue(protokoll.isEmpty());
			Check.fileExists(vs.getOrginalFile(d));
			Check.fileExists(vs.getVerschlFile(d));
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	private Datenlieferung erzeugeTestdaten()
			throws NoSuchAlgorithmException, Exception, IOException, CertificateEncodingException {
		ZertifikateLesen lesen = new ZertifikateLesen(new File("src/test/resources/test-sha256.key").toPath(),
				zertifikatRepo, keyRepo);
		lesen.einlesen();
		IK versenderIK = new IK(999999999);
		lesen.erzeugeTestZertifikate(versenderIK);

		Datenlieferung d = new Datenlieferung();
		d.setVersenderIK(versenderIK);
		d.setDatenPrüfungsIK(new IK(320720409));
		d.setDatenArt(DatenArt.PAR300ABRP);
		d.setTransfernummer_datenannahme(346);
		d.setTestKennzeichen("T");
		d.setDateinummer(90);
		berechneDateinamen(d);
		datenlieferungen.saveAndFlush(d);
		return d;
	}
}
