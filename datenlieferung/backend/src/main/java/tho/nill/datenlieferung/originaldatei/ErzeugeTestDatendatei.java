package tho.nill.datenlieferung.originaldatei;

import java.io.File;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Optional;

import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import tho.nill.datenlieferung.allgemein.Action;
import tho.nill.datenlieferung.allgemein.CreateProtokoll;
import tho.nill.datenlieferung.allgemein.Dateien;
import tho.nill.datenlieferung.allgemein.Dateinamen;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;

@Slf4j
public class ErzeugeTestDatendatei extends Verzeichnisse implements Action {

	public ErzeugeTestDatendatei(@NonNull String datenPathOriginal, @NonNull String datenPathVerschl) {
		super(datenPathOriginal, datenPathVerschl);
	}

	@Override
	public Optional<DatenlieferungProtokoll> action(@NonNull Datenlieferung datenlieferung) {
		File file = getOrginalFile(datenlieferung);
		log.debug("ErzeugeTestDatendatei " + file.getAbsolutePath());
		try (OutputStream out = Dateien.createOutputStream(file)) {
			out.write(datenlieferung.toString().getBytes());
		} catch (Exception e) {
			return CreateProtokoll.create(AktionsArt.ERSTELLT, FehlerMeldung.FILE_NOT_WRITABLE, e,
					file.getAbsoluteFile());
		}
		datenlieferung.setLogDateiname(Dateinamen.berechneLogDateinamen(datenlieferung));

		datenlieferung.setPhysDateiname(Dateinamen.berechnePhysDateinamen(datenlieferung));

		datenlieferung.setDateigröße_nutzdaten(file.length());
		datenlieferung.setErstellt(LocalDateTime.now());
		return Optional.empty();
	}

}
