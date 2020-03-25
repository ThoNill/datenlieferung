package interfaces;

import java.time.LocalDateTime;

import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

public interface IEingeleseneDatei {

	Long getEingeleseneDateiId();

	// Kind: value

	String getHost();

	void setHost(String value);
	// Kind: value

	String getHostverzeichnis();

	void setHostverzeichnis(String value);
	// Kind: CharBLOB

	String getEmailFrom();

	void setEmailFrom(String value);
	// Kind: LocalDateTime

	LocalDateTime getErstellt();

	void setErstellt(LocalDateTime value);
	// Kind: LocalDateTime

	LocalDateTime getBestätigt();

	void setBestätigt(LocalDateTime value);
	// Kind: value

	int getFehler();

	void setFehler(int value);
	// Kind: enumeration

	Verbindungsart getArt();

	void setArt(Verbindungsart value);
	// Kind: tomany2many

	void addDatenlieferung(IDatenlieferung x);

	void removeDatenlieferung(IDatenlieferung x);

}
