package tho.nill.datenlieferung;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import entities.RechnungsGruppierung;
import repositories.RechnungsGruppierungRepository;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;

@Service
public class RechnungsGruppierungEntityService {

	@Autowired
	RechnungsGruppierungRepository rechnungsGruppierungRepo;

	public RechnungsGruppierungEntityService() {
		super();
	}

	public Optional<RechnungsGruppierung> get(long id) {
		return rechnungsGruppierungRepo.findById(id);
	}

	public RechnungsGruppierung create(IK versenderIK, IK datenPrüfungsIK, DatenArt datenArt, int maxRechnungsAnzahl) {
		RechnungsGruppierung d = new RechnungsGruppierung();
		felderSetzen(d, versenderIK, datenPrüfungsIK, datenArt, maxRechnungsAnzahl);
		return rechnungsGruppierungRepo.save(d);
	}

	public void update(long id, IK versenderIK, IK datenPrüfungsIK, DatenArt datenArt, int maxRechnungsAnzahl) {
		RechnungsGruppierung d = rechnungsGruppierungRepo.getOne(id);
		felderSetzen(d, versenderIK, datenPrüfungsIK,

				datenArt, maxRechnungsAnzahl);
		rechnungsGruppierungRepo.save(d);
	}

	public void delete(long id) {
		rechnungsGruppierungRepo.deleteById(id);

	}

	private void felderSetzen(RechnungsGruppierung d, IK versenderIK, IK datenPrüfungsIK, DatenArt datenArt,
			int maxRechnungsAnzahl) {
		d.setVersenderIK(versenderIK);
		d.setDatenPrüfungsIK(datenPrüfungsIK);
		d.setDatenArt(datenArt);
		d.setMaxRechnungsAnzahl(maxRechnungsAnzahl);
	}
}
