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

import interfaces.IDatenaustausch;
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
public class Datenaustausch implements IDatenaustausch {

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

	@Override
	public IK getVersenderIK() {
		return versenderIK;
	}

	@Override
	public void setVersenderIK(IK value) {
		versenderIK = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "DATENANNAHMEIK")
	@Convert(converter = tho.nill.datenlieferung.simpleAttributes.IKAdapter.class)
	private IK datenAnnahmeIK;

	@Override
	public IK getDatenAnnahmeIK() {
		return datenAnnahmeIK;
	}

	@Override
	public void setDatenAnnahmeIK(IK value) {
		datenAnnahmeIK = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "DATENPRÜFUNGSIK")
	@Convert(converter = tho.nill.datenlieferung.simpleAttributes.IKAdapter.class)
	private IK datenPrüfungsIK;

	@Override
	public IK getDatenPrüfungsIK() {
		return datenPrüfungsIK;
	}

	@Override
	public void setDatenPrüfungsIK(IK value) {
		datenPrüfungsIK = value;
	}

	// Kind: (enumeration)

	@Enumerated
	@Column(name = "RICHTUNG")
	private Richtung richtung;

	@Override
	public Richtung getRichtung() {
		return richtung;
	}

	@Override
	public void setRichtung(Richtung value) {
		richtung = value;
	}

	// Kind: (enumeration)

	@Enumerated
	@Column(name = "DATENART")
	private DatenArt datenArt;

	@Override
	public DatenArt getDatenArt() {
		return datenArt;
	}

	@Override
	public void setDatenArt(DatenArt value) {
		datenArt = value;
	}

	// Kind: (enumeration)

	@Enumerated
	@Column(name = "VERBINDUNG")
	private Verbindungsart verbindung;

	@Override
	public Verbindungsart getVerbindung() {
		return verbindung;
	}

	@Override
	public void setVerbindung(Verbindungsart value) {
		verbindung = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "HOST")
	private String host;

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public void setHost(String value) {
		host = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "PORT")
	private int port;

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void setPort(int value) {
		port = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "HOSTVERZEICHNIS")
	private String hostVerzeichnis;

	@Override
	public String getHostVerzeichnis() {
		return hostVerzeichnis;
	}

	@Override
	public void setHostVerzeichnis(String value) {
		hostVerzeichnis = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "EMAILTO")
	private String emailTo;

	@Override
	public String getEmailTo() {
		return emailTo;
	}

	@Override
	public void setEmailTo(String value) {
		emailTo = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "EMAILFROM")
	private String emailFrom;

	@Override
	public String getEmailFrom() {
		return emailFrom;
	}

	@Override
	public void setEmailFrom(String value) {
		emailFrom = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "LOGINNUTZER")
	private String loginNutzer;

	@Override
	public String getLoginNutzer() {
		return loginNutzer;
	}

	@Override
	public void setLoginNutzer(String value) {
		loginNutzer = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "LOGINPASSWORT")
	private String loginPasswort;

	@Override
	public String getLoginPasswort() {
		return loginPasswort;
	}

	@Override
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

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String value) {
		name = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "STRASSE")
	private String strasse;

	@Override
	public String getStrasse() {
		return strasse;
	}

	@Override
	public void setStrasse(String value) {
		strasse = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "PLZ")
	private String plz;

	@Override
	public String getPlz() {
		return plz;
	}

	@Override
	public void setPlz(String value) {
		plz = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "ANNAHMECLASSNAME")
	private String annahmeClassName;

	@Override
	public String getAnnahmeClassName() {
		return annahmeClassName;
	}

	@Override
	public void setAnnahmeClassName(String value) {
		annahmeClassName = value;
	}

	@Basic
	@Column(name = "CODEPAGE")
	private String codepage;

	@Override
	public String getCodepage() {
		return codepage;
	}

	@Override
	public void setCodepage(String value) {
		codepage = value;
	}

}
