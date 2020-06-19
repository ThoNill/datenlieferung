package tho.nill.datenlieferung.anlegen;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import entities.Datenlieferung;
import entities.RechnungAuftrag;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import repositories.DateiNummerRepository;
import repositories.DatenlieferungRepository;
import repositories.RechnungAuftragRepository;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;
import tho.nill.datenlieferung.nummernvergabe.Nummervergabe;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;

@Slf4j
public class Nähmaschine implements Function<Object[], List<Datenlieferung>> {
	private static final String RECHNUNG_AUFTRAG_MIT_ID = "RechnungAuftrag mit id = ";
	private DatenlieferungRepository datenlieferungen;
	private RechnungAuftragRepository aufträge;

	private Datenlieferung aktuelleDatenlieferung;
	private Datenlieferung recpDatenlieferung;
	private RechnungAuftrag letzterAuftrag;
	private Status status;
	private int cdNummer;
	int par300Verbindung;
	private Nummervergabe nummernVergabe;
	private int anzahl;

	public Nähmaschine(@NonNull DatenlieferungRepository datenlieferungen, @NonNull RechnungAuftragRepository aufträge,
			@NonNull DateiNummerRepository nummern) {
		super();
		this.datenlieferungen = datenlieferungen;
		this.aufträge = aufträge;
		this.nummernVergabe = new Nummervergabe(nummern);
	}

	@Override
	public List<Datenlieferung> apply(@NonNull Object[] werte) {
		anzahl++;
		for (int i = 0; i < werte.length; i++) {
			log.debug(" werte[" + i + "]=" + werte[i]);
		}
		log.debug("");
		Long id = (Long) werte[0];
		Optional<RechnungAuftrag> optionalAuftrag = aufträge.findById(id);
		if (optionalAuftrag.isPresent()) {
			RechnungAuftrag auftrag = optionalAuftrag.get();
			check(auftrag);
			return bearbeiteAuftrag(auftrag);
		} else {
			throw new DatenlieferungException(RECHNUNG_AUFTRAG_MIT_ID + id + " wurde nicht gefunden");
		}
	}

	public int getAnzahl() {
		return anzahl;
	}

	private void check(RechnungAuftrag auftrag) {
		if (auftrag.getDatenArt() == null) {
			throw new DatenlieferungException(
					RECHNUNG_AUFTRAG_MIT_ID + auftrag.getRechnungAuftragId() + " hat keine Datenart");
		}
		if (auftrag.getDatenAnnahmeIK() == null) {
			throw new DatenlieferungException(
					RECHNUNG_AUFTRAG_MIT_ID + auftrag.getRechnungAuftragId() + " hat keine DatenannahmeIK");
		}
		if (auftrag.getVersenderIK() == null) {
			throw new DatenlieferungException(
					RECHNUNG_AUFTRAG_MIT_ID + auftrag.getRechnungAuftragId() + " hat keine VersenderIK");
		}
		if (auftrag.getDatenPrüfungsIK() == null) {
			throw new DatenlieferungException(
					RECHNUNG_AUFTRAG_MIT_ID + auftrag.getRechnungAuftragId() + " hat keine PrüfungsIK");
		}

	}

	private List<Datenlieferung> bearbeiteAuftrag(RechnungAuftrag auftrag) {
		status = bestimmeStatus(auftrag);

		boolean mitRecpUndAbrpLieferung = DatenArt.PAR300DATEN.equals(auftrag.getDatenArt());

		switch (status) {
		case CD_BESTIMMEN:
			cdNummer = neueMediumId(auftrag);
		case NEUE_DATENLIEFERUNG:
			if (mitRecpUndAbrpLieferung) {
				par300Verbindung = nummernVergabe.getPar300Verbindung();
			} else {
				par300Verbindung = 0;
			}
			break;
		case EINFÄDELN:
			break;
		default:
			break;
		}

		if (mitRecpUndAbrpLieferung) {
			RechnungAuftrag recpAuftrag = copyAuftrag(auftrag);
			recpAuftrag.setDatenArt(DatenArt.PAR300RECP);
			recpDatenlieferung = jeNachStatusBearbeiten(recpDatenlieferung, recpAuftrag);
			auftrag.setDatenArt(DatenArt.PAR300ABRP);
		}

		aktuelleDatenlieferung = jeNachStatusBearbeiten(aktuelleDatenlieferung, auftrag);

		letzterAuftrag = copyAuftrag(auftrag);
		if (mitRecpUndAbrpLieferung) {
			letzterAuftrag.setDatenArt(DatenArt.PAR300DATEN);
		}

		if (status.equals(Status.EINFÄDELN)) {
			return Collections.emptyList();
		} else {
			if (mitRecpUndAbrpLieferung) {
				return asList(aktuelleDatenlieferung, recpDatenlieferung);
			} else {
				return asList(aktuelleDatenlieferung);
			}
		}
	}

