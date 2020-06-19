package tho.nill.datenlieferung;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import entities.Datenlieferung;
import entities.RechnungAuftrag;
import repositories.DateiNummerRepository;
import repositories.DatenlieferungRepository;
import repositories.RechnungAuftragRepository;
import tho.nill.datenlieferung.allgemein.Dateinamen;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;
import tho.nill.datenlieferung.nummernvergabe.Nummervergabe;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.DateiNummerArt;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;

@Service
public class KorrekturErstellenService extends BasisService<Long> {
	@Autowired
	public RechnungAuftragRepository rechnungAuftragRepo;

	@Autowired
	DateiNummerRepository dateiNummernRepo;

	@Autowired
	private DatenlieferungRepository datenlieferungRepo;

	public KorrekturErstellenService(PlatformTransactionManager transactionManager) {
		super(transactionManager);
	}

	@Override
	public void performService(Long id) {
		Nummervergabe nummernVergabe = new Nummervergabe(dateiNummernRepo);
		Optional<Datenlieferung> datenlieferungOpt = datenlieferungRepo.findById(id);
		if (datenlieferungOpt.isPresent()) {
			Datenlieferung alteDatenlieferung = datenlieferungOpt.get();
			int cdNummer = nummernVergabe.getNumber(DateiNummerArt.CD_NUMMER, DatenArt.CD_NUMMER,
					alteDatenlieferung.getVersenderIK(), alteDatenlieferung.getVersenderIK(), 0);

			List<Datenlieferung> liste = datenlieferungRepo
					.getDatenlieferungenZurVerbindung(alteDatenlieferung.getPar300Verbindung());
			int neueVerbindungsNummer = nummernVergabe.getPar300Verbindung();
			for (Datenlieferung datenlieferung : liste) {
				eineDatenlieferungBearbeiten(datenlieferung, neueVerbindungsNummer, cdNummer);
			}
		} else {
			throw new DatenlieferungException("Keine Datenlieferung für id = " + id);
		}

	}

	private void eineDatenlieferungBearbeiten(Datenlieferung alteDatenlieferung, int verbindungsNummer, int cdNummer) {

		Datenlieferung neueDatenlieferung = erzeugeDatenlieferung(alteDatenlieferung, verbindungsNummer, cdNummer);

		Set<RechnungAuftrag> alteListe = alteDatenlieferung.getRechnungAuftrag();
		for (RechnungAuftrag a : alteListe) {
			RechnungAuftrag na = erzeugeRechnungAuftrag(a);
			neueDatenlieferung.addRechnungAuftrag(na);
		}

		datenlieferungRepo.saveAndFlush(neueDatenlieferung);
	}

	private RechnungAuftrag erzeugeRechnungAuftrag(RechnungAuftrag a) {
		RechnungAuftrag na = new RechnungAuftrag();
		na.setDatenAnnahmeIK(a.getDatenAnnahmeIK());
		na.setDatenArt(na.getDatenArt());
		na.setDatenPrüfungsIK(a.getDatenPrüfungsIK());
		na.setVersenderIK(a.getVersenderIK());
		na.setRechnungsNummer(a.getRechnungsNummer());
		return rechnungAuftragRepo.saveAndFlush(na);
	}

	private Datenlieferung erzeugeDatenlieferung(Datenlieferung alteDatenlieferung, int verbindungsNummer,
			int cdNummer) {
		Datenlieferung datenlieferung = new Datenlieferung();

		datenlieferung.setPar300Verbindung(verbindungsNummer);
		datenlieferung.setLieferJahr(LocalDateTime.now().getYear());
		datenlieferung.setMj(alteDatenlieferung.getMj());
		datenlieferung.setVersenderIK(alteDatenlieferung.getVersenderIK());
		datenlieferung.setDatenAnnahmeIK(alteDatenlieferung.getDatenAnnahmeIK());
		datenlieferung.setDatenPrüfungsIK(alteDatenlieferung.getDatenPrüfungsIK());
		datenlieferung.setDatenArt(alteDatenlieferung.getDatenArt());
		datenlieferung.setLetzteAktion(AktionsArt.NUMMERIERT);
		datenlieferung.setDateinummer(alteDatenlieferung.getDateinummer());

		Nummervergabe nummernVergabe = new Nummervergabe(dateiNummernRepo);
		int transferDatenanahme = nummernVergabe.getNumber(DateiNummerArt.TRANSFERNUMMER_DATENANNAHME,
				DatenArt.PAR300DATEN, alteDatenlieferung.getDatenAnnahmeIK(), alteDatenlieferung.getVersenderIK(),
				alteDatenlieferung.getLieferJahr());
		datenlieferung.setTransfernummer_datenannahme(transferDatenanahme);
		int transferVorprüfung = nummernVergabe.getNumber(DateiNummerArt.TRANSFERNUMMER_VORPRÜFUNG,
				DatenArt.PAR300DATEN, alteDatenlieferung.getDatenPrüfungsIK(), alteDatenlieferung.getVersenderIK(),
				alteDatenlieferung.getLieferJahr());
		datenlieferung.setTransfernummer_vorprüfung(transferVorprüfung);
		datenlieferung.setTestKennzeichen(alteDatenlieferung.getTestKennzeichen());
		datenlieferung.setCdnummer(cdNummer);
		datenlieferung.setErstellt(LocalDateTime.now());
		int korrekturnummer = datenlieferungRepo.getKorrekturnummer(alteDatenlieferung.getLieferJahr(),
				alteDatenlieferung.getMj(), alteDatenlieferung.getVersenderIK(),
				alteDatenlieferung.getDatenPrüfungsIK(), alteDatenlieferung.getDatenArt(),
				alteDatenlieferung.getDateinummer());
		datenlieferung.setKorrekturnummer(korrekturnummer);
		datenlieferung.setLogDateiname(Dateinamen.berechneLogDateinamen(datenlieferung));
		datenlieferung.setPhysDateiname(Dateinamen.berechnePhysDateinamen(datenlieferung));
		return datenlieferungRepo.saveAndFlush(datenlieferung);
	}
}
