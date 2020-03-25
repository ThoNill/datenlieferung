package tho.nill.datenlieferung.senden.email;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import entities.Adresse;
import entities.Datenaustausch;
import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import repositories.AdresseRepository;
import repositories.DatenaustauschRepository;
import tho.nill.datenlieferung.allgemein.Action;
import tho.nill.datenlieferung.allgemein.CreateProtokoll;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;
import tho.nill.datenlieferung.simpleAttributes.AdressArt;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

@Slf4j
public class EMailSender extends Verzeichnisse implements Action {

	private TemplateEngine templateEngine;
	private DatenaustauschRepository datenaustauschRepo;
	private AdresseRepository adresseRepo;

	public EMailSender(@NonNull TemplateEngine templateEngine, @NonNull DatenaustauschRepository datenaustauschRepo,
			@NonNull AdresseRepository adresseRepo, @NonNull String datenPathOriginal,
			@NonNull String datenPathVerschl) {
		super(datenPathOriginal, datenPathVerschl);
		this.templateEngine = templateEngine;
		this.datenaustauschRepo = datenaustauschRepo;
		this.adresseRepo = adresseRepo;
	}

	@Override
	public Optional<DatenlieferungProtokoll> action(@NonNull Datenlieferung datenlieferung) {
		try {
			List<Datenaustausch> da = datenaustauschRepo.sucheDatenaustausch(datenlieferung.getVersenderIK(),
					datenlieferung.getDatenAnnahmeIK(), datenlieferung.getDatenPrüfungsIK(), Richtung.AUSGANG,
					datenlieferung.getDatenArt(), Verbindungsart.EMAIL);
			if (!da.isEmpty()) {
				List<Adresse> a = adresseRepo.getAdresse(datenlieferung.getVersenderIK(), AdressArt.DATENLIEFERUNG);
				if (a.isEmpty()) {
					throw new DatenlieferungException(
							"Adresse zur Datenlieferung nicht bekannt für " + datenlieferung.getDatenlieferungId());
				}
				senden(datenlieferung, da.get(0), a.get(0));
			} else {
				throw new DatenlieferungException(
						"Keine Angabe zum Datenaustausch für " + datenlieferung.getDatenlieferungId());
			}
		} catch (MessagingException e) {
			log.error("Exception in " + this.getClass().getCanonicalName() + "action", e);
			return CreateProtokoll.create(AktionsArt.GESENDET, FehlerMeldung.FILE_EMAIL, e,
					datenlieferung.getDatenlieferungId());

		}
		return Optional.empty();
	}

	public void senden(@NonNull Datenlieferung datenlieferung, @NonNull Datenaustausch austausch,
			@NonNull Adresse adresse) throws MessagingException {
		checkDatenAustausch(austausch);

		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		log.debug("austausch: " + austausch.toString());
		sender.setHost(austausch.getHost());
		sender.setUsername(austausch.getLoginNutzer());
		sender.setPassword(austausch.getLoginPasswort());
		sender.setPort(austausch.getPort());

		MimeMessage message = sender.createMimeMessage();

// use the true flag to indicate you need a multipart message
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setTo(austausch.getEmailTo());
		helper.setFrom(austausch.getEmailFrom());
		helper.setSentDate(java.sql.Timestamp.valueOf(datenlieferung.getGesendet()));
		helper.setSubject(datenlieferung.getVersenderIK().toString());

		final Context context = new Context();
		context.setVariable("datei", datenlieferung.getPhysDateiname());
		context.setVariable("verschl", datenlieferung.getDateigröße_übertragung());
		context.setVariable("original", datenlieferung.getDateigröße_nutzdaten());
		context.setVariable("datum", JJJJMMTTHHMMSS(datenlieferung.getErstellt()));
		context.setVariable("firmenname", adresse.getFirma());
		context.setVariable("ansprechpartner", adresse.getAnsprechpartner());
		context.setVariable("email", adresse.getEmail());
		context.setVariable("telefon", adresse.getTelefon());
		String body = templateEngine.process("emailKassen.txt", context);

		helper.setText(body);
// let's attach the infamous windows Sample file (this time copied to c:/)
		File auftragsFile = getAuftragsFile(datenlieferung);
		FileSystemResource fileAuftrag = new FileSystemResource(auftragsFile);
		helper.addAttachment(datenlieferung.getPhysDateiname() + ".AUF", fileAuftrag);

		File verschlFile = getVerschlFile(datenlieferung);
		FileSystemResource file = new FileSystemResource(verschlFile);
		helper.addAttachment(datenlieferung.getPhysDateiname(), file);

		sender.send(message);
	}

	private String JJJJMMTTHHMMSS(LocalDateTime datum) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd:HHmmss");
		return datum.format(formatter);
	}

	private void checkDatenAustausch(Datenaustausch austausch) {
		if (austausch.getLoginNutzer() == null) {
			throw new DatenlieferungException(
					"Keine Angabe zum Datenaustausch für getLoginNutzer ID= " + austausch.getDatenaustauschId());

		}
		if (austausch.getLoginPasswort() == null) {
			throw new DatenlieferungException(
					"Keine Angabe zum Datenaustausch für getLoginPasswort ID= " + austausch.getDatenaustauschId());

		}
		if (austausch.getHost() == null) {
			throw new DatenlieferungException(
					"Keine Angabe zum Datenaustausch für getHost ID= " + austausch.getDatenaustauschId());

		}
		if (austausch.getPort() == 0) {
			throw new DatenlieferungException(
					"Keine Angabe zum Datenaustausch für getPort ID= " + austausch.getDatenaustauschId());

		}
		if (austausch.getEmailTo() == null) {
			throw new DatenlieferungException(
					"Keine Angabe zum Datenaustausch für getEmailTo ID= " + austausch.getDatenaustauschId());

		}
	}
}