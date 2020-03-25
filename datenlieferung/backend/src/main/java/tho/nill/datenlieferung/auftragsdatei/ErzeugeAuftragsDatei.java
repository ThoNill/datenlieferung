package tho.nill.datenlieferung.auftragsdatei;

import java.io.File;
import java.util.Optional;

import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;
import lombok.NonNull;
import tho.nill.datenlieferung.allgemein.Action;
import tho.nill.datenlieferung.allgemein.CreateProtokoll;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;

public class ErzeugeAuftragsDatei extends Verzeichnisse implements Action {

	public ErzeugeAuftragsDatei(@NonNull String datenPathOriginal, @NonNull String datenPathVerschl) {
		super(datenPathOriginal, datenPathVerschl);
	}

	@Override
	public Optional<DatenlieferungProtokoll> action(@NonNull Datenlieferung datenlieferung) {

		File file = getAuftragsFile(datenlieferung);
		try {
			AuftragsFelder.auftragsdatei(datenlieferung, file);
		} catch (Exception e) {
			return CreateProtokoll.create(AktionsArt.AUFTRAGSDATEI, FehlerMeldung.FILE_AUFTRAGSDATEI, e,
					file.getAbsolutePath());
		}
		return Optional.empty();
	}

}
