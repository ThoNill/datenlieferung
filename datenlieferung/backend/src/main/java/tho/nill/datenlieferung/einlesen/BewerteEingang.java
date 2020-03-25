package tho.nill.datenlieferung.einlesen;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entities.EingangText;
import repositories.EingangTextRepository;
import tho.nill.datenlieferung.simpleAttributes.Bewertung;

public class BewerteEingang {
	private EingangTextRepository eingangTextRepo;

	public BewerteEingang(EingangTextRepository eingangTextRepo) {
		super();
		this.eingangTextRepo = eingangTextRepo;
	}

	private List<EingangText> texte;

	public void init() {
		texte = new ArrayList<>(eingangTextRepo.findAll());
	}

	public Bewertung bewerten(String text) {
		Bewertung erg = Bewertung.UNBESTIMMT;
		for (EingangText textPattern : texte) {
			Pattern pattern = Pattern.compile(textPattern.getRegexp(), Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				Bewertung neu = textPattern.getBewertung();
				if ((erg == Bewertung.ABGELEHNT && neu == Bewertung.ANGENOMMEN)
						|| (erg == Bewertung.ANGENOMMEN && neu == Bewertung.ABGELEHNT)) {
					return Bewertung.KONFLIKT;
				}
				if (neu == Bewertung.ABGELEHNT || neu == Bewertung.ANGENOMMEN) {
					erg = neu;
				}
			}
		}
		return erg;
	}

}