	private RechnungAuftrag copyAuftrag(RechnungAuftrag auftrag) {
		RechnungAuftrag copyAuftrag = new RechnungAuftrag();
		copyAuftrag.setDatenAnnahmeIK(auftrag.getDatenAnnahmeIK());
		copyAuftrag.setDatenPrüfungsIK(auftrag.getDatenPrüfungsIK());
		copyAuftrag.setVersenderIK(auftrag.getVersenderIK());
		copyAuftrag.setMj(auftrag.getMj());
		copyAuftrag.setDatenArt(auftrag.getDatenArt());
		return copyAuftrag;
	}

	private Datenlieferung jeNachStatusBearbeiten(Datenlieferung datenlieferung, RechnungAuftrag auftrag) {
		switch (status) {
		case CD_BESTIMMEN:
		case NEUE_DATENLIEFERUNG:
			log.debug("Neue Datenlieferung mit CD Nummer " + cdNummer);
			speichere(datenlieferung);
			return neueDatenlieferungErzeugen(auftrag);
		case EINFÄDELN:
			einfädeln(datenlieferung, auftrag);
			log.debug("Einfädeln " + auftrag.getRechnungAuftragId() + " in Datenliefung "
					+ datenlieferung.getDatenlieferungId());
			break;
		}
		return datenlieferung;
	}

	private Datenlieferung neueDatenlieferungErzeugen(RechnungAuftrag auftrag) {
		Datenlieferung neueDatenlieferung = neueDatenlieferung(auftrag);
		log.debug("Neue Datenlieferung " + neueDatenlieferung.getDatenlieferungId());
		einfädeln(neueDatenlieferung, auftrag);
		return neueDatenlieferung;
	}

	private Datenlieferung neueDatenlieferung(RechnungAuftrag auftrag) {
		Datenlieferung d = createDatenlieferung(auftrag);
		nummernVergabe.nummerieren(d);
		datenlieferungen.save(d);
		return d;
	}

	private Datenlieferung createDatenlieferung(RechnungAuftrag auftrag) {
		Datenlieferung d = new Datenlieferung();
		d.setLieferJahr(LocalDateTime.now().getYear());
		d.setMj(auftrag.getMj());
		d.setDatenArt(auftrag.getDatenArt());
		d.setVersenderIK(auftrag.getVersenderIK());
		d.setDatenAnnahmeIK(auftrag.getDatenAnnahmeIK());
		d.setDatenPrüfungsIK(auftrag.getDatenPrüfungsIK());
		d.setTestKennzeichen("E");
		d.setCdnummer(cdNummer);
		if (DatenArt.PAR300ABRP.equals(auftrag.getDatenArt()) || DatenArt.PAR300RECP.equals(auftrag.getDatenArt())) {
			d.setPar300Verbindung(par300Verbindung);
		}
		d.setLetzteAktion(AktionsArt.ANGELEGT);
		return datenlieferungen.save(d);
	}

	private void einfädeln(Datenlieferung datenlieferung, RechnungAuftrag auftrag) {
		datenlieferung.addRechnungAuftrag(auftrag);
		aufträge.save(auftrag);
		datenlieferungen.save(datenlieferung);
	}

	private int neueMediumId(RechnungAuftrag auftrag) {
		return nummernVergabe.getNeueCDNummer(auftrag);

	}

	private void speichere(Datenlieferung datenlieferung) {
		if (datenlieferung != null) {
			datenlieferungen.save(datenlieferung);
		}
	}

	private Status bestimmeStatus(RechnungAuftrag auftrag) {
		if (letzterAuftrag == null || !letzterAuftrag.getMj().equals(auftrag.getMj())
				|| !letzterAuftrag.getDatenArt().equals(auftrag.getDatenArt())
				|| !letzterAuftrag.getVersenderIK().equals(auftrag.getVersenderIK())
				|| !letzterAuftrag.getDatenAnnahmeIK().equals(auftrag.getDatenAnnahmeIK())) {
			return Status.CD_BESTIMMEN;
		}
		if (!letzterAuftrag.getDatenPrüfungsIK().equals(auftrag.getDatenPrüfungsIK())) {
			return Status.NEUE_DATENLIEFERUNG;
		}
		return Status.EINFÄDELN;
	}

	private List<Datenlieferung> asList(Datenlieferung... args) {
		ArrayList<Datenlieferung> l = new ArrayList<>();
		for (Datenlieferung d : args) {
			l.add(d);
		}
		return l;
	}
}
