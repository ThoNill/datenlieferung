package tho.nill.datenlieferung.einlesen;

import java.time.LocalDateTime;
import java.util.Optional;

import entities.DatenlieferungProtokoll;
import entities.EingeleseneDatei;
import lombok.NonNull;
import repositories.EingeleseneDateiRepository;
import tho.nill.datenlieferung.allgemein.CreateProtokoll;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;

public class ErzeugeEingeleseneDatei extends Verzeichnisse {
	private EingeleseneDateiRepository eingeleseneDateiRepo;

	public ErzeugeEingeleseneDatei(@NonNull String datenPathOriginal, @NonNull String datenPathVerschl,
			EingeleseneDateiRepository eingeleseneDateiRepo) {
		super(datenPathOriginal, datenPathVerschl);
		this.eingeleseneDateiRepo = eingeleseneDateiRepo;
	}

	public Optional<DatenlieferungProtokoll> action(@NonNull EingeleseneDateiDaten daten) {
		try {
			EingeleseneDatei datei = new EingeleseneDatei();
			datei.setErstellt(LocalDateTime.now());
			datei.setHost(daten.getHost());
			datei.setHostverzeichnis(daten.getHostverzeichnis());
			datei.setArt(daten.getArt());
			datei.setDaten(daten.getDaten());
			eingeleseneDateiRepo.saveAndFlush(datei);
		} catch (Exception e) {
			return CreateProtokoll.create(AktionsArt.RÜCKMELDUNG, FehlerMeldung.FILE_AUFTRAGSDATEI, e, daten.getHost());
		}
		return Optional.empty();
	}

}
