package tho.nill.datenlieferung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.mail.Pop3MailReceiver;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import entities.Datenlieferung;
import entities.EingeleseneDatei;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import repositories.DatenlieferungRepository;
import repositories.EingeleseneDateiRepository;
import tho.nill.datenlieferung.allgemein.Dateien;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.einlesen.DefaultSuchInfoExtractor;
import tho.nill.datenlieferung.einlesen.SuchInfo;
import tho.nill.datenlieferung.einlesen.email.Pop3Configuration;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;
import tho.nill.datenlieferung.sql.SqlQueryFlux;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MailEinlesenTest implements Consumer<Object> {
	@Autowired
	public PlatformTransactionManager transactionManager;

	@Autowired
	TemplateEngine templateEngine;

	@Autowired
	DataSource dataSource;

	@Autowired
	public DatenlieferungRepository datenlieferungenRepo;

	@Autowired
	public EingeleseneDateiRepository eingeleseneDateiRepo;

	@Autowired
	public Pop3Configuration config;

	public Pop3Server server;

	@Before
	public void init() {
		try {
			server = new Pop3Server(config.BENUTZERNAME, config.PASSWORT, config.TO);
			server.before();
		} catch (Throwable t) {
			t.printStackTrace();
		}
		Check.clearDb(datenlieferungenRepo, eingeleseneDateiRepo);
		testdatenAnlegen();
	}

	@After
	public void deinit() {
		server.after();
	}

	private void testdatenAnlegen() {
		Datenlieferung d = new Datenlieferung();
		d.setPhysDateiname("EAPO200");
		d.setDateigröße_nutzdaten(100);
		d.setDateigröße_übertragung(200);
		d.setErstellt(LocalDateTime.now());
		datenlieferungenRepo.save(d);
	}

	@Test
	public void extractor() {
		DefaultSuchInfoExtractor extractor = new DefaultSuchInfoExtractor();
		List<SuchInfo> infos = extractor.extractSuchInfos("aaaaaaaa EAPO200 34867 20200109:023839 bbbbbbbbb");
		assertEquals(1, infos.size());
		assertEquals("EAPO200", infos.get(0).getDateiname());
		assertEquals(34867, infos.get(0).getGröße());
		LocalDateTime erstellt = LocalDateTime.of(2020, 1, 9, 2, 38, 39);

		assertEquals(erstellt, infos.get(0).getErstellt());

		infos = extractor.extractSuchInfos("aaaaaaaa EAPO200 34867 20200109:020039 bbbbbbbbb");
		erstellt = LocalDateTime.of(2020, 1, 9, 2, 0, 39);

		assertEquals(erstellt, infos.get(0).getErstellt());

		infos = extractor
				.extractSuchInfos("EAPO200 34867 20200109:023839 aaaaaaaa EAPO200 34867 20200109:020039 bbbbbbbbb");
		assertEquals(2, infos.size());

		erstellt = LocalDateTime.of(2020, 1, 9, 2, 38, 39);

		assertEquals(erstellt, infos.get(0).getErstellt());

		erstellt = LocalDateTime.of(2020, 1, 9, 2, 0, 39);
		assertEquals(erstellt, infos.get(1).getErstellt());
	}

	@Test
	public void versendenUndEinlesen() {
		TransactionTemplate template = new TransactionTemplate(transactionManager);
		final Consumer<Object> consumer = this;
		template.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					SqlQueryFlux
							.using(dataSource, "select DatenlieferungId from datenlieferung where bestätigt is null")
							.subscribe(consumer);

				} catch (Exception e) {
					log.error("Exception in " + this.getClass().getCanonicalName() + ".performService", e);
				}

			}

		});
		try {
			pop3lesen();
			assertEquals(1, eingeleseneDateiRepo.count());
		} catch (Exception e) {
			log.error("Exception in " + this.getClass().getCanonicalName() + ".performService", e);
			fail();
		}

	}

	@Override
	public void accept(Object t) {
		try {
			Long id = (Long) ((Object[]) t)[0];
			Datenlieferung d = datenlieferungenRepo.getOne(id);
			if (d != null) {
				erstellen(d);
				senden(d);
			}
		} catch (Exception e) {
			throw new DatenlieferungException(e);
		}
	}

	public void erstellen(@NonNull Datenlieferung datenlieferung) throws FileNotFoundException, IOException {

		final Context context = new Context();
		context.setVariable("datei", datenlieferung.getPhysDateiname());
		context.setVariable("verschl", datenlieferung.getDateigröße_übertragung());
		context.setVariable("original", datenlieferung.getDateigröße_nutzdaten());
		context.setVariable("datum", JJJJMMTTHHMMSS(datenlieferung.getErstellt()));

		String body = templateEngine.process("testeinlesen.txt", context);
		File file = Dateien
				.createFile(Verzeichnisse.getTestDatei(this, "einlesen", "" + datenlieferung.getDatenlieferungId()));
		try (FileOutputStream out = Dateien.createOutputStream(file)) {
			out.write(body.getBytes(StandardCharsets.UTF_8));
		}
	}

	private String JJJJMMTTHHMMSS(LocalDateTime datum) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd:HHmmss");
		return datum.format(formatter);
	}

	private void senden(Datenlieferung datenlieferung) throws MessagingException {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(config.HOST);
		sender.setUsername(config.BENUTZERNAME);
		sender.setPassword(config.PASSWORT);
		sender.setPort(25);

		MimeMessage message = sender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setTo(config.TO);
		helper.setFrom(config.FROM);
		helper.setSentDate(java.sql.Timestamp.valueOf(datenlieferung.getErstellt()));
		helper.setSubject("Testversand");

		final Context context = new Context();
		context.setVariable("datei", datenlieferung.getPhysDateiname());
		context.setVariable("verschl", datenlieferung.getDateigröße_übertragung());
		context.setVariable("original", datenlieferung.getDateigröße_nutzdaten());
		context.setVariable("datum", JJJJMMTTHHMMSS(datenlieferung.getErstellt()));

		String body = templateEngine.process("testeinlesen.txt", context);

		helper.setText(body);
		sender.send(message);

		log.debug("Server count= " + server.getMessages().length);
	}

	private void springlesen() throws MessagingException, IOException {
		Pop3MailReceiver receiver = new Pop3MailReceiver(
				"pop3://" + config.BENUTZERNAME + ":" + config.PASSWORT + "@" + config.HOST + "/INBOX");
		receiver.setShouldDeleteMessages(false);
		Object m[] = receiver.receive();
		log.debug("count= " + m.length);
		for (Object mo : m) {
			log.debug("count= " + mo.getClass().getName());
			if (mo instanceof MimeMessage) {
				MimeMessage mm = (MimeMessage) mo;
				MimeMultipart mp = (MimeMultipart) mm.getContent();
				BodyPart bp = mp.getBodyPart(0);
				log.debug("body= " + bp.getContent());
			}
		}
	}

	private void pop3lesen() throws MessagingException, IOException {
		Properties properties = System.getProperties();
		Session session = Session.getDefaultInstance(properties);
		Store store = session.getStore("pop3");
		store.connect(config.HOST, config.BENUTZERNAME, config.PASSWORT);
		Folder inbox = store.getFolder("Inbox");
		inbox.open(Folder.READ_ONLY);

		Message[] messages = inbox.getMessages();

		if (messages.length == 0)
			log.debug("No messages found.");

		for (int i = 0; i < messages.length; i++) {
			if (i > 10) {
				System.exit(0);
				inbox.close(true);
				store.close();
			}

			log.debug("Message " + (i + 1));
			log.debug("From : " + messages[i].getFrom()[0]);
			log.debug("Subject : " + messages[i].getSubject());
			log.debug("Sent Date : " + messages[i].getSentDate());
			String text = extractBodyText(messages[i]);
			log.debug("body= " + extractBodyText(messages[i]));

			EingeleseneDatei datei = new EingeleseneDatei();
			datei.setArt(Verbindungsart.EMAIL);
			datei.setDaten(text);
			datei.setHost(messages[i].getFrom()[0].toString());
			datei.setHostverzeichnis("");
			datei.setErstellt(LocalDateTime.now());
			eingeleseneDateiRepo.saveAndFlush(datei);

		}

		inbox.close(true);
		store.close();
	}

	private String extractBodyText(Message message) throws IOException, MessagingException {
		MimeMultipart mp = (MimeMultipart) message.getContent();
		return extractBodyText(mp);
	}

	private String extractBodyText(MimeMultipart mp) throws MessagingException, IOException {
		BodyPart bp = mp.getBodyPart(0);
		Object om = bp.getContent();
		if (om instanceof String) {
			return (String) om;
		}
		if (om instanceof MimeMultipart) {
			return extractBodyText((MimeMultipart) om);
		}
		return "";
	}
}
