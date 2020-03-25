package tho.nill.datenlieferung;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.extern.slf4j.Slf4j;
import repositories.VersenderKeyRepository;
import repositories.ZertifikatRepository;
import tho.nill.datenlieferung.allgemein.Dateien;
import tho.nill.datenlieferung.allgemein.DummyVoid;
import tho.nill.datenlieferung.zertifikate.ZertifikateLesen;

@Slf4j
@Service
public class ZertifikateVonDerItsgEinlesenService extends BasisService<DummyVoid> {

	private ZertifikatRepository certs;

	private VersenderKeyRepository keyRepo;

	public ZertifikateVonDerItsgEinlesenService(PlatformTransactionManager transactionManager,
			ZertifikatRepository certs, VersenderKeyRepository keyRepo) {
		super(transactionManager);
		this.certs = certs;
		this.keyRepo = keyRepo;
	}

	@Override
	public void performService(DummyVoid d) {
		try {
			String uri = "https://trustcenter-data.itsg.de/dale/annahme-sha256.key";
			// TODO "https://trustcenter-data.itsg.de/dale/gesamt-pkcs.key";
			Path pfad = Dateien.createPath("target/gesamt-pkcs.key");

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).build();
			CompletableFuture<Void> future = client.sendAsync(request, BodyHandlers.ofFile(pfad))
					.thenApply((HttpResponse<Path> response) -> {
						log.debug("" + response.statusCode());
						return response;
					}).thenApply(HttpResponse::body).thenAccept(x -> einlesen(x));
			future.get();
		} catch (Exception e) {
			log.error("Exception in " + this.getClass().getCanonicalName() + ".performService", e);
		}
	}

	public void einlesen(Path pfad) {
		ZertifikateLesen lesen = new ZertifikateLesen(pfad, certs, keyRepo);
		lesen.einlesen();
	}

}
