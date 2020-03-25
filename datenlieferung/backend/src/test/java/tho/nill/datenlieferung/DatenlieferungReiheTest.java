package tho.nill.datenlieferung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.security.Security;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import entities.Datenaustausch;
import entities.Datenlieferung;
import repositories.DatenaustauschRepository;
import repositories.DatenlieferungProtokollRepository;
import repositories.DatenlieferungRepository;
import repositories.VersenderKeyRepository;
import repositories.ZertifikatRepository;
import tho.nill.datenlieferung.allgemein.PathConfiguration;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.senden.email.EMailConfiguration;
import tho.nill.datenlieferung.senden.sftp.SftpConfiguration;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.MonatJahr;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;
import tho.nill.datenlieferung.zertifikate.ZertifikateLesen;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG)
@WithMockUser(username = "user", password = "password")
public class DatenlieferungReiheTest {

	@Autowired
	public DatenaustauschRepository datenaustauschRepo;

	@Autowired
	public DatenlieferungRepository datenlieferungenRepo;

	@Autowired
	private ZertifikatRepository zertifikatRepo;

	@Autowired
	private VersenderKeyRepository keyRepo;

	@Autowired
	private DatenlieferungProtokollRepository protokolle;

	@Autowired
	public EMailConfiguration emailConfig;

	@Autowired
	public SftpConfiguration sftpConfig;

	@Autowired
	public PathConfiguration pathConfig;

	@Autowired
	protected MockMvc mockMvc;

	public String serverVerz;

	public EMailServer emailServer;

	public SFtpServer sftpServer;

	public DatenlieferungReiheTest() {
		super();
		Security.addProvider(new BouncyCastleProvider());
	}

	@Before
	public void init() {
		try {
			serverVerz = Verzeichnisse.getTestVerz(this, "sftp-server-verz");
			Check.clearDb(zertifikatRepo, keyRepo, datenaustauschRepo, protokolle, datenlieferungenRepo);
			emailServer = new EMailServer(emailConfig.BENUTZERNAME, emailConfig.PASSWORT, emailConfig.PORT,
					emailConfig.HOST);
			emailServer.before();
			sftpServer = new SFtpServer(sftpConfig.BENUTZERNAME, sftpConfig.PASSWORT, sftpConfig.PORT);
			sftpServer.before();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@After
	public void deinit() {
		emailServer.after();
		sftpServer.after();
	}

	@Test
	public void requestErstellenUndSenden() throws Exception {
		Check.clearDirectory(pathConfig.dataPathOriginal);
		Check.clearDirectory(pathConfig.dataPathVerschl);

		Verzeichnisse verz = new Verzeichnisse(pathConfig.dataPathOriginal, pathConfig.dataPathVerschl);

		testDatenErzeugen();
		this.mockMvc.perform(post("/api/action/datenlieferung/reihe").param("monat", "1").param("jahr", "2019"))
				.andDo(print()).andExpect(status().isOk());
		assertEquals(0L, protokolle.count());
		List<Datenlieferung> liste = datenlieferungenRepo.findAll();
		assertTrue(liste.size() > 0);
		for (Datenlieferung d : liste) {
			assertEquals(AktionsArt.GESENDET, d.getLetzteAktion());
			assertNotNull(d.getGesendet());
			Check.fileExists(verz.getOrginalFile(d));
			Check.fileExists(verz.getAuftragsFile(d));
			Check.fileExists(verz.getVerschlFile(d));
		}
	}

	private Datenlieferung testDatenErzeugen() throws Exception {
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
		datenaustausch.setHost(sftpConfig.HOST);
		datenaustausch.setHostVerzeichnis(serverVerz);
		datenaustausch.setPort(sftpConfig.PORT);
		datenaustausch.setLoginNutzer(sftpConfig.BENUTZERNAME);
		datenaustausch.setLoginPasswort(sftpConfig.PASSWORT);
		datenaustauschRepo.saveAndFlush(datenaustausch);

		Datenlieferung datenlieferung = new Datenlieferung();
		datenlieferung.setVersenderIK(versenderIk);
		datenlieferung.setDatenAnnahmeIK(datenIk);
		datenlieferung.setDatenPrüfungsIK(vorprüfIk);

		datenlieferung.setDatenArt(DatenArt.PAR300ABRP);
		datenlieferung.setMj(new MonatJahr(1, 2019));
		datenlieferung.setTestKennzeichen("T");
		datenlieferung.setCdnummer(20);
		datenlieferung.setLetzteAktion(AktionsArt.ANGELEGT);

		datenlieferungenRepo.saveAndFlush(datenlieferung);

		ZertifikateLesen zl = new ZertifikateLesen(new File("src/test/resources/test-sha256.key").toPath(),
				zertifikatRepo, keyRepo);
		zl.erzeugeTestZertifikate(datenlieferung.getVersenderIK());
		zl.erzeugeTestZertifikate(datenlieferung.getDatenPrüfungsIK());

		return datenlieferung;
	}

}