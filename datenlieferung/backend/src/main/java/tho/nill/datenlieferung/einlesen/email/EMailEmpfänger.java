package tho.nill.datenlieferung.einlesen.email;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

import entities.DatenlieferungProtokoll;
import entities.EingeleseneDatei;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import repositories.EingeleseneDateiRepository;
import tho.nill.datenlieferung.allgemein.CreateProtokoll;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

@Slf4j
public class EMailEmpfänger {
	private EingeleseneDateiRepository eingeleseneDateiRepo;

	private Pop3Configuration config;

	public EMailEmpfänger(@NonNull Pop3Configuration config, @NonNull EingeleseneDateiRepository eingeleseneDateiRepo) {
		super();
		this.config = config;
		this.eingeleseneDateiRepo = eingeleseneDateiRepo;
	}

	public Optional<DatenlieferungProtokoll> action() {
		try {
			pop3lesen();
		} catch (MessagingException | IOException e) {
			log.error("Exception in " + this.getClass().getCanonicalName() + "action", e);
			return CreateProtokoll.create(AktionsArt.RÜCKMELDUNG, FehlerMeldung.EINGANG_EMAIL, e);
		}
		return Optional.empty();
	}

	private void pop3lesen() throws MessagingException, IOException {
		Properties properties = System.getProperties();
		Session session = Session.getDefaultInstance(properties);
		Store store = session.getStore("pop3");
		store.connect(config.HOST, config.BENUTZERNAME, config.PASSWORT);
		Folder inbox = store.getFolder("Inbox");
		inbox.open(Folder.READ_ONLY);

		// get the list of inbox messages
		Message[] messages = inbox.getMessages();

		if (messages.length == 0) {
			log.debug("No messages found.");
		}

		for (int i = 0; i < messages.length; i++) {
			// stop after listing ten messages
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

			neueEingeleseneDatei(messages, i, text);

		}

		inbox.close(true);
		store.close();
	}

	private EingeleseneDatei neueEingeleseneDatei(Message[] messages, int i, String text) throws MessagingException {
		EingeleseneDatei datei = new EingeleseneDatei();
		datei.setArt(Verbindungsart.EMAIL);
		datei.setDaten(text);
		datei.setEmailFrom(messages[i].getFrom()[0].toString());
		datei.setHostverzeichnis("");
		datei.setErstellt(LocalDateTime.now());
		return eingeleseneDateiRepo.saveAndFlush(datei);
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