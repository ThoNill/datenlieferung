package tho.nill.datenlieferung.nummernvergabe;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import entities.DateiNummer;
import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;
import interfaces.IDatenlieferung;
import interfaces.IRechnungAuftrag;
import lombok.NonNull;
import repositories.DateiNummerRepository;
import tho.nill.datenlieferung.allgemein.Action;
import tho.nill.datenlieferung.allgemein.CreateProtokoll;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.DateiNummerArt;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;
import tho.nill.datenlieferung.simpleAttributes.IK;

public class Nummervergabe implements Action {

	private DateiNummerRepository repo;

	public Nummervergabe(@NonNull DateiNummerRepository repo) {
		super();
		this.repo = repo;
	}

	@Override
	public Optional<DatenlieferungProtokoll> action(@NonNull Datenlieferung datenlieferung) {
		try {
			nummerieren(datenlieferung);
		} catch (Exception e) {
			return CreateProtokoll.create(AktionsArt.NUMMERIERT, FehlerMeldung.ERROR_NUMMERIERUNG, e,
					datenlieferung.getDatenlieferungId());
		}
		return Optional.empty();
	}

	public int getNeueCDNummer(@NonNull IRechnungAuftrag auftrag) {
		return getNumber(DateiNummerArt.CD_NUMMER, DatenArt.CD_NUMMER, auftrag.getVersenderIK(),
				auftrag.getVersenderIK(), 0);
	}

	public int getPar300Verbindung() {
		return getNumber(DateiNummerArt.PAR300VERBINDUNG, DatenArt.PAR300DATEN, new IK(0), new IK(0), 0);
	}

	@Transactional
	public int getNumber(@NonNull DateiNummerArt nummernArt, @NonNull DatenArt datenArt, @NonNull IK ik,
			@NonNull IK versenderIK, int jahr) {
		Optional<Long> id = repo.getId(nummernArt, datenArt, ik, versenderIK, jahr);
		if (id.isPresent()) {
			long idNummer = id.get();
			repo.updateAktuelleNummer(idNummer);
			return repo.getAktuelleNummer(idNummer);
		} else {
			DateiNummer nummer = new DateiNummer();
			nummer.setAktuelleNummer(1);
			nummer.setVersenderIK(versenderIK);
			nummer.setDatenArt(datenArt);
			nummer.setIk(ik);
			nummer.setJahr(jahr);
			nummer.setNummernArt(nummernArt);
			repo.saveAndFlush(nummer);
			return 1;
		}
	}

	public void nummerieren(@NonNull IDatenlieferung datenlieferung) {
		datenlieferung.setLieferJahr(LocalDateTime.now().getYear());
		switch (datenlieferung.getDatenArt()) {
		case PAR300ABRP:
		case PAR300RECP:
			bei300(datenlieferung);
			break;
		default:
			sonst(datenlieferung);
			datenlieferung.setKorrekturnummer(0);
			break;
		}
		datenlieferung.setLetzteAktion(AktionsArt.NUMMERIERT);
	}

	private void sonst(IDatenlieferung datenlieferung) {
		int transferDatenanahme = getNumber(DateiNummerArt.TRANSFERNUMMER_DATENANNAHME, DatenArt.PAR300DATEN,
				datenlieferung.getDatenAnnahmeIK(), datenlieferung.getVersenderIK(), datenlieferung.getLieferJahr());

		int dateinr = getNumber(DateiNummerArt.DATEINUMMER, datenlieferung.getDatenArt(),
				datenlieferung.getDatenPrüfungsIK(), datenlieferung.getVersenderIK(), datenlieferung.getLieferJahr());
		datenlieferung.setTransfernummer_datenannahme(transferDatenanahme);
		datenlieferung.setTransfernummer_vorprüfung(dateinr);
		datenlieferung.setDateinummer(dateinr);
	}

	private void bei300(IDatenlieferung datenlieferung) {
		int transferDatenanahme = getNumber(DateiNummerArt.TRANSFERNUMMER_DATENANNAHME, DatenArt.PAR300DATEN,
				datenlieferung.getDatenAnnahmeIK(), datenlieferung.getVersenderIK(), datenlieferung.getLieferJahr());
		int transferVorprüfung = getNumber(DateiNummerArt.TRANSFERNUMMER_VORPRÜFUNG, DatenArt.PAR300DATEN,
				datenlieferung.getDatenPrüfungsIK(), datenlieferung.getVersenderIK(), datenlieferung.getLieferJahr());
		int dateinrVorprüfung = getNumber(DateiNummerArt.DATEINUMMER, DatenArt.PAR300DATEN,
				datenlieferung.getDatenPrüfungsIK(), datenlieferung.getVersenderIK(), datenlieferung.getLieferJahr());
		datenlieferung.setTransfernummer_datenannahme(transferDatenanahme);
		datenlieferung.setTransfernummer_vorprüfung(transferVorprüfung);
		datenlieferung.setDateinummer(dateinrVorprüfung);
	}

	// TODO
	public void nummerierenBeiFehler(@NonNull IDatenlieferung datenlieferung,
			@NonNull IDatenlieferung ursprungsDatenlieferung) {
		switch (datenlieferung.getDatenArt()) {
		case PAR300ABRP:
		case PAR300RECP:
			int transferDatenanahme = getNumber(DateiNummerArt.TRANSFERNUMMER_DATENANNAHME, DatenArt.PAR300DATEN,
					datenlieferung.getDatenAnnahmeIK(), datenlieferung.getVersenderIK(),
					datenlieferung.getLieferJahr());
			int transferVorprüfung = getNumber(DateiNummerArt.TRANSFERNUMMER_VORPRÜFUNG, DatenArt.PAR300DATEN,
					datenlieferung.getDatenPrüfungsIK(), datenlieferung.getVersenderIK(),
					datenlieferung.getLieferJahr());
			datenlieferung.setTransfernummer_datenannahme(transferDatenanahme);
			datenlieferung.setTransfernummer_vorprüfung(transferVorprüfung);
			datenlieferung.setDateinummer(ursprungsDatenlieferung.getDateinummer());

			int korrnr = repo.getNächsteKorrekturNummer(ursprungsDatenlieferung.getDatenlieferungId());
			datenlieferung.setKorrekturnummer(korrnr);
			break;
		default:
			break;
		}
	}

}
