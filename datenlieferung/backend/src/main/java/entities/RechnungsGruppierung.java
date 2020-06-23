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
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "RECHNUNGSGRUPPIERUNG")
@SequenceGenerator(name = "RECHNUNGSGRUPPIERUNG_SEQ", sequenceName = "RECHNUNGSGRUPPIERUNG_SEQ")
public class RechnungsGruppierung {

	@EqualsAndHashCode.Include
	@ToString.Include
	@Basic
	@Column(name = "RECHNUNGSGRUPPIERUNGID")
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECHNUNGSGRUPPIERUNG_SEQ")
	private java.lang.Long RechnungsGruppierungId;

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

	// Kind: (value)

	@Basic
	@Column(name = "MAXRECHNUNGSANZAHL")
	private int maxRechnungsAnzahl;

	public int getMaxRechnungsAnzahl() {
		return maxRechnungsAnzahl;
	}

	public void setMaxRechnungsAnzahl(int value) {
		maxRechnungsAnzahl = value;
	}

}
