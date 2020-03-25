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
		Nummervergabe nummernVergabe = new Nummervergabe(dateiNummernRepo);

		Datenlieferung neueDatenlieferung = new Datenlieferung();

		neueDatenlieferung.setPar300Verbindung(verbindungsNummer);
		neueDatenlieferung.setLieferJahr(LocalDateTime.now().getYear());
		neueDatenlieferung.setMj(alteDatenlieferung.getMj());
		neueDatenlieferung.setVersenderIK(alteDatenlieferung.getVersenderIK());
		neueDatenlieferung.setDatenAnnahmeIK(alteDatenlieferung.getDatenAnnahmeIK());
		neueDatenlieferung.setDatenPrüfungsIK(alteDatenlieferung.getDatenPrüfungsIK());
		neueDatenlieferung.setDatenArt(alteDatenlieferung.getDatenArt());
		neueDatenlieferung.setLetzteAktion(AktionsArt.NUMMERIERT);
		neueDatenlieferung.setDateinummer(alteDatenlieferung.getDateinummer());
		int transferDatenanahme = nummernVergabe.getNumber(DateiNummerArt.TRANSFERNUMMER_DATENANNAHME,
				DatenArt.PAR300DATEN, alteDatenlieferung.getDatenAnnahmeIK(), alteDatenlieferung.getVersenderIK(),
				alteDatenlieferung.getLieferJahr());
		neueDatenlieferung.setTransfernummer_datenannahme(transferDatenanahme);
		int transferVorprüfung = nummernVergabe.getNumber(DateiNummerArt.TRANSFERNUMMER_VORPRÜFUNG,
				DatenArt.PAR300DATEN, alteDatenlieferung.getDatenPrüfungsIK(), alteDatenlieferung.getVersenderIK(),
				alteDatenlieferung.getLieferJahr());
		neueDatenlieferung.setTransfernummer_vorprüfung(transferVorprüfung);
		neueDatenlieferung.setTestKennzeichen(alteDatenlieferung.getTestKennzeichen());
		neueDatenlieferung.setCdnummer(cdNummer);
		neueDatenlieferung.setErstellt(LocalDateTime.now());
		int korrekturnummer = datenlieferungRepo.getKorrekturnummer(alteDatenlieferung.getLieferJahr(),
				alteDatenlieferung.getMj(), alteDatenlieferung.getVersenderIK(),
				alteDatenlieferung.getDatenPrüfungsIK(), alteDatenlieferung.getDatenArt(),
				alteDatenlieferung.getDateinummer());
		neueDatenlieferung.setKorrekturnummer(korrekturnummer);
		neueDatenlieferung.setLogDateiname(Dateinamen.berechneLogDateinamen(neueDatenlieferung));
		neueDatenlieferung.setPhysDateiname(Dateinamen.berechnePhysDateinamen(neueDatenlieferung));
		datenlieferungRepo.saveAndFlush(neueDatenlieferung);

		Set<RechnungAuftrag> alteListe = alteDatenlieferung.getRechnungAuftrag();
		for (RechnungAuftrag a : alteListe) {
			RechnungAuftrag na = new RechnungAuftrag();
			na.setDatenAnnahmeIK(a.getDatenAnnahmeIK());
			na.setDatenArt(na.getDatenArt());
			na.setDatenPrüfungsIK(a.getDatenPrüfungsIK());
			na.setVersenderIK(a.getVersenderIK());
			na.setRechnungsNummer(a.getRechnungsNummer());
			rechnungAuftragRepo.saveAndFlush(na);
			neueDatenlieferung.addRechnungAuftrag(na);
		}

		datenlieferungRepo.saveAndFlush(neueDatenlieferung);
	}
}
