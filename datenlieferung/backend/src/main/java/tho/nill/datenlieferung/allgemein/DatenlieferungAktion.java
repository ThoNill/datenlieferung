package tho.nill.datenlieferung.allgemein;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;
import lombok.extern.slf4j.Slf4j;
import repositories.DatenlieferungProtokollRepository;
import repositories.DatenlieferungRepository;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;

@Slf4j
public class DatenlieferungAktion implements Function<Datenlieferung, Datenlieferung> {
	private AktionsArt vorAktion;
	private AktionsArt nachAktion;

	private DatenlieferungRepository datenlieferungen;
	private DatenlieferungProtokollRepository protokolle;

	private Action aktion;

	public DatenlieferungAktion(AktionsArt vorAktion, AktionsArt nachAktion, DatenlieferungRepository datenlieferungen,
			DatenlieferungProtokollRepository protokolle, Action aktion) {
		super();
		this.vorAktion = vorAktion;
		this.nachAktion = nachAktion;
		this.datenlieferungen = datenlieferungen;
		this.protokolle = protokolle;
		this.aktion = aktion;
	}

	@Override
	public Datenlieferung apply(Datenlieferung t) {
		log.debug("Aktion:" + aktion.getClass().getCanonicalName());
		if (vorAktion.equals(t.getLetzteAktion())) {
			Optional<DatenlieferungProtokoll> error = aktion.action(t);
			if (error.isPresent()) {
				t.setLetzteAktion(nachAktion);
				DatenlieferungProtokoll p = error.get();
				p.setAktion(nachAktion);
				t.setFehler(p.getFehler().ordinal());
				protokolle.save(p);
				t.addDatenlieferungProtokoll(p);
				datenlieferungen.save(t);
				protokolle.save(p);
			} else {
				t.setLetzteAktion(nachAktion);
				t.setFehler(FehlerMeldung.SUCCESS.ordinal());
				switch (nachAktion) {
				case ANGELEGT:
					break;
				case AUFTRAGSDATEI:
					break;
				case ERSTELLT:
					t.setErstellt(LocalDateTime.now());
					break;
				case FEHLERBEHEBUNG:
					break;
				case GESENDET:
					t.setGesendet(LocalDateTime.now());
					break;
				case NUMMERIERT:
					break;
				case RÜCKMELDUNG:
					t.setBestätigt(LocalDateTime.now());
					break;
				case VERSCHLÜSSELT:
					break;
				default:
					break;

				}
				datenlieferungen.save(t);
			}
		}
		return t;
	}

}
