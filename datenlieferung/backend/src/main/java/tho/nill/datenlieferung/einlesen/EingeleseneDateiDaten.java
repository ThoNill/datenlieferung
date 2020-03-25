package tho.nill.datenlieferung.einlesen;

import lombok.Data;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

@Data
public class EingeleseneDateiDaten {
	private String host;
	private String hostverzeichnis;
	private String daten;
	private Verbindungsart art;
}
