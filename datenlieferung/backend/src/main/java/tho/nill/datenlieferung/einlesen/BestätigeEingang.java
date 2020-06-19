package tho.nill.datenlieferung.einlesen;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import entities.Datenlieferung;
import entities.DatenlieferungProtokoll;
import entities.EingeleseneDatei;
import lombok.NonNull;
import repositories.DatenlieferungRepository;
import repositories.EingangTextRepository;
import repositories.EingeleseneDateiRepository;
import tho.nill.datenlieferung.Protokollführer;
import tho.nill.datenlieferung.allgemein.CreateProtokoll;
import tho.nill.datenlieferung.allgemein.Verzeichnisse;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.Bewertung;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;

public class BestätigeEingang extends Verzeichnisse implements Consumer {
	private EingeleseneDateiRepository eingeleseneDateiRepo;
	private TextAnalyse analyse;
	private DatenlieferungRepository datenlieferungRepo;

	private Protokollführer protokoll;
	private BewerteEingang bewerter;

	public BestätigeEingang(@NonNull String datenPathOriginal, @NonNull String datenPathVerschl,
			@NonNull EingeleseneDateiRepository eingeleseneDateiRepo,
			@NonNull DatenlieferungRepository datenlieferungRepo, @NonNull EingangTextRepository eingangTextRepo,
			@NonNull Protokollführer protokoll) {
		super(datenPathOriginal, datenPathVerschl);
		this.eingeleseneDateiRepo = eingeleseneDateiRepo;
		this.datenlieferungRepo = datenlieferungRepo;
		this.bewerter = new BewerteEingang(eingangTextRepo);
		this.protokoll = protokoll;
		this.analyse = new DefaultTextAnalyse(datenlieferungRepo, new DefaultSuchInfoExtractor(), protokoll);
	}

	public void init() {
		this.bewerter.init();
	}

	public Optional<DatenlieferungProtokoll> action(@NonNull EingeleseneDatei datei) {
		try {
			List<Long> datenlieferungIdList = analyse.analysieren(datei);
			Bewertung bewertung = bewerter.bewerten(datei.getDaten().toLowerCase());

			if (!datenlieferungIdList.isEmpty()) {
				for (long datenlieferungId : datenlieferungIdList) {
					bearbeiteDatenlieferung(datei, bewertung, datenlieferungId);
				}
			} else {
				keineDateiInfoImText(datei);
			}
		} catch (Exception e) {
			return CreateProtokoll.create(AktionsArt.RÜCKMELDUNG, FehlerMeldung.EINGANG_AUSNAHME, e,
					datei.getEingeleseneDateiId());
		}
		return Optional.empty();
	}

	private EingeleseneDatei keineDateiInfoImText(EingeleseneDatei datei) {
		int fehler;
		fehler = protokoll.protokolliere(AktionsArt.RÜCKMELDUNG, null, FehlerMeldung.EINGANG_KEINE_DATEIINFO,
				datei.getEingeleseneDateiId());
		datei.setFehler(fehler);
		return eingeleseneDateiRepo.saveAndFlush(datei);
	}

	private int bearbeiteDatenlieferung(EingeleseneDatei datei, Bewertung bewertung, long datenlieferungId) {
		int fehler = 0;
		Optional<Datenlieferung> datenlieferungOpt = datenlieferungRepo.findById(datenlieferungId);
		if (datenlieferungOpt.isPresent()) {
			Datenlieferung datenlieferung = datenlieferungOpt.get();

			fehler = bestimmeFehler(bewertung, datenlieferung);

			setzeEntities(datei, fehler, datenlieferung);
		} else {
			throw new DatenlieferungException("Die Datenlieferung mit Id " + datenlieferungId + " existiert nicht");
		}
		return fehler;
	}

	private int bestimmeFehler(Bewertung bewertung, Datenlieferung datenlieferung) {
		int fehler = 0;
		switch (bewertung) {
		case ANGENOMMEN:
			break;
		case UNBESTIMMT:
			fehler = protokoll.protokolliere(AktionsArt.RÜCKMELDUNG, datenlieferung, FehlerMeldung.EINGANG_UNBESTIMMT,
					datenlieferung.getDatenlieferungId());
			break;
		case KONFLIKT:
			fehler = protokoll.protokolliere(AktionsArt.RÜCKMELDUNG, datenlieferung, FehlerMeldung.EINGANG_KONFLIKT,
					datenlieferung.getDatenlieferungId());
			break;
		case ABGELEHNT:
			fehler = protokoll.protokolliere(AktionsArt.RÜCKMELDUNG, datenlieferung, FehlerMeldung.EINGANG_ABGELEHNT,
					datenlieferung.getDatenlieferungId());
			break;
		default:
			break;
		}
		return fehler;
	}

	private void setzeEntities(EingeleseneDatei datei, int fehler, Datenlieferung datenlieferung) {
		LocalDateTime now = LocalDateTime.now();
		datei.addDatenlieferung(datenlieferung);
		datenlieferung.addEingeleseneDatei(datei);
		datei.setFehler(fehler);
		if (fehler == 0) {
			datei.setBestätigt(now);
			datenlieferung.setBestätigt(now);
		}

		datenlieferung.setFehler(fehler);
		eingeleseneDateiRepo.saveAndFlush(datei);
		datenlieferungRepo.saveAndFlush(datenlieferung);
	}

	@Override
	public void accept(Object t) {
		EingeleseneDatei datei = eingeleseneDateiRepo.getOne((Long) ((Object[]) t)[0]);
		action(datei);
	}

}
