package tho.nill.datenlieferung.senden.cd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import entities.Adresse;
import entities.Datenaustausch;
import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;
import lombok.NonNull;
import repositories.AdresseRepository;
import repositories.DatenaustauschRepository;
import tho.nill.datenlieferung.allgemein.Action;
import tho.nill.datenlieferung.allgemein.CreateProtokoll;
import tho.nill.datenlieferung.allgemein.Dateien;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;
import tho.nill.datenlieferung.simpleAttributes.AdressArt;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

public class Begleitzettel extends Beleg implements Action {

	public Begleitzettel(@NonNull TemplateEngine templateEngine, @NonNull DatenaustauschRepository datenaustauschRepo,
			@NonNull AdresseRepository adresseRepo, @NonNull String datenPathOriginal,
			@NonNull String datenPathVerschl) {
		super(templateEngine, datenaustauschRepo, adresseRepo, datenPathOriginal, datenPathVerschl);
	}

	@Override
	public Optional<DatenlieferungProtokoll> action(@NonNull Datenlieferung datenlieferung) {
		try {
			List<Datenaustausch> datenaustauschListe = datenaustauschRepo.sucheDatenaustausch(
					datenlieferung.getVersenderIK(), datenlieferung.getDatenAnnahmeIK(),
					datenlieferung.getDatenPrüfungsIK(), Richtung.AUSGANG, datenlieferung.getDatenArt(),
					Verbindungsart.CD);
			if (!datenaustauschListe.isEmpty()) {
				List<Adresse> adressenVersenderListe = adresseRepo.getAdresse(datenlieferung.getVersenderIK(),
						AdressArt.FIRMA);
				if (adressenVersenderListe.isEmpty()) {
					throw new DatenlieferungException(
							"Adresse des Versenders nicht bekannt für " + datenlieferung.getDatenlieferungId());
				}
				List<Adresse> adressenEmpfängerListe = adresseRepo.getAdresse(datenlieferung.getDatenPrüfungsIK(),
						AdressArt.DATENLIEFERUNG);
				if (adressenEmpfängerListe.isEmpty()) {
					throw new DatenlieferungException(
							"Adresse des Empfängers nicht bekannt für " + datenlieferung.getDatenlieferungId());
				}
				List<Adresse> adressenBearbeiterListe = adresseRepo.getAdresse(datenlieferung.getVersenderIK(),
						AdressArt.DATENLIEFERUNG);
				if (adressenBearbeiterListe.isEmpty()) {
					throw new DatenlieferungException(
							"Adresse des Bearbeiters nicht bekannt für " + datenlieferung.getDatenlieferungId());
				}
				senden(datenlieferung, adressenVersenderListe.get(0), adressenEmpfängerListe.get(0),
						adressenBearbeiterListe.get(0));
			}
		} catch (Exception e) {
			return CreateProtokoll.create(AktionsArt.GESENDET, FehlerMeldung.FILE_CD_BEGLEITZETTEL, e,
					datenlieferung.getDatenlieferungId());

		}
		return Optional.empty();
	}

	public void senden(@NonNull Datenlieferung datenlieferung, @NonNull Adresse adresseVersender,
			@NonNull Adresse adresseEmpfänger, @NonNull Adresse bearbeiter) throws IOException {

		final Context context = new Context();
		context.setVariable("id", datenlieferung.getDatenlieferungId());
		context.setVariable("verfahren", datenlieferung.getDatenArt().verfahrensKennung);
		context.setVariable("spezifikation", datenlieferung.getDatenArt().verfahrensSpezifikation);
		context.setVariable("logDateiname", datenlieferung.getLogDateiname());
		context.setVariable("physDateiname", datenlieferung.getPhysDateiname());
		context.setVariable("cdnr", datenlieferung.getCdnummer());
		context.setVariable("erstellt", TTMMJJJJ(datenlieferung.getErstellt()));
		context.setVariable("gesendet", TTMMJJJJ(datenlieferung.getGesendet()));
		context.setVariable("jahr", datenlieferung.getMj().getJahr());
		context.setVariable("monat", datenlieferung.getMj().getMonat());

		setzeAdresse("abs", adresseVersender, context);
		setzeAdresse("empf", adresseEmpfänger, context);
		setzeAdresse("bearb", bearbeiter, context);
		String body = templateEngine.process("begleitbeleg.txt", context);
		File file = getBegleitzettelFile(datenlieferung);
		try (FileOutputStream out = Dateien.createOutputStream(file)) {
			out.write(body.getBytes(StandardCharsets.UTF_8));
		}
	}

}