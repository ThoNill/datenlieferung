package interfaces;

import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

public interface IDatenaustausch {

	Long getDatenaustauschId();

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

	Richtung getRichtung();

	void setRichtung(Richtung value);
	// Kind: enumeration

	DatenArt getDatenArt();

	void setDatenArt(DatenArt value);
	// Kind: enumeration

	Verbindungsart getVerbindung();

	void setVerbindung(Verbindungsart value);
	// Kind: value

	String getHost();

	void setHost(String value);
	// Kind: value

	int getPort();

	void setPort(int value);
	// Kind: value

	String getHostVerzeichnis();

	void setHostVerzeichnis(String value);
	// Kind: value

	String getEmailTo();

	void setEmailTo(String value);
	// Kind: value

	String getEmailFrom();

	void setEmailFrom(String value);
	// Kind: value

	String getLoginNutzer();

	void setLoginNutzer(String value);
	// Kind: value

	String getLoginPasswort();

	void setLoginPasswort(String value);
	// Kind: ByteBLOB

	// Kind: value

	String getName();

	void setName(String value);
	// Kind: value

	String getStrasse();

	void setStrasse(String value);
	// Kind: value

	String getPlz();

	void setPlz(String value);
	// Kind: value

	String getAnnahmeClassName();

	void setAnnahmeClassName(String value);

	public String getCodepage();

	public void setCodepage(String value);
}
