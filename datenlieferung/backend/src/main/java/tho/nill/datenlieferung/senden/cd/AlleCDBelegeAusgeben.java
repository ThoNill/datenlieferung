package tho.nill.datenlieferung.senden.cd;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.thymeleaf.TemplateEngine;

import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;
import lombok.NonNull;
import repositories.AdresseRepository;
import repositories.DatenaustauschRepository;
import repositories.DatenlieferungRepository;
import tho.nill.datenlieferung.allgemein.Action;
import tho.nill.datenlieferung.allgemein.CreateProtokoll;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;

public class AlleCDBelegeAusgeben extends Verzeichnisse implements Action {
	private TemplateEngine templateEngine;
	private DatenaustauschRepository datenaustauschRepo;
	private AdresseRepository adresseRepo;
	private DatenlieferungRepository datenlieferungenRepo;
	private String datenPathOriginal;
	private String datenPathVerschl;

	public AlleCDBelegeAusgeben(@NonNull TemplateEngine templateEngine,
			@NonNull DatenaustauschRepository datenaustauschRepo, @NonNull AdresseRepository adresseRepo,
			@NonNull DatenlieferungRepository datenlieferungenRepo, String datenPathOriginal, String datenPathVerschl) {
		super(datenPathOriginal, datenPathVerschl);
		this.templateEngine = templateEngine;
		this.datenaustauschRepo = datenaustauschRepo;
		this.adresseRepo = adresseRepo;
		this.datenlieferungenRepo = datenlieferungenRepo;
		this.datenPathOriginal = datenPathOriginal;
		this.datenPathVerschl = datenPathVerschl;
	}

	@Override
	public Optional<DatenlieferungProtokoll> action(@NonNull Datenlieferung datenlieferung) {
		try {
			return erstelle(datenlieferung.getCdnummer());
		} catch (Exception e) {
			return CreateProtokoll.create(AktionsArt.GESENDET, FehlerMeldung.FILE_CD_BELEGE, e,
					datenlieferung.getCdnummer());
		}

	}

	public Optional<DatenlieferungProtokoll> erstelle(int cdnummer) throws IOException {
		List<Datenlieferung> liste = datenlieferungenRepo.getDatenlieferungenZurCD(cdnummer);
		if (liste.isEmpty()) {
			throw new DatenlieferungException(" Liste ist leer für CD " + cdnummer);
		} else {
			Datenlieferung kopf = liste.get(0);
			Optional<DatenlieferungProtokoll> error = new CDBrennerExport(templateEngine, datenlieferungenRepo,
					datenPathOriginal, datenPathVerschl).action(kopf);
			if (error.isPresent()) {
				return error;
			}
			error = new CDBrief(templateEngine, datenaustauschRepo, adresseRepo, datenPathOriginal, datenPathVerschl)
					.action(kopf);
			if (error.isPresent()) {
				return error;
			}

			for (Datenlieferung datenlieferung : liste) {
				error = new Begleitzettel(templateEngine, datenaustauschRepo, adresseRepo, datenPathOriginal,
						datenPathVerschl).action(datenlieferung);
				if (error.isPresent()) {
					return error;
				}

			}
		}
		return Optional.empty();
	}

}
