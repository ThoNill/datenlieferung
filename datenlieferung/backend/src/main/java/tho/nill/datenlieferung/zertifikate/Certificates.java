package tho.nill.datenlieferung.zertifikate;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;

import entities.Zertifikat;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import repositories.VersenderKeyRepository;
import repositories.ZertifikatRepository;
import tho.nill.datenlieferung.allgemein.Dateien;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;
import tho.nill.datenlieferung.simpleAttributes.IK;

@Slf4j
public class Certificates {
	private ZertifikatRepository certs;
	private VersenderKeyRepository keyRepo;

	private CertificateFactory cf;

	private static BigInteger serialNumber = BigInteger.ONE;
	private static final X509ExtensionUtils extUtils = new X509ExtensionUtils(new SHA1DigestCalculator());

	public Certificates(@NonNull ZertifikatRepository certs, @NonNull VersenderKeyRepository keyRepo) {
		super();
		this.certs = certs;
		this.keyRepo = keyRepo;
		try {
			this.cf = CertificateFactory.getInstance("X.509");
		} catch (CertificateException e) {
			log.error("Exception in " + this.getClass().getCanonicalName() + ".constructor", e);
			throw new DatenlieferungException("CertificateFactory kann nich initialisiert werden", e);
		}

	}

	public void erzeugeTestZertifikate(@NonNull IK ik)
			throws OperatorCreationException, IOException, GeneralSecurityException {
		PrivPubKeys k = new PrivPubKeys();
		KeyPair subKP = k.generateKeyPair();
		KeyPair issKP = k.generateKeyPair();

		PrivPubEntities ppe = new PrivPubEntities(keyRepo);
		ppe.saveKeyPair(subKP, ik, "Thomas Nill");

		X509Certificate cert = makeCertificate(subKP,
				"CN=Herr Thomas Nill,OU=IK" + ik.toString()
						+ ",OU=ThomasNill,O=ITSG TrustCenter fuer sonstige Leistungserbringer,C=DE",
				issKP,
				"CN=Herr Thomas Nill,OU=IK888888888,OU=Trust Center,O=ITSG TrustCenter fuer sonstige Leistungserbringer,C=DE");
		Base64.Encoder encoder = Base64.getEncoder();

		StringBuilder builder = new StringBuilder();
		builder.append("-----BEGIN CERTIFICATE-----\n");
		builder.append(encoder.encodeToString(cert.getEncoded()));
		builder.append("-----END CERTIFICATE-----\n");
		saveZertifikat(cert, ik, builder.toString());
	}

