package tho.nill.datenlieferung.zertifikate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import repositories.VersenderKeyRepository;
import repositories.ZertifikatRepository;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;
import tho.nill.datenlieferung.simpleAttributes.IK;

@Slf4j
public class ZertifikateLesen extends Certificates implements Consumer<String> {

	private Path pfad;
	private StringBuilder builder;

	public ZertifikateLesen(@NonNull Path pfad, @NonNull ZertifikatRepository certs,
			@NonNull VersenderKeyRepository keyRepo) {
		super(certs, keyRepo);
		this.pfad = pfad;
		this.builder = new StringBuilder();
	}

	public void einlesen() {
		try (Stream<String> fileNames = Files.lines(pfad)) {
			fileNames.forEach(this);
		} catch (Exception e) {
			throw new DatenlieferungException("Konnte nicht von " + pfad + " lesen", e);
		}
	}

	@Override
	public void accept(@NonNull String t) {
		try {
			if (t.isBlank()) {
				if (builder.length() > 0) {
					this.builder.append("-----END CERTIFICATE-----\n");

					X509Certificate cert = leseZertifikat();
					IK ik = extractIK(cert);
					if (ik != null) {
						String pemZertifikat = builder.toString();
						saveZertifikat(cert, ik, pemZertifikat);
					}
					this.builder = new StringBuilder();
				}
			} else {
				if (builder.length() == 0) {
					this.builder.append("-----BEGIN CERTIFICATE-----\n");
				}
				this.builder.append(t);
				this.builder.append("\n");
			}
		} catch (Exception ex) {
			log.error("Exception in " + this.getClass().getCanonicalName() + ".accept", ex);
		}
	}

	private IK extractIK(@NonNull X509Certificate cert) {
		String name = cert.getSubjectDN().getName();
		log.info("extract {} ", name);
		X500Name x500name = new X500Name(name);

		RDN[] cn = x500name.getRDNs(BCStyle.OU);
		if (cn.length > 0) {
			String sik = cn[0].getFirst().getValue().toString().substring(2);
			IK ik = new IK(Integer.valueOf(sik));
			return ik;
		} else {
			return null;
		}
	}

	private X509Certificate leseZertifikat() throws CertificateException, IOException {
		InputStream inStream = new ByteArrayInputStream(builder.toString().getBytes());
		return leseZertifikat(inStream);
	}

}
