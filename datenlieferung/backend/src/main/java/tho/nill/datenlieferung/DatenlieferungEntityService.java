package tho.nill.datenlieferung;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import repositories.DatenlieferungRepository;

@Service
public class DatenlieferungEntityService {

	@Autowired
	DatenlieferungRepository datenlieferungRepo;

	public DatenlieferungEntityService() {
		super();
	}

	public Optional<Object> get(long id) {
		return datenlieferungRepo.getListeneintrag(id);
	}

}
