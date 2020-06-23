package entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.MonatJahr;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "DATENLIEFERUNG")
@SequenceGenerator(name = "DATENLIEFERUNG_SEQ", sequenceName = "DATENLIEFERUNG_SEQ")
public class Datenlieferung {

	@EqualsAndHashCode.Include
	@ToString.Include
	@Basic
	@Column(name = "DATENLIEFERUNGID")
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DATENLIEFERUNG_SEQ")
	private java.lang.Long DatenlieferungId;

	// Kind: (value)

	@Basic
	@Column(name = "LIEFERJAHR")
	private int lieferJahr;

	public int getLieferJahr() {
		return lieferJahr;
	}

	public void setLieferJahr(int value) {
		lieferJahr = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "MJ")
	@Convert(converter = tho.nill.datenlieferung.simpleAttributes.MonatJahrAdapter.class)
	private MonatJahr mj;

	public MonatJahr getMj() {
		return mj;
	}

	public void setMj(MonatJahr value) {
		mj = value;
	}

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
	@Column(name = "LETZTEAKTION")
	private AktionsArt letzteAktion;

	public AktionsArt getLetzteAktion() {
		return letzteAktion;
	}

	public void setLetzteAktion(AktionsArt value) {
		letzteAktion = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "DATEINUMMER")
	private int dateinummer;

	public int getDateinummer() {
		return dateinummer;
	}

	public void setDateinummer(int value) {
		dateinummer = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "TRANSFERNUMMER_DATENANNAHME")
	private int transfernummer_datenannahme;

	public int getTransfernummer_datenannahme() {
		return transfernummer_datenannahme;
	}

	public void setTransfernummer_datenannahme(int value) {
		transfernummer_datenannahme = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "TRANSFERNUMMER_VORPRÜFUNG")
	private int transfernummer_vorprüfung;

	public int getTransfernummer_vorprüfung() {
		return transfernummer_vorprüfung;
	}

	public void setTransfernummer_vorprüfung(int value) {
		transfernummer_vorprüfung = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "KORREKTURNUMMER")
	private int korrekturnummer;

	public int getKorrekturnummer() {
		return korrekturnummer;
	}

	public void setKorrekturnummer(int value) {
		korrekturnummer = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "CDNUMMER")
	private int cdnummer;

	public int getCdnummer() {
		return cdnummer;
	}

	public void setCdnummer(int value) {
		cdnummer = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "PAR300VERBINDUNG")
	private int par300Verbindung;

	public int getPar300Verbindung() {
		return par300Verbindung;
	}

	public void setPar300Verbindung(int value) {
		par300Verbindung = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "FEHLER")
	private int fehler;

	public int getFehler() {
		return fehler;
	}

	public void setFehler(int value) {
		fehler = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "DATEIGRÖSSE_NUTZDATEN")
	private long dateigröße_nutzdaten;

	public long getDateigröße_nutzdaten() {
		return dateigröße_nutzdaten;
	}

	public void setDateigröße_nutzdaten(long value) {
		dateigröße_nutzdaten = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "DATEIGRÖSSE_ÜBERTRAGUNG")
	private long dateigröße_übertragung;

	public long getDateigröße_übertragung() {
		return dateigröße_übertragung;
	}

	public void setDateigröße_übertragung(long value) {
		dateigröße_übertragung = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "LOGDATEINAME")
	private String logDateiname;

	public String getLogDateiname() {
		return logDateiname;
	}

	public void setLogDateiname(String value) {
		logDateiname = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "PHYSDATEINAME")
	private String physDateiname;

	public String getPhysDateiname() {
		return physDateiname;
	}

	public void setPhysDateiname(String value) {
		physDateiname = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "TESTKENNZEICHEN")
	private String testKennzeichen;

	public String getTestKennzeichen() {
		return testKennzeichen;
	}

	public void setTestKennzeichen(String value) {
		testKennzeichen = value;
	}

	// Kind: (LocalDateTime)

	@Column(name = "ERSTELLT", columnDefinition = "TIMESTAMP")
	private LocalDateTime erstellt;

	public LocalDateTime getErstellt() {
		return erstellt;
	}

	public void setErstellt(LocalDateTime value) {
		erstellt = value.plusNanos(-value.getNano());
	}

	// Kind: (LocalDateTime)

	@Column(name = "VERSCHLÜSSELT", columnDefinition = "TIMESTAMP")
	private LocalDateTime verschlüsselt;

	public LocalDateTime getVerschlüsselt() {
		return verschlüsselt;
	}

	public void setVerschlüsselt(LocalDateTime value) {
		verschlüsselt = value;
	}

	// Kind: (LocalDateTime)

	@Column(name = "GESENDET", columnDefinition = "TIMESTAMP")
	private LocalDateTime gesendet;

	public LocalDateTime getGesendet() {
		return gesendet;
	}

	public void setGesendet(LocalDateTime value) {
		gesendet = value;
	}

	// Kind: (LocalDateTime)

	@Column(name = "BESTÄTIGT", columnDefinition = "TIMESTAMP")
	private LocalDateTime bestätigt;

	public LocalDateTime getBestätigt() {
		return bestätigt;
	}

	public void setBestätigt(LocalDateTime value) {
		bestätigt = value;
	}

	// Kind: (fromone2many)

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "Datenlieferung", orphanRemoval = true)
	private Set<RechnungAuftrag> RechnungAuftrag = new HashSet<>();

	public void addRechnungAuftrag(RechnungAuftrag x) {
		this.RechnungAuftrag.add(x);
		x.setDatenlieferung(this);
	}

	public void removeRechnungAuftrag(RechnungAuftrag x) {
		this.RechnungAuftrag.remove(x);
		x.setDatenlieferung(null);
	}

	// Kind: (fromone2many)

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "Datenlieferung", orphanRemoval = true)
	private Set<DatenlieferungProtokoll> DatenlieferungProtokoll = new HashSet<>();

	public void addDatenlieferungProtokoll(DatenlieferungProtokoll x) {
		this.DatenlieferungProtokoll.add(x);
		x.setDatenlieferung(this);
	}

	public void removeDatenlieferungProtokoll(DatenlieferungProtokoll x) {
		this.DatenlieferungProtokoll.remove(x);
		x.setDatenlieferung(null);
	}

	// Kind: (frommany2many)

	@ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinTable(name = "Datenlieferung_EingeleseneDatei", joinColumns = {
			@JoinColumn(name = "DatenlieferungId") }, inverseJoinColumns = { @JoinColumn(name = "EingeleseneDateiId") })
	private Set<EingeleseneDatei> EingeleseneDatei = new HashSet<>();

	public void addEingeleseneDatei(EingeleseneDatei x) {
		this.EingeleseneDatei.add(x);
		x.addDatenlieferung(this);
	}

	public void removeEingeleseneDatei(EingeleseneDatei x) {
		this.EingeleseneDatei.remove(x);
		x.removeDatenlieferung(this);
	}

	public String getKennung() {
		return datenArt.verfahrensKennung;
	}

	public String getSpezifikation() {
		return datenArt.verfahrensSpezifikation;
	}

	public String getZeichensatz() {
		return datenArt.zeichensatz;
	}

	public String getKomprimierung() {
		return datenArt.komprimierung;
	}

}
