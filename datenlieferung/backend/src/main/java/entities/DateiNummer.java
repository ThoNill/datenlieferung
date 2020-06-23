package entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import tho.nill.datenlieferung.simpleAttributes.DateiNummerArt;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "DATEINUMMER")
@SequenceGenerator(name = "DATEINUMMER_SEQ", sequenceName = "DATEINUMMER_SEQ")
public class DateiNummer {

	@EqualsAndHashCode.Include
	@ToString.Include
	@Basic
	@Column(name = "DATEINUMMERID")
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DATEINUMMER_SEQ")
	private java.lang.Long DateiNummerId;

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
	@Column(name = "JAHR")
	private int jahr;

	public int getJahr() {
		return jahr;
	}

	public void setJahr(int value) {
		jahr = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "IK")
	@Convert(converter = tho.nill.datenlieferung.simpleAttributes.IKAdapter.class)
	private IK ik;

	public IK getIk() {
		return ik;
	}

	public void setIk(IK value) {
		ik = value;
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
	@Column(name = "NUMMERNART")
	private DateiNummerArt nummernArt;

	public DateiNummerArt getNummernArt() {
		return nummernArt;
	}

	public void setNummernArt(DateiNummerArt value) {
		nummernArt = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "AKTUELLENUMMER")
	private int aktuelleNummer;

	public int getAktuelleNummer() {
		return aktuelleNummer;
	}

	public void setAktuelleNummer(int value) {
		aktuelleNummer = value;
	}

}
