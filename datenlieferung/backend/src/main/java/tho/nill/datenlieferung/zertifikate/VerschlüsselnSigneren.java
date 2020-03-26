package tho.nill.datenlieferung.zertifikate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSSignedDataStreamGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mime.smime.SMIMEEnvelopedWriter;
import org.bouncycastle.mime.smime.SMIMESignedWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import repositories.VersenderKeyRepository;
import repositories.ZertifikatRepository;
import tho.nill.datenlieferung.allgemein.Action;
import tho.nill.datenlieferung.allgemein.CreateProtokoll;
import tho.nill.datenlieferung.allgemein.Dateien;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;
import tho.nill.datenlieferung.simpleAttributes.IK;

@Slf4j
public class VerschlüsselnSigneren extends Verzeichnisse implements Action {

	private static final String SHA256WITH_RSA = "SHA256withRSA";
	private static final String BC = "BC";
	private static final String ZERTIFIKAT_FÜR_DAS_ZIEL = "Zertifikat für das Ziel ";
	private static final String ZERTIFIKAT_FÜR_DEN_VERSENDER = "Zertifikat für den Versender";
	private static final String KEY_FÜR = "Key für ";
	private static final String KANN_NICHT_ERZEUGT_WERDEN = " kann nicht erzeugt werden";
	private static final String EXISTIERT_NICHT = " existiert nicht";
	private VersenderKeyRepository keyRepo;
	private ZertifikatRepository certs;

	public VerschlüsselnSigneren(@NonNull VersenderKeyRepository keyRepo, @NonNull ZertifikatRepository certs,
			@NonNull String datenPathOriginal, @NonNull String datenPathVerschl) {
		super(datenPathOriginal, datenPathVerschl);
		Security.addProvider(new BouncyCastleProvider());
		this.keyRepo = keyRepo;
		this.certs = certs;
	}

	@Override
	public Optional<DatenlieferungProtokoll> action(@NonNull Datenlieferung datenlieferung) {
		try {
			IK versenderIK = datenlieferung.getVersenderIK();
			if (versenderIK == null) {
				throw new DatenlieferungException(
						"VersenderIK in Datenlieferung " + datenlieferung.getDatenlieferungId() + " ist leer");
			}

			IK verschlusselnIK = datenlieferung.getDatenPrüfungsIK();

			if (verschlusselnIK == null) {
				throw new DatenlieferungException(
						"DatenprüfungsIK in Datenlieferung " + datenlieferung.getDatenlieferungId() + " ist leer");
			}

			List<entities.VersenderKey> keyList = keyRepo.geKey(versenderIK);
			if (keyList.isEmpty()) {
				throw new DatenlieferungException(KEY_FÜR + versenderIK + EXISTIERT_NICHT);
			}

			PrivPubEntities pe = new PrivPubEntities(keyRepo);
			entities.VersenderKey k = keyList.get(0);
			PrivateKey signingKey = pe.lesePrivateKey(k);
			if (keyList.isEmpty()) {
				throw new DatenlieferungException(KEY_FÜR + versenderIK + KANN_NICHT_ERZEUGT_WERDEN);
			}

			List<entities.Zertifikat> signingZertList = certs.getZertifikat(versenderIK, Instant.now());
			if (signingZertList.isEmpty()) {
				throw new DatenlieferungException(ZERTIFIKAT_FÜR_DEN_VERSENDER + versenderIK + EXISTIERT_NICHT);
			}

			entities.Zertifikat signingz = signingZertList.get(0);
			Certificates c = new Certificates(certs, keyRepo);
			X509Certificate signingCertificate = c.leseZertifikat(signingz);
			if (signingKey == null) {
				throw new DatenlieferungException(
						ZERTIFIKAT_FÜR_DEN_VERSENDER + versenderIK + KANN_NICHT_ERZEUGT_WERDEN);
			}

			List<entities.Zertifikat> encryptionZertList = certs.getZertifikat(verschlusselnIK, Instant.now());
			if (encryptionZertList.isEmpty()) {
				throw new DatenlieferungException(ZERTIFIKAT_FÜR_DAS_ZIEL + verschlusselnIK + EXISTIERT_NICHT);
			}
			entities.Zertifikat encryptionz = encryptionZertList.get(0);
			X509Certificate encryptionCertificate = c.leseZertifikat(encryptionz);
			if (encryptionCertificate == null) {
				throw new DatenlieferungException(
						ZERTIFIKAT_FÜR_DAS_ZIEL + verschlusselnIK + KANN_NICHT_ERZEUGT_WERDEN);
			}

			File inputFile = getOrginalFile(datenlieferung);
			File signiertFile = Dateien.createFile("target/signiert/" + datenlieferung.getDatenlieferungId());
			File encryptFile = getVerschlFile(datenlieferung);

			log.debug("Originale Datei " + inputFile.getAbsolutePath());
			log.debug("Signierte Datei " + signiertFile.getAbsolutePath());
			log.debug("Verschlüsselte Datei " + encryptFile.getAbsolutePath());

			Files.createDirectories(signiertFile.toPath().getParent());
			Files.createDirectories(encryptFile.toPath().getParent());

			if (!inputFile.exists()) {
				throw new DatenlieferungException("File " + inputFile.getAbsolutePath() + " does not exist");
			}
			signData(signingCertificate, signingKey, inputFile, signiertFile);

			encryptData(encryptionCertificate, signiertFile, encryptFile);
			log.debug("Verschlüsselte Datei " + encryptFile.getAbsolutePath());
			datenlieferung.setDateigröße_übertragung(encryptFile.length());
			datenlieferung.setVerschlüsselt(LocalDateTime.now());
		} catch (IOException | CMSException | OperatorCreationException | CertificateException
				| NoSuchAlgorithmException | InvalidKeySpecException e) {
			log.error("Exception in " + this.getClass().getCanonicalName() + ".action", e);
			return CreateProtokoll.create(AktionsArt.VERSCHLÜSSELT, FehlerMeldung.FILE_VERSCHLÜSSELUNG, e,
					datenlieferung.getDatenlieferungId());
		}
		return Optional.empty();
	}

