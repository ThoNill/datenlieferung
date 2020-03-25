package tho.nill.datenlieferung;

import static org.junit.Assert.fail;

import java.io.File;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;
import repositories.DateiNummerRepository;
import repositories.DatenlieferungRepository;
import repositories.RechnungAuftragRepository;
import repositories.VersenderKeyRepository;
import repositories.ZertifikatRepository;
import tho.nill.datenlieferung.allgemein.PathConfiguration;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.zertifikate.Certificates;
import tho.nill.datenlieferung.zertifikate.PrivPubKeys;
import tho.nill.datenlieferung.zertifikate.VerschlüsselnSigneren;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class EncrypSignTest {

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

	@Autowired
	public PathConfiguration pathConfig;

	PrivPubKeys ppk;
	Certificates certs;

	KeyPair issuerKp;
	KeyPair subscriberKp;
	X509Certificate cert;

	File signiert;
	File verschlüsselt1;
	File verschlüsselt2;

	public EncrypSignTest() {
		Security.addProvider(new BouncyCastleProvider());
	}

	@Before
	public void init() {
		try {
			signiert = new File(Verzeichnisse.getTestVerz(this, "signiert") + "/signiert");
			verschlüsselt1 = new File(Verzeichnisse.getTestVerz(this, "verschluesselt") + "/verschluesselt");
			verschlüsselt2 = new File(Verzeichnisse.getTestVerz(this, "signiertUndVerschluesselt") + "/verschluesselt");

			Check.clearDb(repo, nummern, zertifikatRepo, keyRepo);
			this.ppk = new PrivPubKeys();
			this.certs = new Certificates(zertifikatRepo, keyRepo);
			issuerKp = generateKeyPair();
			subscriberKp = generateKeyPair();

			cert = Certificates.makeCertificate(subscriberKp,
					"CN=Herr Thomas Nill,OU=IK999999999,OU=ThomasNill,O=ITSG TrustCenter fuer sonstige Leistungserbringer,C=DE",
					issuerKp,
					"CN=Herr Thomas Nill,OU=IK888888888,OU=Trust Center,O=ITSG TrustCenter fuer sonstige Leistungserbringer,C=DE");
			log.info(cert.getSigAlgName());
			log.info(cert.getSigAlgOID());
			log.info("Public key: " + cert.getPublicKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void verschlüsselnCMS() {
		try {
			File original = new File("src/test/resources/original");

			VerschlüsselnSigneren vs = new VerschlüsselnSigneren(keyRepo, zertifikatRepo, pathConfig.dataPathOriginal,
					pathConfig.dataPathVerschl);

			vs.signData(cert, subscriberKp.getPrivate(), original, signiert);
			vs.encryptData(cert, signiert, verschlüsselt1);
			Check.fileExists(signiert);
			Check.fileExists(verschlüsselt1);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void verschlüsselnSMIME() {
		try {
			File original = new File("src/test/resources/original");

			VerschlüsselnSigneren vs = new VerschlüsselnSigneren(keyRepo, zertifikatRepo, pathConfig.dataPathOriginal,
					pathConfig.dataPathVerschl);
			vs.encryptDataSMIME(cert, subscriberKp.getPrivate(), cert, original, verschlüsselt2);
			Check.fileExists(verschlüsselt2);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		return ppk.generateKeyPair();
	}

}