	public static X509Certificate makeCertificate(@NonNull KeyPair subKP, @NonNull String _subDN,
			@NonNull KeyPair issKP, @NonNull String _issDN)
			throws GeneralSecurityException, IOException, OperatorCreationException {
		PrivateKey issPriv = issKP.getPrivate();
		PublicKey issPub = issKP.getPublic();
		SubjectPublicKeyInfo subPub = SubjectPublicKeyInfo.getInstance(subKP.getPublic().getEncoded());

		X509v3CertificateBuilder v3CertGen = new X509v3CertificateBuilder(new X500Name(_issDN), allocateSerialNumber(),
				new Date(System.currentTimeMillis()),
				new Date(System.currentTimeMillis() + (1000L * 60L * 60L * 24L * 365L * 2L)), new X500Name(_subDN),
				subPub);

		JcaContentSignerBuilder contentSignerBuilder = new JcaContentSignerBuilder("SHA256WithRSA");

		v3CertGen.addExtension(Extension.subjectKeyIdentifier, false, createSubjectKeyId(subPub));

		v3CertGen.addExtension(Extension.authorityKeyIdentifier, false, createAuthorityKeyId(issPub));

		v3CertGen.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));

		X509CertificateHolder holder = v3CertGen.build(contentSignerBuilder.build(issPriv));

		X509Certificate _cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(holder);

		_cert.checkValidity(new Date());
		_cert.verify(issPub);

		return _cert;
	}

	private static AuthorityKeyIdentifier createAuthorityKeyId(PublicKey _pubKey) throws IOException {
		return extUtils.createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance(_pubKey.getEncoded()));
	}

	static SubjectKeyIdentifier createSubjectKeyId(@NonNull SubjectPublicKeyInfo _pubKey) throws IOException {
		return extUtils.createSubjectKeyIdentifier(_pubKey);
	}

	private static BigInteger allocateSerialNumber() {
		BigInteger _tmp = serialNumber;
		serialNumber = serialNumber.add(BigInteger.ONE);
		return _tmp;
	}

	// TODO kann ik = null sein?
	public void saveZertifikat(@NonNull X509Certificate cert, IK ik, @NonNull String pemZertifikat) {
		if (ik != null) {
			List<Zertifikat> z = certs.getZertifikatFromSerialId(cert.getSerialNumber());
			if (!z.isEmpty()) {
				log.info("Der Eintrag {} ist schon in den Zertifikaten vorhanden", cert.getSerialNumber());
			}

		}
		erzeugeNeuesZertifikat(cert, ik, pemZertifikat);
	}

	private Zertifikat erzeugeNeuesZertifikat(X509Certificate cert, IK ik, String pemZertifikat) {
		Zertifikat zertifikat = new Zertifikat();
		if (ik != null) {
			log.info("IK = " + ik);
			zertifikat.setIk(ik);
		}
		zertifikat.setSerialId(cert.getSerialNumber());
		zertifikat.setVon(cert.getNotBefore().toInstant());
		zertifikat.setBis(cert.getNotAfter().toInstant());
		log.info("" + pemZertifikat.length());
		log.info("vor algo");
		log.info("Algo: " + cert.getSigAlgName());
		log.info("AlgoOID: " + cert.getSigAlgOID());

		zertifikat.setPemZertifikat(pemZertifikat);
		return certs.saveAndFlush(zertifikat);
	}

	public X509Certificate leseZertifikat(@NonNull InputStream inStream) throws CertificateException, IOException {
		X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);
		X500Principal p = cert.getSubjectX500Principal();
		log.info("ID: " + cert.getSerialNumber());
		log.info("Name: " + p.getName());
		inStream.close();
		return cert;
	}

	public X509Certificate leseZertifikat(@NonNull Zertifikat z) throws CertificateException, IOException {
		log.info(z.getPemZertifikat());
		InputStream inStream = new ByteArrayInputStream(z.getPemZertifikat().getBytes());

		X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);
		X500Principal p = cert.getSubjectX500Principal();
		log.info("ID: " + cert.getSerialNumber());
		log.info("Name: " + p.getName());
		inStream.close();
		return cert;
	}

	public void createCertificationRequest(@NonNull KeyPair keyPair, @NonNull String _subjDN, @NonNull File output)
			throws IOException, OperatorCreationException {
		SubjectPublicKeyInfo subPub = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());

		X500Name subjectName = new X500Name(_subjDN);
		PKCS10CertificationRequestBuilder kpGen = new PKCS10CertificationRequestBuilder(subjectName, subPub);

		JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WITHRSA");

		ContentSigner signer = builder.build(keyPair.getPrivate());
		PKCS10CertificationRequest request = kpGen.build(signer);
		try (FileOutputStream out = Dateien.createOutputStream(output)) {
			out.write(request.getEncoded());
		}
	}

	public X509Certificate selfSign(@NonNull KeyPair keyPair, @NonNull String subjectDN)
			throws OperatorCreationException, CertIOException, CertificateException {
		Provider bcProvider = new BouncyCastleProvider();
		Security.addProvider(bcProvider);

		String signatureAlgorithm = "SHA256WithRSA";
		ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).build(keyPair.getPrivate());

		X500Name dnName = new X500Name(subjectDN);
		BigInteger certSerialNumber = allocateSerialNumber();
		JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(dnName, certSerialNumber,
				new Date(System.currentTimeMillis()),
				new Date(System.currentTimeMillis() + (1000L * 60L * 60L * 24L * 365L * 2L)), dnName,
				keyPair.getPublic());

		certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true)); // <-- true for CA,
																								// false for EndEntity

		return new JcaX509CertificateConverter().setProvider(bcProvider)
				.getCertificate(certBuilder.build(contentSigner));
	}

}
