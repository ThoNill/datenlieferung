package entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "EINGELESENEDATEI")
@SequenceGenerator(name = "EINGELESENEDATEI_SEQ", sequenceName = "EINGELESENEDATEI_SEQ")
public class EingeleseneDatei {

	@EqualsAndHashCode.Include
	@ToString.Include
	@Basic
	@Column(name = "EINGELESENEDATEIID")
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EINGELESENEDATEI_SEQ")
	private java.lang.Long EingeleseneDateiId;

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
	@Column(name = "HOSTVERZEICHNIS")
	private String hostverzeichnis;

	public String getHostverzeichnis() {
		return hostverzeichnis;
	}

	public void setHostverzeichnis(String value) {
		hostverzeichnis = value;
	}

	// Kind: (CharBLOB)
	@Basic
	@Column(name = "EMAILFROM")
	private String emailFrom;

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(String value) {
		emailFrom = value;
	}

	@Lob
	@Column(name = "DATEN")
	private String daten;

	// Kind: (LocalDateTime)

	@Column(name = "ERSTELLT", columnDefinition = "TIMESTAMP")
	private LocalDateTime erstellt;

	public LocalDateTime getErstellt() {
		return erstellt;
	}

	public void setErstellt(LocalDateTime value) {
		erstellt = value;
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

	// Kind: (enumeration)

	@Enumerated
	@Column(name = "ART")
	private Verbindungsart art;

	public Verbindungsart getArt() {
		return art;
	}

	public void setArt(Verbindungsart value) {
		art = value;
	}

	// Kind: (tomany2many)

	@ManyToMany(mappedBy = "EingeleseneDatei")
	private Set<Datenlieferung> Datenlieferung = new HashSet<>();

	public void addDatenlieferung(Datenlieferung x) {
		this.Datenlieferung.add(x);
	}

	public void removeDatenlieferung(Datenlieferung x) {
		this.Datenlieferung.remove(x);
	}

}
