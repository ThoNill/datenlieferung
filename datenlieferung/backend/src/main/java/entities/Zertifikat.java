package entities;

import java.math.BigInteger;
import java.time.Instant;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import tho.nill.datenlieferung.simpleAttributes.IK;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "ZERTIFIKAT")
@SequenceGenerator(name = "ZERTIFIKAT_SEQ", sequenceName = "ZERTIFIKAT_SEQ")
public class Zertifikat {

	@EqualsAndHashCode.Include
	@ToString.Include
	@Basic
	@Column(name = "ZERTIFIKATID")
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ZERTIFIKAT_SEQ")
	private java.lang.Long ZertifikatId;

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

	// Kind: (value)

	@Basic
	@Column(name = "SERIALID")
	private BigInteger serialId;

	public BigInteger getSerialId() {
		return serialId;
	}

	public void setSerialId(BigInteger value) {
		serialId = value;
	}

	// Kind: (CharBLOB)

	@Lob
	@Column(name = "PEMZERTIFIKAT", length = 5000)
	private String pemZertifikat;

	// Kind: (value)

	@Basic
	@Column(name = "VON")
	private Instant von;

	public Instant getVon() {
		return von;
	}

	public void setVon(Instant value) {
		von = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "BIS")
	private Instant bis;

	public Instant getBis() {
		return bis;
	}

	public void setBis(Instant value) {
		bis = value;
	}

}
