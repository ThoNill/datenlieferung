package tho.nill.datenlieferung.allgemein;

import java.util.Optional;

import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;

public interface Action {
	Optional<DatenlieferungProtokoll> action(Datenlieferung datenlieferung);
}
