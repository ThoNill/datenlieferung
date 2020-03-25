package tho.nill.datenlieferung.allgemein;

import java.time.LocalDateTime;
import java.util.Optional;

import entities.DatenlieferungProtokoll;
import lombok.extern.slf4j.Slf4j;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;

@Slf4j
public class CreateProtokoll {

	private CreateProtokoll() {
		super();
	}

	public static Optional<DatenlieferungProtokoll> create(AktionsArt aktion, FehlerMeldung meldung, Exception e,
			Object... args) {
		DatenlieferungProtokoll p = new DatenlieferungProtokoll();
		p.setAktion(aktion);
		p.setAm(LocalDateTime.now());
		p.setFehler(meldung);
		if (e != null) {
			log.error("Exception in CreateProtokoll ", e);

			p.setExeption_class(e.getClass().getName());
			p.setExeption_meldung(e.getMessage());
		}
		p.setMeldung(String.format(meldung.meldung, args));
		return Optional.of(p);
	}
}
