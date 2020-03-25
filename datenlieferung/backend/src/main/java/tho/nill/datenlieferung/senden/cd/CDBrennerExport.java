package tho.nill.datenlieferung.senden.cd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import repositories.DatenlieferungRepository;
import tho.nill.datenlieferung.allgemein.Action;
import tho.nill.datenlieferung.allgemein.CreateProtokoll;
import tho.nill.datenlieferung.allgemein.Dateien;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;

@Slf4j
public class CDBrennerExport extends Verzeichnisse implements Action {

	protected TemplateEngine templateEngine;
	protected DatenlieferungRepository datenlieferungen;

	public CDBrennerExport(@NonNull TemplateEngine templateEngine, @NonNull DatenlieferungRepository datenlieferungen,
			@NonNull String datenPathOriginal, @NonNull String datenPathVerschl) {
		super(datenPathOriginal, datenPathVerschl);
		this.templateEngine = templateEngine;
		this.datenlieferungen = datenlieferungen;
	}

	@Override
	public Optional<DatenlieferungProtokoll> action(@NonNull Datenlieferung datenlieferung) {
		try {
			erstelle(datenlieferung.getCdnummer());
		} catch (Exception e) {
			log.error("Exception in " + this.getClass().getCanonicalName() + "action", e);
			return CreateProtokoll.create(AktionsArt.GESENDET, FehlerMeldung.FILE_CD_BRENNER, e,
					datenlieferung.getCdnummer());
		}
		return Optional.empty();
	}

	public void erstelle(int cdnummer) throws IOException {
		List<Datenlieferung> liste = datenlieferungen.getDatenlieferungenZurCD(cdnummer);

		File file = getBrennerFile(cdnummer);
		try (FileOutputStream out = Dateien.createOutputStream(file)) {
			listeDurcharbeiten(liste, out);
		}
	}

	private void listeDurcharbeiten(@NonNull List<Datenlieferung> liste, @NonNull FileOutputStream out)
			throws IOException {
		for (Datenlieferung datenlieferung : liste) {
			final Context context = new Context();
			context.setVariable("id", datenlieferung.getDatenlieferungId());
			context.setVariable("physDateiname", getVerschlFile(datenlieferung).getAbsolutePath());
			context.setVariable("cdnr", datenlieferung.getCdnummer());
			context.setVariable("erstellt", TTMMJJJJ(datenlieferung.getErstellt()));
			context.setVariable("gesendet", TTMMJJJJ(datenlieferung.getGesendet()));
			context.setVariable("jahr", datenlieferung.getMj().getJahr());
			context.setVariable("monat", datenlieferung.getMj().getMonat());
			String body = templateEngine.process("brenner.txt", context);
			log.debug("Ausgabe: " + body);

			out.write(body.getBytes(StandardCharsets.UTF_8));
		}
	}

	protected String TTMMJJJJ(LocalDateTime datum) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		return datum.format(formatter);
	}

}
