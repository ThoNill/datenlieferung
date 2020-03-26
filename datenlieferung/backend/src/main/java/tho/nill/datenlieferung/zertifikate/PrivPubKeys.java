package tho.nill.datenlieferung.zertifikate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.util.ASN1Dump;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import tho.nill.datenlieferung.allgemein.Dateien;

@Slf4j
public class PrivPubKeys {

	public static final String PUB_FILETYPE = ".pub";
	public static final String KEY_FILETYPE = ".key";
	private static final String END_PUBLIC_KEY = "\n-----END PUBLIC KEY-----\n";
	private static final String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n";
	private static final String END_PRIVATE_KEY = "\n-----END PRIVATE KEY-----\n";
	private static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n";

	public PrivPubKeys() {
		super();
	}

	public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		log.info("Erzeuge KeyPair");
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		return kpg.generateKeyPair();
	}

	public void saveKeys(@NonNull KeyPair kp, @NonNull String outFile) throws IOException {

		log.info("Schreibe private Key in {}", outFile + KEY_FILETYPE);
		try (FileOutputStream out = Dateien.createOutputStream(outFile + KEY_FILETYPE)) {
			out.write(createPksc8Data(kp));
			log.info("Privater Schlüssel in {} geschrieben", outFile + KEY_FILETYPE);
		} catch (IOException ex) {
			throw ex;
		}

		try (FileOutputStream out = Dateien.createOutputStream(outFile + PUB_FILETYPE)) {
			out.write(createX509Data(kp));
			log.info("Öffentlicher Schlüssel in {} geschrieben", outFile + PUB_FILETYPE);
		} catch (IOException ex) {
			throw ex;
		}

	}

	private byte[] createX509Data(KeyPair kp) {
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(kp.getPublic().getEncoded());
		return x509EncodedKeySpec.getEncoded();
	}

	private byte[] createPksc8Data(KeyPair kp) {
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(kp.getPrivate().getEncoded());
		return pkcs8EncodedKeySpec.getEncoded();
	}

	public PrivateKey loadPrivateKey(@NonNull String keyFile)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		log.info("Lese private Key aus {}", keyFile);
		PKCS8EncodedKeySpec ks = loadPrivateKeySpec(keyFile);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(ks);
	}

	public PublicKey loadPublicKey(@NonNull String keyFile)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		log.info("Lese public Key aus {}", keyFile);
		X509EncodedKeySpec ks = loadPublicKeySpec(keyFile);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(ks);
	}

	private PKCS8EncodedKeySpec loadPrivateKeySpec(@NonNull String keyFile) throws IOException {
		return new PKCS8EncodedKeySpec(readBytesFromFile(keyFile));
	}

	private X509EncodedKeySpec loadPublicKeySpec(@NonNull String keyFile) throws IOException {
		return new X509EncodedKeySpec(readBytesFromFile(keyFile));
	}

	private byte[] readBytesFromFile(String keyFile) throws IOException {
		Path path = Dateien.createPath(keyFile);
		return Files.readAllBytes(path);
	}

	public PrivateKey loadPrivateKeyFromPEMFile(@NonNull String keyFile)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		log.info("Lese private Key aus {}", keyFile);
		PKCS8EncodedKeySpec ks = loadPrivateKeySpecFromPEMFile(keyFile);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(ks);
	}

	public PublicKey loadPublicKeyFromPEMFile(@NonNull String keyFile)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		log.info("Lese public Key aus {}", keyFile);
		X509EncodedKeySpec ks = loadPublicKeySpecFromPEMFile(keyFile);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(ks);
	}

	private PKCS8EncodedKeySpec loadPrivateKeySpecFromPEMFile(@NonNull String keyFile) throws IOException {
		return new PKCS8EncodedKeySpec(readBytesFromPEMFile(keyFile, BEGIN_PRIVATE_KEY, END_PRIVATE_KEY));
	}

	private X509EncodedKeySpec loadPublicKeySpecFromPEMFile(@NonNull String keyFile) throws IOException {
		return new X509EncodedKeySpec(readBytesFromPEMFile(keyFile, BEGIN_PUBLIC_KEY, END_PUBLIC_KEY));
	}

	private byte[] readBytesFromPEMFile(String keyFile, String prefix, String suffix) throws IOException {
		Path path = Dateien.createPath(keyFile);
		byte[] bytes = Files.readAllBytes(path);
		String text = new String(bytes);
		String encodesText = text.substring(prefix.length(), text.length() - suffix.length());
		return Base64.getDecoder().decode(encodesText);
	}

	public void saveAsPEM(@NonNull KeyPair kp, @NonNull String outFile) throws IOException {
		log.info("Schreibe priv/pub KeyPair im PEM Format in {] und {]", outFile + KEY_FILETYPE,
				outFile + PUB_FILETYPE);

		Base64.Encoder encoder = Base64.getEncoder();
		try (Writer out = Dateien.createWriter(outFile + KEY_FILETYPE)) {
			out.write(BEGIN_PRIVATE_KEY);
			out.write(encoder.encodeToString(createPksc8Data(kp)));
			out.write(END_PRIVATE_KEY);

		} catch (IOException ex) {
			throw ex;
		}

		try (Writer out = Dateien.createWriter(outFile + PUB_FILETYPE)) {
			out.write(BEGIN_PUBLIC_KEY);
			out.write(encoder.encodeToString(createX509Data(kp)));
			out.write(END_PUBLIC_KEY);

		} catch (IOException ex) {
			throw ex;
		}
		log.info("Priv/pub KeyPair im PEM Format in {] und {] geschrieben", outFile + KEY_FILETYPE,
				outFile + PUB_FILETYPE);
	}

	public byte[] calculateSignature(@NonNull PrivateKey pvt, @NonNull String dataFile)
			throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		log.info("Berechne die Signatur der Datei {]", dataFile);

		Signature sign = Signature.getInstance("SHA256withRSA");
		sign.initSign(pvt);

		try (InputStream in = Dateien.createInputStream(dataFile)) {
			byte[] buf = new byte[2048];
			int len;
			while ((len = in.read(buf)) != -1) {
				sign.update(buf, 0, len);
			}
		}
		return sign.sign();
	}

	public static void dump(@NonNull String fileName) throws IOException {
		log.info("Dump der Datei {] ", fileName);

		try (ASN1InputStream ais = new ASN1InputStream(Dateien.createInputStream(fileName))) {
			while (ais.available() > 0) {
				ASN1Primitive obj = ais.readObject();
				log.debug(ASN1Dump.dumpAsString(obj, true));
			}
		}
	}
}