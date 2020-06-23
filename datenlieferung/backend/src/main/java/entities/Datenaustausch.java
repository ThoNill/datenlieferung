package entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "DATENAUSTAUSCH")
@SequenceGenerator(name = "DATENAUSTAUSCH_SEQ", sequenceName = "DATENAUSTAUSCH_SEQ")
public class Datenaustausch {

	@EqualsAndHashCode.Include
	@ToString.Include
	@Basic
	@Column(name = "DATENAUSTAUSCHID")
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DATENAUSTAUSCH_SEQ")
	private java.lang.Long DatenaustauschId;

	// Kind: (value)

	@Basic
	@Column(name = "VERSENDERIK")
	@Convert(converter = tho.nill.datenlieferung.simpleAttributes.IKAdapter.class)
	private IK versenderIK;

	public IK getVersenderIK() {
		return versenderIK;
	}

	public void setVersenderIK(IK value) {
		versenderIK = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "DATENANNAHMEIK")
	@Convert(converter = tho.nill.datenlieferung.simpleAttributes.IKAdapter.class)
	private IK datenAnnahmeIK;

	public IK getDatenAnnahmeIK() {
		return datenAnnahmeIK;
	}

	public void setDatenAnnahmeIK(IK value) {
		datenAnnahmeIK = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "DATENPRÜFUNGSIK")
	@Convert(converter = tho.nill.datenlieferung.simpleAttributes.IKAdapter.class)
	private IK datenPrüfungsIK;

	public IK getDatenPrüfungsIK() {
		return datenPrüfungsIK;
	}

	public void setDatenPrüfungsIK(IK value) {
		datenPrüfungsIK = value;
	}

	// Kind: (enumeration)

	@Enumerated
	@Column(name = "RICHTUNG")
	private Richtung richtung;

	public Richtung getRichtung() {
		return richtung;
	}

	public void setRichtung(Richtung value) {
		richtung = value;
	}

	// Kind: (enumeration)

	@Enumerated
	@Column(name = "DATENART")
	private DatenArt datenArt;

	public DatenArt getDatenArt() {
		return datenArt;
	}

	public void setDatenArt(DatenArt value) {
		datenArt = value;
	}

	// Kind: (enumeration)

	@Enumerated
	@Column(name = "VERBINDUNG")
	private Verbindungsart verbindung;

	public Verbindungsart getVerbindung() {
		return verbindung;
	}

	public void setVerbindung(Verbindungsart value) {
		verbindung = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "HOST")
	private String host;

	public String getHost() {
		return host;
	}

	public void setHost(String value) {
		host = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "PORT")
	private int port;

	public int getPort() {
		return port;
	}

	public void setPort(int value) {
		port = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "HOSTVERZEICHNIS")
	private String hostVerzeichnis;

	public String getHostVerzeichnis() {
		return hostVerzeichnis;
	}

	public void setHostVerzeichnis(String value) {
		hostVerzeichnis = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "EMAILTO")
	private String emailTo;

	public String getEmailTo() {
		return emailTo;
	}

	public void setEmailTo(String value) {
		emailTo = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "EMAILFROM")
	private String emailFrom;

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(String value) {
		emailFrom = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "LOGINNUTZER")
	private String loginNutzer;

	public String getLoginNutzer() {
		return loginNutzer;
	}

	public void setLoginNutzer(String value) {
		loginNutzer = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "LOGINPASSWORT")
	private String loginPasswort;

	public String getLoginPasswort() {
		return loginPasswort;
	}

	public void setLoginPasswort(String value) {
		loginPasswort = value;
	}

	// Kind: (ByteBLOB)

	@Lob
	@Column(name = "LOGINCERT")
	private byte[] loginCert;

	// Kind: (value)

	@Basic
	@Column(name = "NAME")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String value) {
		name = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "STRASSE")
	private String strasse;

	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String value) {
		strasse = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "PLZ")
	private String plz;

	public String getPlz() {
		return plz;
	}

	public void setPlz(String value) {
		plz = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "ANNAHMECLASSNAME")
	private String annahmeClassName;

	public String getAnnahmeClassName() {
		return annahmeClassName;
	}

	public void setAnnahmeClassName(String value) {
		annahmeClassName = value;
	}

	@Basic
	@Column(name = "CODEPAGE")
	private String codepage;

	public String getCodepage() {
		return codepage;
	}

	public void setCodepage(String value) {
		codepage = value;
	}

}
