package tho.nill.datenlieferung.auftragsdatei;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import entities.Datenlieferung;
import lombok.NonNull;
import tho.nill.datenlieferung.allgemein.Dateien;
import tho.nill.datenlieferung.exceptions.DatenlieferungException;

public enum AuftragsFelder {
	IDENTIFIKATOR(1, 6, FeldTyp.NUM, true, "500000"),
	VERSION(7, 2, FeldTyp.NUM, true, "01"),
	LÄNGE_AUTRAG(9, 8, FeldTyp.NUM, true, "00000348"),
	SEQUENZ_NR(17, 3, FeldTyp.NUM, true),
	VERFAHREN_KENNUNG(20, 5, FeldTyp.ALPHA, true, Datenlieferung::getKennung),
	TRANSFER_NUMMER(25, 3, FeldTyp.NUM, true, Datenlieferung::getTransfernummer_datenannahme),
	VERFAHREN_KENNUNG_SPEZIFIKATION(28, 5, FeldTyp.ALPHA, true, Datenlieferung::getSpezifikation),
	ABSENDER_EIGNER(33, 15, FeldTyp.ALPHA, true, Datenlieferung::getVersenderIK),
	ABSENDER_PHYSIKALISCH(48, 15, FeldTyp.ALPHA, true, Datenlieferung::getVersenderIK),
	EMPFÄNGER_NUTZER(63, 15, FeldTyp.ALPHA, true, Datenlieferung::getDatenPrüfungsIK),
	EMPFÄNGER_PHYSIKALISCH(78, 15, FeldTyp.ALPHA, true, Datenlieferung::getDatenAnnahmeIK),
	FEHLER_NUMMER(93, 6, FeldTyp.NUM, true),
	FEHLER_MAßNAHME(99, 6, FeldTyp.NUM, true),
	DATEINAME(105, 11, FeldTyp.ALPHA, true, Datenlieferung::getLogDateiname),
	DATUM_ERSTELLUNG(116, 14, FeldTyp.DATUM, true, Datenlieferung::getErstellt),
	DATUM_ÜBERTRAGUNG_GESENDET(130, 14, FeldTyp.DATUM, false),
	DATUM_ÜBERTRAGUNG_EPFANGEN_START(144, 14, FeldTyp.DATUM, false),
	DATUM_ÜBERTRAGUNG_EPFANGEN_ENDE(158, 14, FeldTyp.DATUM, false),
	DATEIVERSION(172, 6, FeldTyp.NUM, true),
	KORREKTUR(178, 1, FeldTyp.NUM, true),
	DATEIGRÖßE_NUTZDATEN(179, 12, FeldTyp.NUM, true, Datenlieferung::getDateigröße_nutzdaten),
	DATEIGRÖßE_ÜBERTRAGUNG(191, 12, FeldTyp.NUM, true, Datenlieferung::getDateigröße_übertragung),
	ZEICHENSATZ(203, 2, FeldTyp.ALPHA, true, "I1"),
	KOMPRIMIERUNG(205, 2, FeldTyp.NUM, true, Datenlieferung::getKomprimierung),
	VERSCHLÜSSELUNGSART(207, 2, FeldTyp.NUM, true, "03"),
	ELEKTRONISCHE_UNTERSCHRIFT(209, 2, FeldTyp.NUM, true, "03"),
	SATZFORMAT(211, 3, FeldTyp.ALPHA, true, "   "),
	SATZLÄNGE(214, 5, FeldTyp.NUM, true),
	BLOCKLÄNGE(219, 8, FeldTyp.NUM, true),
	STATUS(227, 1, FeldTyp.ALPHA, true),
	WIEDERHOLUNG(228, 2, FeldTyp.NUM, true, "00"),
	ÜBERTRAGUNGSWEG(230, 1, FeldTyp.NUM, true),
	VERZÖGERTER_VERSAND(231, 10, FeldTyp.NUM, true),
	INFO_UND_FEHLERFELDER(241, 6, FeldTyp.NUM, true),
	VARIABLES_INFOFELD(247, 28, FeldTyp.ALPHA, true),
	EMAIL_ADRESSE_ABSENDER(275, 44, FeldTyp.ALPHA, true, "t.nill@t-online.de"),
	DATEI_BEZEICHNUNG(319, 30, FeldTyp.ALPHA, true);

	private int start;
	private int länge;
	private FeldTyp typ;
	private boolean muss;
	private String konstant;
	private Function<Datenlieferung, Object> funct;

	private AuftragsFelder(int start, int länge, FeldTyp typ, boolean muss, String konstant) {
		this.start = start;
		this.länge = länge;
		this.typ = typ;
		this.muss = muss;
		this.konstant = konstant;
	}

	private AuftragsFelder(int start, int länge, FeldTyp typ, boolean muss, Function<Datenlieferung, Object> funct) {
		this.start = start;
		this.länge = länge;
		this.typ = typ;
		this.muss = muss;
		this.funct = funct;
	}

	private AuftragsFelder(int start, int länge, FeldTyp typ, boolean muss) {
		this.start = start;
		this.länge = länge;
		this.typ = typ;
		this.muss = muss;
		this.funct = null;

		StringBuilder b = new StringBuilder();
		if (FeldTyp.ALPHA.equals(typ)) {
			paddWith(b, ' ', länge);
		} else {
			paddWith(b, '0', länge);
		}
		this.konstant = b.toString();
	}

	public static void auftragsdatei(@NonNull Datenlieferung d, @NonNull File datei) throws IOException {
		try (FileOutputStream out = Dateien.createOutputStream(datei)) {
			out.write(getWerte(d));
		}
	}

	private Object getWert(Datenlieferung d) {
		if (funct != null) {
			Object o = funct.apply(d);
			if (o == null) {
				throw new DatenlieferungException("der Wert von " + this.name() + " ist null");
			}
			return o;
		}
		if (konstant != null) {
			return konstant;
		}
		throw new DatenlieferungException("konstant oder funct muss belegt sein in " + this.name());
	}

	private void setzeWert(byte[] data, Object wert) {

		byte[] bWert = format(wert).getBytes();
		if (bWert.length != länge) {
			throw new DatenlieferungException("Wert " + wert + " passt in  der Länge nicht zu " + this.name());
		}
		for (int i = 0; i < länge; i++) {
			data[start - 1 + i] = bWert[i];
		}
	}

	private String format(Object wert) {
		if (wert == null) {
			throw new DatenlieferungException("Wert ist null für " + this.name());
		}
		if (FeldTyp.DATUM.equals(typ) && wert instanceof LocalDateTime) {
			return JJJJMMTTHHMMSS(wert);
		}
		StringBuilder b = new StringBuilder();
		int anzahlPadding = länge - wert.toString().length();
		if (FeldTyp.NUM.equals(typ)) {
			paddWith(b, '0', anzahlPadding);
		}
		b.append(wert.toString());
		if (FeldTyp.ALPHA.equals(typ)) {
			paddWith(b, ' ', anzahlPadding);
		}
		return b.toString();
	}

	private String JJJJMMTTHHMMSS(Object datum) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime d = (LocalDateTime) datum;
		return d.format(formatter);

	}

	private void paddWith(StringBuilder b, char c, int anzahlPadding) {
		for (int i = 0; i < anzahlPadding; i++) {
			b.append(c);
		}
	}

	// public wegen Test
	public static byte[] getWerte(Datenlieferung d) {
		byte[] werte = new byte[348];
		for (AuftragsFelder f : AuftragsFelder.values()) {
			Object w = f.getWert(d);
			f.setzeWert(werte, w);
		}
		return werte;
	}

}
