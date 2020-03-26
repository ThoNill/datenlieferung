package tho.nill.datenlieferung.einlesen;

import java.util.ArrayList;
import java.util.List;

import entities.EingeleseneDatei;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import repositories.DatenlieferungRepository;
import tho.nill.datenlieferung.Protokollführer;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;

@Slf4j
public class DefaultTextAnalyse implements TextAnalyse {
	private DatenlieferungRepository datenlieferungRepo;
	private SuchInfoExtractor extractor;

	private Protokollführer protokoll;

	public DefaultTextAnalyse(DatenlieferungRepository datenlieferungRepo, SuchInfoExtractor extractor,
			Protokollführer protokoll) {
		super();
		this.datenlieferungRepo = datenlieferungRepo;
		this.extractor = extractor;
		this.protokoll = protokoll;
	}

	@Override
	public List<Long> analysieren(@NonNull EingeleseneDatei datei) {
		List<Long> alleIds = new ArrayList<>();
		List<SuchInfo> infos = extractor.extractSuchInfos(datei.getDaten().toLowerCase());
		for (SuchInfo info : infos) {
			alleIds.add(bestimmeDatenlieferungIdZurInfo(datei, info));
		}
		return alleIds;
	}

	private Long bestimmeDatenlieferungIdZurInfo(EingeleseneDatei datei, SuchInfo info) {
		log.debug("Host: " + datei.getHost());
		log.debug("From: " + datei.getEmailFrom());
		log.debug("Dateiname: " + info.getDateiname().toUpperCase());
		log.debug("Größe: " + info.getGröße());
		log.debug("Erstellt: " + info.getErstellt());
		List<Long> ids = null;
		switch (datei.getArt()) {
		case EMAIL:
			ids = datenlieferungRepo.getNichtBestätigteDatenlieferungenEMail(datei.getEmailFrom(),

					info.getDateiname().toUpperCase(), info.getGröße(), info.getErstellt());
			break;
		case SFTP:
			ids = datenlieferungRepo.getNichtBestätigteDatenlieferungenSftp(datei.getHost(),

					info.getDateiname().toUpperCase(), info.getGröße(), info.getErstellt());
			break;
		default:
			break;
		}
		int fehler = 0;
		if (ids == null) {
			fehler = protokoll.protokolliere(AktionsArt.RÜCKMELDUNG, null, FehlerMeldung.EINGANG_KEINE_ABFRAGE,
					datei.getEingeleseneDateiId());
		} else {
			if (ids.isEmpty()) {
				log.debug("Liste ist leer");
				fehler = protokoll.protokolliere(AktionsArt.RÜCKMELDUNG, null,
						FehlerMeldung.EINGANG_KEINE_DATENLIEFERUNG, datei.getEingeleseneDateiId());
			}
			if (ids.size() > 1) {
				log.debug("Zuviel in der Liste");
				fehler = protokoll.protokolliere(AktionsArt.RÜCKMELDUNG, null,
						FehlerMeldung.EINGANG_DATENLIEFERUNG_NICHT_EINDEUTIG, datei.getEingeleseneDateiId());
			}
			if (fehler == 0) {
				datei.setFehler(0);
				return ids.get(0);
			}
		}
		datei.setFehler(fehler);
		return 0L;
	}

}
