package tho.nill.datenlieferung.simpleAttributes;

public enum FehlerMeldung {
	SUCCESS("Success"),
	FILE_NOT_EXISTS("Die Datei %s existiert nicht"),
	FILE_NOT_WRITABLE("Die Datei %s ist nicht beschreibbar"),
	FILE_CD_BELEGE("Für das Medium %d konnten keine Belege erstellt werden"),
	FILE_CD_BRENNER("Für das Medium %d konnten kein Brennerfile erstellt werden"),
	FILE_CD_BEGLEITZETTEL("Für die Datenlieferung %d konnten kein Begleitbeleg erstellt werden"),
	FILE_CD_ANSCHREIBEN("Für das Medium %d konnten kein Anschreiben erstellt werden"),
	FILE_EMAIL("Die Datenlieferung %d konnte nicht per EMail gesendet werden"),
	FILE_SFTP("Die Datenlieferung %d konnte nicht per Sftp gesendet werden"),
	FILE_VERSCHLÜSSELUNG("Die Datenlieferung %d konnte nicht verschlüsselt werden"),
	FILE_AUFTRAGSDATEI("Die Auftragsdatei %s konnte nicht erzeugt werden"),
	ERROR_NUMMERIERUNG("Die Datenlieferung %d konnte nicht mit nummern versorgt werden"),
	EINGANG_ABGELEHNT("Die Datenlieferung %d wurde abgelehnt"),
	EINGANG_UNBESTIMMT("Unbestimmte Bewertung der Datenlieferung %d wurde abgelehnt"),
	EINGANG_KONFLIKT("Bewertung der Datenlieferung %d enthält einen Konflikt"),
	EINGANG_KEINE_DATENLIEFERUNG("Es gibt zur Eingangsdatei %d keine Datenlieferung"),
	EINGANG_DATENLIEFERUNG_NICHT_EINDEUTIG("Es gibt zur Eingangsdatei %d keine eindeutige Datenlieferung"),
	EINGANG_KEINE_DATEIINFO("Es gibt zur Eingangsdatei %d keine Infos zu Dateien"),
	EINGANG_KEINE_ABFRAGE("Es gibt zur Eingangsdatei %d keine Abfrage"),
	EINGANG_AUSNAHME("Ausnahme bei der Verarbeitung von Eingangsdatei %d "),
	
	EINGANG_EMAIL("Ausnahme bei der Verarbeitung eingehender EMails"),
	EINGANG_SFTP("Ausnahme bei der Verarbeitung eingehender Sftp Dateien")
	;
	
	public String meldung;

	private FehlerMeldung(String meldung) {
		this.meldung = meldung;
	}
	
	
}
