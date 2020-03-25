package tho.nill.datenlieferung;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import entities.Datenaustausch;
import entities.EingangText;
import entities.RechnungsGruppierung;
import repositories.DatenaustauschRepository;
import repositories.DatenlieferungRepository;
import repositories.EingangTextRepository;
import repositories.RechnungAuftragRepository;
import repositories.RechnungsGruppierungRepository;

@Service
public class RepositoryService {

	@Autowired
	private EingangTextRepository eingangTextRepository;

	@Autowired
	private RechnungAuftragRepository rechnungsAuftragRepo;

	@Autowired
	private DatenlieferungRepository datenlieferungRepo;

	@Autowired
	private RechnungsGruppierungRepository rechnungsGruppierungRepo;

	@Autowired
	private DatenaustauschRepository datenaustauschRepo;

	public List<Object> getDatenlieferungListe() {
		return datenlieferungRepo.getListe();
	}

	public List<Object> getRechnungAuftragListe() {
		return rechnungsAuftragRepo.getListe();
	}

	public List<Datenaustausch> getDatenAustauschListe() {
		return datenaustauschRepo.findAll();
	}

	public List<RechnungsGruppierung> getRechnungsGruppierungListe() {
		return rechnungsGruppierungRepo.findAll();
	}

	public List<EingangText> getEingangTextListe() {
		return eingangTextRepository.findAll();
	}

}
