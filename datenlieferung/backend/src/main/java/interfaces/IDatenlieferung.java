package interfaces;

import java.time.LocalDateTime;

import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.MonatJahr;

public interface IDatenlieferung {

	Long getDatenlieferungId();

	// Kind: value

	int getLieferJahr();

	void setLieferJahr(int value);
	// Kind: value

	MonatJahr getMj();

	void setMj(MonatJahr value);
	// Kind: value

	IK getVersenderIK();

	void setVersenderIK(IK value);
	// Kind: value

	IK getDatenAnnahmeIK();

	void setDatenAnnahmeIK(IK value);
	// Kind: value

	IK getDatenPrüfungsIK();

	void setDatenPrüfungsIK(IK value);
	// Kind: enumeration

	DatenArt getDatenArt();

	void setDatenArt(DatenArt value);
	// Kind: enumeration

	AktionsArt getLetzteAktion();

	void setLetzteAktion(AktionsArt value);
	// Kind: value

	int getDateinummer();

	void setDateinummer(int value);
	// Kind: value

	int getTransfernummer_datenannahme();

	void setTransfernummer_datenannahme(int value);
	// Kind: value

	int getTransfernummer_vorprüfung();

	void setTransfernummer_vorprüfung(int value);
	// Kind: value

	int getKorrekturnummer();

	void setKorrekturnummer(int value);
	// Kind: value

	int getCdnummer();

	void setCdnummer(int value);
	// Kind: value

	int getPar300Verbindung();

	void setPar300Verbindung(int value);
	// Kind: value

	int getFehler();

	void setFehler(int value);
	// Kind: value

	long getDateigröße_nutzdaten();

	void setDateigröße_nutzdaten(long value);
	// Kind: value

	long getDateigröße_übertragung();

	void setDateigröße_übertragung(long value);
	// Kind: value

	String getLogDateiname();

	void setLogDateiname(String value);
	// Kind: value

	String getPhysDateiname();

	void setPhysDateiname(String value);
	// Kind: value

	String getTestKennzeichen();

	void setTestKennzeichen(String value);
	// Kind: LocalDateTime

	LocalDateTime getErstellt();

	void setErstellt(LocalDateTime value);
	// Kind: LocalDateTime

	LocalDateTime getVerschlüsselt();

	void setVerschlüsselt(LocalDateTime value);
	// Kind: LocalDateTime

	LocalDateTime getGesendet();

	void setGesendet(LocalDateTime value);
	// Kind: LocalDateTime

	LocalDateTime getBestätigt();

	void setBestätigt(LocalDateTime value);
	// Kind: fromone2many

	void addRechnungAuftrag(IRechnungAuftrag x);

	void removeRechnungAuftrag(IRechnungAuftrag x);

	// Kind: fromone2many

	void addDatenlieferungProtokoll(IDatenlieferungProtokoll x);

	void removeDatenlieferungProtokoll(IDatenlieferungProtokoll x);

	// Kind: frommany2many

	void addEingeleseneDatei(IEingeleseneDatei x);

	void removeEingeleseneDatei(IEingeleseneDatei x);

}
