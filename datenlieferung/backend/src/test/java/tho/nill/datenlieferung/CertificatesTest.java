package tho.nill.datenlieferung;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import entities.Zertifikat;
import lombok.extern.slf4j.Slf4j;
import repositories.VersenderKeyRepository;
import repositories.ZertifikatRepository;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.zertifikate.Certificates;
import tho.nill.datenlieferung.zertifikate.PrivPubKeys;
import tho.nill.datenlieferung.zertifikate.ZertifikateLesen;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CertificatesTest {
	@Autowired
	public ZertifikatRepository zertifikatRepo;

	@Autowired
	public VersenderKeyRepository keyRepo;

	PrivPubKeys ppk;
	Certificates certs;

	public CertificatesTest() {
		Security.addProvider(new BouncyCastleProvider());
	}

	@Before
	public void init() {
		this.ppk = new PrivPubKeys();
		this.certs = new Certificates(zertifikatRepo, keyRepo);

		Check.clearDb(zertifikatRepo, keyRepo);

	}

	@Test
	public void requestErzeugen() {
		try {
			KeyPair keyPair = generateKeyPair();
			File request = new File(Verzeichnisse.getTestVerz(this, "request") + File.separatorChar + "request.p10");
			certs.createCertificationRequest(keyPair,
					"CN=Herr Thomas Nill,OU=IK999999999,OU=ThomasNill,O=ITSG TrustCenter fuer sonstige Leistungserbringer,C=DE",
					request);
			Check.fileExists(request);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		return ppk.generateKeyPair();
	}

	@Test
	public void zertLesenUndSpeichern() {
		InputStream inStream = null;
		try {
			Certificates c = new Certificates(zertifikatRepo, keyRepo);
			inStream = new FileInputStream("src/test/resources/zertifikat");
			X509Certificate cert = c.leseZertifikat(inStream);
			assertNotNull(cert);
			Zertifikat zertifikat = new Zertifikat();
			zertifikat.setSerialId(cert.getSerialNumber());
			zertifikatRepo.saveAndFlush(zertifikat);
			assertTrue(zertifikatRepo.count() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Ignore
	@Test
	public void alleZertifikateLesen() {
		try {
			ZertifikateLesen lesen = new ZertifikateLesen(new File("src/test/resources/test-sha256.key").toPath(),
					zertifikatRepo, keyRepo);
			lesen.einlesen();
			assertTrue(zertifikatRepo.count() > 3);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
