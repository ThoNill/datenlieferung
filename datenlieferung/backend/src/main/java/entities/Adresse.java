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
import tho.nill.datenlieferung.simpleAttributes.AdressArt;
import tho.nill.datenlieferung.simpleAttributes.IK;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "ADRESSE")
@SequenceGenerator(name = "ADRESSE_SEQ", sequenceName = "ADRESSE_SEQ")
public class Adresse {

	@EqualsAndHashCode.Include
	@ToString.Include
	@Basic
	@Column(name = "ADRESSEID")
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADRESSE_SEQ")
	private java.lang.Long AdresseId;

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
	@Column(name = "ART")
	private AdressArt art;

	public AdressArt getArt() {
		return art;
	}

	public void setArt(AdressArt value) {
		art = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "ANSPRECHPARTNER")
	private String ansprechpartner;

	public String getAnsprechpartner() {
		return ansprechpartner;
	}

	public void setAnsprechpartner(String value) {
		ansprechpartner = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "FIRMA")
	private String firma;

	public String getFirma() {
		return firma;
	}

	public void setFirma(String value) {
		firma = value;
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
	@Column(name = "ORT")
	private String ort;

	public String getOrt() {
		return ort;
	}

	public void setOrt(String value) {
		ort = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "STRASSE")
	private String straße;

	public String getStraße() {
		return straße;
	}

	public void setStraße(String value) {
		straße = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "TELEFON")
	private String telefon;

	public String getTelefon() {
		return telefon;
	}

	public void setTelefon(String value) {
		telefon = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "EMAIL")
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String value) {
		email = value;
	}

}
