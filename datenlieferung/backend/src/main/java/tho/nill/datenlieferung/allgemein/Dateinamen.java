package tho.nill.datenlieferung.allgemein;

import entities.Datenlieferung;

public class Dateinamen {

	private static final String ARZKOR = "ARZKOR";

	private Dateinamen() {
		super();
	}

	public static String berechnePhysDateinamen(Datenlieferung d) {
		String vk = d.getDatenArt().verfahrensKennung;
		return String.format("%s%s0%03d", d.getTestKennzeichen(), vk, d.getTransfernummer_datenannahme());
	}

	public static String berechneLogDateinamen(Datenlieferung d) {
		switch (d.getDatenArt()) {
		case CD_NUMMER:
			return "";
		case PAR300ABRP:
			return "ARZ" + korrektur(d) + lieferjahr(d) + dateinummer300(d);
		case PAR300DATEN:
			return "ARZ" + korrektur(d) + lieferjahr(d) + dateinummer300(d);
		case PAR300IMAGE:
			return String.format("%sIMG0%03d", d.getTestKennzeichen(), d.getDateinummer());
		case PAR300PDF:
			return String.format("%sREC0%03d", d.getTestKennzeichen(), d.getDateinummer());
		case PAR300RECP:
			return "ARZ" + korrektur(d) + lieferjahr(d) + dateinummer300(d);
		case PAR300VERWURF:
			return String.format("%sZVP0%03d", d.getTestKennzeichen(), d.getDateinummer());
		case PAR302:
			return dateiname302(d);
		case TA6_AKOR:
			return ARZKOR + lieferjahr(d) + dateinummer300(d);
		case TA6_KKOR:
			return ARZKOR + lieferjahr(d) + dateinummer300(d);
		case TA6_LKOR:
			return ARZKOR + lieferjahr(d) + dateinummer300(d);
		default:
			return "NN";

		}
	}

	private static String dateiname302(Datenlieferung d) {
		int m = d.getMj().getMonat();
		return "SL" + d.getVersenderIK().toString().substring(3, 8) + "A" + ((m < 10) ? "0" : "") + m;
	}

	private static String korrektur(Datenlieferung d) {
		int korrNr = d.getKorrekturnummer();
		return (korrNr == 0) ? "ABR" : "KO" + korrNr;
	}

	private static String lieferjahr(Datenlieferung d) {
		return "" + (d.getLieferJahr() % 100);
	}

	private static String dateinummer300(Datenlieferung d) {
		int dateinr = d.getDateinummer();
		if (dateinr < 10) {
			return "00" + dateinr;
		}
		if (dateinr < 100) {
			return "0" + dateinr;
		}

		int m = (dateinr % 100);
		int index = (dateinr - m) / 100;
		int i = (index < 10) ? '0' + index : 'A' + (index - 10);
		char c = (char) (i & 0xFF);
		return "" + c + ((m < 10) ? "0" : "") + m;
	}
}