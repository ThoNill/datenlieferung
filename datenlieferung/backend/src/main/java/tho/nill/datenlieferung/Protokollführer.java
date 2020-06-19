package tho.nill.datenlieferung;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;
import repositories.DatenlieferungProtokollRepository;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;

@Service
public class Protokollführer {

	@Autowired
	DatenlieferungProtokollRepository datenlieferungProtokollRepo;

	public Protokollführer() {
		super();
	}

	public int protokolliere(AktionsArt aktionsArt, Datenlieferung datenlieferung, FehlerMeldung fmeldung,
			Object... args) {
		DatenlieferungProtokoll protokoll = new DatenlieferungProtokoll();
		if (datenlieferung != null) {
			protokoll.setDatenlieferung(datenlieferung);
		}
		protokoll.setAm(LocalDateTime.now());
		protokoll.setFehler(fmeldung);
		String meldung = String.format(fmeldung.meldung, args);
		protokoll.setMeldung(meldung);
		protokoll.setAktion(aktionsArt);
		datenlieferungProtokollRepo.saveAndFlush(protokoll);

		return fmeldung.ordinal();
	}
}
