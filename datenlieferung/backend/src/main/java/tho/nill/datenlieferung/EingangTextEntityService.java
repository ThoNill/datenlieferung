package tho.nill.datenlieferung;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import entities.EingangText;
import repositories.EingangTextRepository;
import tho.nill.datenlieferung.simpleAttributes.Bewertung;

@Service
public class EingangTextEntityService {

	@Autowired
	EingangTextRepository eingangTextRepo;

	public EingangTextEntityService() {
		super();
	}

	public Optional<EingangText> get(long id) {
		return eingangTextRepo.findById(id);
	}

	public EingangText create(String regexp, Bewertung bewertung) {
		EingangText d = new EingangText();
		felderSetzen(d, regexp, bewertung);
		return eingangTextRepo.save(d);
	}

	public void update(long id, String regexp, Bewertung bewertung) {
		EingangText d = eingangTextRepo.getOne(id);
		felderSetzen(d, regexp, bewertung);
		eingangTextRepo.save(d);
	}

	public void delete(long id) {
		eingangTextRepo.deleteById(id);

	}

	private void felderSetzen(EingangText d, String regexp, Bewertung bewertung) {
		d.setRegexp(regexp);
		d.setBewertung(bewertung);
	}
}
