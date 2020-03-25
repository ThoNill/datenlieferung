package entities;

import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import interfaces.IDatenlieferung;
import interfaces.IDatenlieferungProtokoll;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "DATENLIEFERUNGPROTOKOLL")
@SequenceGenerator(name = "DATENLIEFERUNGPROTOKOLL_SEQ", sequenceName = "DATENLIEFERUNGPROTOKOLL_SEQ")
public class DatenlieferungProtokoll implements IDatenlieferungProtokoll {

	@EqualsAndHashCode.Include
	@ToString.Include
	@Basic
	@Column(name = "DATENLIEFERUNGPROTOKOLLID")
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DATENLIEFERUNGPROTOKOLL_SEQ")
	private java.lang.Long DatenlieferungProtokollId;

	// Kind: (LocalDateTime)

	@Column(name = "AM", columnDefinition = "TIMESTAMP")
	private LocalDateTime am;

	@Override
	public LocalDateTime getAm() {
		return am;
	}

	@Override
	public void setAm(LocalDateTime value) {
		am = value;
	}

	// Kind: (enumeration)

	@Enumerated
	@Column(name = "AKTION")
	private AktionsArt aktion;

	@Override
	public AktionsArt getAktion() {
		return aktion;
	}

	@Override
	public void setAktion(AktionsArt value) {
		aktion = value;
	}

	// Kind: (enumeration)

	@Enumerated
	@Column(name = "FEHLER")
	private FehlerMeldung fehler;

	@Override
	public FehlerMeldung getFehler() {
		return fehler;
	}

	@Override
	public void setFehler(FehlerMeldung value) {
		fehler = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "MELDUNG")
	private String meldung;

	@Override
	public String getMeldung() {
		return meldung;
	}

	@Override
	public void setMeldung(String value) {
		meldung = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "EXEPTION_CLASS")
	private String exeption_class;

	@Override
	public String getExeption_class() {
		return exeption_class;
	}

	@Override
	public void setExeption_class(String value) {
		exeption_class = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "EXEPTION_MELDUNG")
	private String exeption_meldung;

	@Override
	public String getExeption_meldung() {
		return exeption_meldung;
	}

	@Override
	public void setExeption_meldung(String value) {
		exeption_meldung = value;
	}

	// Kind: (toone2many)
	@JsonManagedReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Datenlieferung_Id")
	private Datenlieferung Datenlieferung;

	@Override
	public void setDatenlieferung(IDatenlieferung value) {
		Datenlieferung = (Datenlieferung) value;
	}

}