	public void encryptDataSMIME(@NonNull X509Certificate signingCertificate, @NonNull PrivateKey signingKey,
			@NonNull X509Certificate encryptionCertificate, @NonNull File inputFile, @NonNull File outputFile)
			throws IOException, CertificateEncodingException, CMSException, OperatorCreationException {

		byte[] buffer = new byte[1080];
		try (FileOutputStream ofile = Dateien.createOutputStream(outputFile);
				InputStream input = new FileInputStream(inputFile);) {

			SMIMEEnvelopedWriter.Builder envBldr = new SMIMEEnvelopedWriter.Builder();

			// specify encryption certificate
			envBldr.addRecipientInfoGenerator(
					new JceKeyTransRecipientInfoGenerator(encryptionCertificate).setProvider(BC));

			SMIMEEnvelopedWriter envWrt = envBldr.build(ofile,
					new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES256_CBC).setProvider(BC).build());

			OutputStream envOut = envWrt.getContentStream();

			SMIMESignedWriter.Builder sigBldr = new SMIMESignedWriter.Builder();

			// specify signature certificate
			sigBldr.addCertificate(new JcaX509CertificateHolder(signingCertificate));

			sigBldr.addSignerInfoGenerator(new JcaSimpleSignerInfoGeneratorBuilder().setProvider(BC)
					.build(SHA256WITH_RSA, signingKey, signingCertificate));

			// add the encryption stream to the signature stream
			SMIMESignedWriter sigWrt = sigBldr.build(envOut);

			try (OutputStream sigOut = sigWrt.getContentStream()) {
				int anz;
				while ((anz = input.read(buffer)) != -1) {
					sigOut.write(buffer, 0, anz);
				}
			}
		}

	}

	public void encryptData(@NonNull X509Certificate encryptionCertificate, @NonNull File inputFile,
			@NonNull File outputFile) throws CertificateEncodingException, CMSException, IOException {

		if (null != inputFile && null != encryptionCertificate) {
			CMSEnvelopedDataStreamGenerator cmsEnvelopedDataGenerator = new CMSEnvelopedDataStreamGenerator();
			cmsEnvelopedDataGenerator.setBufferSize(256);
			RecipientInfoGenerator transKeyGen = new JceKeyTransRecipientInfoGenerator(encryptionCertificate);
			cmsEnvelopedDataGenerator.addRecipientInfoGenerator(transKeyGen);
			OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.AES128_CBC).setProvider(BC)
					.build();

			byte[] buffer = new byte[1080];
			try (FileOutputStream ofile = Dateien.createOutputStream(outputFile);
					OutputStream oStream = cmsEnvelopedDataGenerator.open(ofile, encryptor);
					InputStream input = new FileInputStream(inputFile);) {
				int anz;
				while ((anz = input.read(buffer)) != -1) {
					oStream.write(buffer, 0, anz);
				}
			}
		}
	}

	public void signDataMime(@NonNull X509Certificate signingCertificate, @NonNull PrivateKey signingKey,
			@NonNull File inputFile, @NonNull File outputFile)
			throws IOException, CertificateEncodingException, CMSException, OperatorCreationException {
		SMIMESignedWriter.Builder sigBldr = new SMIMESignedWriter.Builder();

		sigBldr.addCertificate(new JcaX509CertificateHolder(signingCertificate));

		sigBldr.addSignerInfoGenerator(new JcaSimpleSignerInfoGeneratorBuilder().setProvider(BC).build(SHA256WITH_RSA,
				signingKey, signingCertificate));

		byte[] buffer = new byte[1080];
		try (FileOutputStream ofile = Dateien.createOutputStream(outputFile);
				InputStream input = new FileInputStream(inputFile)) {
			SMIMESignedWriter sigWrt = sigBldr.build(ofile);
			OutputStream out = sigWrt.getContentStream();
			int anz;
			while ((anz = input.read(buffer)) != -1) {
				out.write(buffer, 0, anz);
			}
			out.close();
		}

	}

	/*
	 * CMSProcessableFile
	 */
	public void signData2(@NonNull X509Certificate signingCertificate, @NonNull PrivateKey signingKey,
			@NonNull File inputFile, @NonNull File outputFile)
			throws CertificateEncodingException, OperatorCreationException, CMSException, IOException {

		List<X509Certificate> certList = new ArrayList<X509Certificate>();
		certList.add(signingCertificate);
		Store<?> certsStore = new JcaCertStore(certList);

		CMSSignedDataStreamGenerator cmsGenerator = new CMSSignedDataStreamGenerator();
		cmsGenerator.setBufferSize(256);

		ContentSigner contentSigner = new JcaContentSignerBuilder(SHA256WITH_RSA).setProvider(BC).build(signingKey);
		cmsGenerator.addSignerInfoGenerator(
				new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider(BC).build())
						.build(contentSigner, signingCertificate));
		cmsGenerator.addCertificates(certsStore);

		byte[] buffer = new byte[1080];
		try (FileOutputStream ofile = Dateien.createOutputStream(outputFile);
				OutputStream oStream = cmsGenerator.open(ofile);
				InputStream input = new FileInputStream(inputFile);) {
			int anz;
			while ((anz = input.read(buffer)) != -1) {
				oStream.write(buffer, 0, anz);
			}
		}
	}

	public void signData(@NonNull X509Certificate signingCertificate, @NonNull PrivateKey signingKey,
			@NonNull File inputFile, @NonNull File outputFile)
			throws CertificateEncodingException, OperatorCreationException, CMSException, IOException {
		log.debug("Signed out Start ");

		List<X509Certificate> certList = new ArrayList<X509Certificate>();
		certList.add(signingCertificate);
		Store<?> certsStore = new JcaCertStore(certList);

		CMSSignedDataGenerator cmsGenerator = new CMSSignedDataGenerator();

		ContentSigner contentSigner = new JcaContentSignerBuilder(SHA256WITH_RSA).setProvider(BC).build(signingKey);
		cmsGenerator.addSignerInfoGenerator(
				new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider(BC).build())
						.build(contentSigner, signingCertificate));
		cmsGenerator.addCertificates(certsStore);

		CMSTypedData msg = new CMSProcessableFile(inputFile);

		CMSSignedData sigData = cmsGenerator.generate(msg, true);
		try (FileOutputStream ofile = Dateien.createOutputStream(outputFile)) {
			ofile.write(sigData.getEncoded());
		}
		log.debug("Signed out: " + sigData.getEncoded().toString());
	}

}
