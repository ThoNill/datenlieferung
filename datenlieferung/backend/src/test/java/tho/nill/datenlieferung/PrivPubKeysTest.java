package tho.nill.datenlieferung;

import static org.junit.Assert.fail;

import java.io.File;
import java.security.KeyPair;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.zertifikate.PrivPubKeys;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class PrivPubKeysTest {

	public PrivPubKeysTest() {
		Security.addProvider(new BouncyCastleProvider());
	}

	@Test
	public void createLoadFile() {
		try {
			PrivPubKeys keys = new PrivPubKeys();
			String keysDir = Verzeichnisse.getTestVerz(this, "creatLoadFile/test");
			KeyPair kp = keys.generateKeyPair();
			keys.saveKeys(kp, keysDir);
			Check.fileExists(new File(keysDir + PrivPubKeys.KEY_FILETYPE));
			Check.fileExists(new File(keysDir + PrivPubKeys.PUB_FILETYPE));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	@Test
	public void createLoadFileXML() {
		try {
			PrivPubKeys keys = new PrivPubKeys();
			String keysDir = Verzeichnisse.getTestVerz(this, "creatLoadFileXML/test");
			KeyPair kp = keys.generateKeyPair();
			keys.saveKeys(kp, keysDir);
			Check.fileExists(new File(keysDir + PrivPubKeys.KEY_FILETYPE));
			Check.fileExists(new File(keysDir + PrivPubKeys.PUB_FILETYPE));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

}
