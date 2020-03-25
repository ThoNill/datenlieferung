package tho.nill.datenlieferung.senden.cd;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import entities.Adresse;
import lombok.NonNull;
import repositories.AdresseRepository;
import repositories.DatenaustauschRepository;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;

public class Beleg extends Verzeichnisse {

	protected TemplateEngine templateEngine;
	protected DatenaustauschRepository datenaustauschRepo;
	protected AdresseRepository adresseRepo;

	public Beleg(@NonNull TemplateEngine templateEngine, @NonNull DatenaustauschRepository datenaustauschRepo,
			@NonNull AdresseRepository adresseRepo, @NonNull String datenPathOriginal,
			@NonNull String datenPathVerschl) {
		super(datenPathOriginal, datenPathVerschl);
		this.templateEngine = templateEngine;
		this.datenaustauschRepo = datenaustauschRepo;
		this.adresseRepo = adresseRepo;
	}

	protected void setzeAdresse(String prefix, Adresse adresse, final Context context) {
		context.setVariable(prefix + "_ik", adresse.getIk().toString());
		context.setVariable(prefix + "_firma", adresse.getFirma());
		context.setVariable(prefix + "_ansprechpartner", adresse.getAnsprechpartner());
		context.setVariable(prefix + "_plz", adresse.getPlz());
		context.setVariable(prefix + "_ort", adresse.getOrt());
		context.setVariable(prefix + "_strasse", adresse.getStraße());
		context.setVariable(prefix + "_telefon", adresse.getTelefon());
	}

	protected String TTMMJJJJ(LocalDateTime datum) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		return datum.format(formatter);
	}

}