package entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
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
import interfaces.IRechnungAuftrag;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.MonatJahr;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "RECHNUNGAUFTRAG")
@SequenceGenerator(name = "RECHNUNGAUFTRAG_SEQ", sequenceName = "RECHNUNGAUFTRAG_SEQ")
public class RechnungAuftrag implements IRechnungAuftrag {

	@EqualsAndHashCode.Include
	@ToString.Include
	@Basic
	@Column(name = "RECHNUNGAUFTRAGID")
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECHNUNGAUFTRAG_SEQ")
	private java.lang.Long RechnungAuftragId;

	// Kind: (value)

	@Basic
	@Column(name = "MJ")
	@Convert(converter = tho.nill.datenlieferung.simpleAttributes.MonatJahrAdapter.class)
	private MonatJahr mj;

	@Override
	public MonatJahr getMj() {
		return mj;
	}

	@Override
	public void setMj(MonatJahr value) {
		mj = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "RECHNUNGSNUMMER")
	private int rechnungsNummer;

	@Override
	public int getRechnungsNummer() {
		return rechnungsNummer;
	}

	@Override
	public void setRechnungsNummer(int value) {
		rechnungsNummer = value;
	}

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

	// Kind: (toone2many)
	@JsonManagedReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Datenlieferung_Id")
	private Datenlieferung Datenlieferung;

	@Override
	public void setDatenlieferung(IDatenlieferung value) {
		Datenlieferung = (Datenlieferung) value;
	}

	public Datenlieferung getDatenlieferung() {
		return this.Datenlieferung;
	}

}