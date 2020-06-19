package tho.nill.datenlieferung.zertifikate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;

import org.apache.xerces.impl.dv.util.Base64;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;

import entities.VersenderKey;
import lombok.NonNull;
import repositories.VersenderKeyRepository;
import tho.nill.datenlieferung.allgemein.Dateien;
import tho.nill.datenlieferung.simpleAttributes.IK;

public class PrivPubEntities {
	private VersenderKeyRepository keyRepo;

	public PrivPubEntities(@NonNull VersenderKeyRepository keyRepo) {
		super();
		this.keyRepo = keyRepo;
	}

	public VersenderKey saveKeyPair(@NonNull KeyPair kp, @NonNull IK versenderIK, @NonNull String name)
			throws OperatorCreationException, IOException {
		VersenderKey key = new VersenderKey();
		key.setVersenderIK(versenderIK);
		key.setPrivKey(kp.getPrivate().getEncoded());
		key.setPubKey(kp.getPublic().getEncoded());
		key.setVon(Instant.now());
		key.setCertificateRequest(createCertificationRequest(kp, "CN=" + name + ",OU=IK" + versenderIK
				+ ",OU=ThomasNill,O=ITSG TrustCenter fuer sonstige Leistungserbringer,C=DE"));
		key.setAktiv(true);
		return keyRepo.save(key);
	}

	public PrivateKey lesePrivateKey(@NonNull VersenderKey k) throws NoSuchAlgorithmException, InvalidKeySpecException {
		PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(k.getPrivKey());
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(ks);
	}

	public byte[] createCertificationRequest(@NonNull KeyPair keyPair, @NonNull String _subjDN)
			throws OperatorCreationException, IOException {
		X500Name subjectName = new X500Name(_subjDN);
		SubjectPublicKeyInfo subPub = SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded());

		PKCS10CertificationRequestBuilder kpGen = new PKCS10CertificationRequestBuilder(subjectName, subPub);

		JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WITHRSA");

		ContentSigner signer = builder.build(keyPair.getPrivate());
		PKCS10CertificationRequest request = kpGen.build(signer);
		return request.getEncoded();
	}

	public void saveCertificationRequest(@NonNull VersenderKey key, @NonNull File output) throws IOException {
		byte[] request = key.getCertificateRequest();
		try (FileOutputStream out = Dateien.createOutputStream(output)) {
			out.write("-----BEGIN NEW CERTIFICATE REQUEST-----\n".getBytes());
			out.write(Base64.encode(request).getBytes());
			out.write("\n-----END NEW CERTIFICATE REQUEST-----\n".getBytes());
		}
	}

}
