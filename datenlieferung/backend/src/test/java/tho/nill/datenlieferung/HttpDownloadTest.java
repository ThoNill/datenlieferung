package tho.nill.datenlieferung;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.UriParser;
import org.apache.commons.vfs2.provider.https.HttpsFileProvider;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;

@Slf4j
public class HttpDownloadTest {
	private static final String REMOTE_URL = "https://de.wikipedia.org/wiki/Wikipedia:Hauptseite";
	// "https://trustcenter-data.itsg.de/dale/gesamt-pkcs.key"
	public final static Log log = LogFactory.getLog(HttpDownloadTest.class);

	public HttpDownloadTest() {
		super();
	}

	@Test
	public void downloadFileSystem() {

		try {
			String dateiName = Verzeichnisse.getTestDatei(this, "FileSystem", "gesamt-pkcs.key");
			DefaultFileSystemManager remoteManger = new DefaultFileSystemManager();
			remoteManger.setLogger(log);
			remoteManger.init();
			remoteManger.addProvider("https", new HttpsFileProvider());

			StandardFileSystemManager localManager = new StandardFileSystemManager();
			localManager.init();

			FileSystemManager vorherigerManager = VFS.getManager();
			VFS.setManager(remoteManger);

			for (String s : VFS.getManager().getSchemes()) {
				log.debug("1 mögliches Schema: " + s);
			}

			for (String s : remoteManger.getSchemes()) {
				log.debug("2 mögliches Schema: " + s);
			}

			StringBuilder name = new StringBuilder();
			String schema = UriParser.extractScheme(remoteManger.getSchemes(), REMOTE_URL, name);
			log.debug("Schema: " + schema);
			log.debug("Name: " + name);

			URI uri = new URI(REMOTE_URL);

			FileObject remoteFile = remoteManger.resolveFile(uri);
			FileObject localFile = localManager.resolveFile(new File(dateiName).getAbsoluteFile().toURI());

			localFile.copyFrom(remoteFile, Selectors.SELECT_SELF);

			localManager.close();
			remoteManger.close();

			VFS.setManager(vorherigerManager);
			assertTrue(new File(dateiName).exists());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void downloadHttpClient() {
		try {
			String dateiName = Verzeichnisse.getTestDatei(this, "HttpClient", "gesamt-pkcs.key");
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(REMOTE_URL)).build();
			CompletableFuture<Void> future = client.sendAsync(request, BodyHandlers.ofFile(Paths.get(dateiName)))
					.thenApply(response -> {
						log.debug(response.statusCode());
						return response;
					}).thenApply(HttpResponse::body).thenAccept(x -> x.getFileName());
			future.get();
			assertTrue(new File(dateiName).exists());
		} catch (Exception e) {
			e.printStackTrace();
			fail();

		}
	}
}
