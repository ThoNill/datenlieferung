package entities;

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
@Table(name = "PRIVATEKEY")
@SequenceGenerator(name = "PRIVATEKEY_SEQ", sequenceName = "PRIVATEKEY_SEQ")
public class VersenderKey {

	@EqualsAndHashCode.Include
	@ToString.Include
	@Basic
	@Column(name = "PRIVATEKEYID")
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRIVATEKEY_SEQ")
	private java.lang.Long PrivateKeyId;

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

	// Kind: (ByteBLOB)

	@Lob
	@Column(name = "PRIVKEY", length = 5000)
	private byte[] privKey;

	// Kind: (ByteBLOB)

	@Lob
	@Column(name = "PUBKEY", length = 5000)
	private byte[] pubKey;

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

	// Kind: (value)

	@Basic
	@Column(name = "AKTIV")
	private boolean aktiv;

	public boolean getAktiv() {
		return aktiv;
	}

	public void setAktiv(boolean value) {
		aktiv = value;
	}

	// Kind: (ByteBLOB)

	@Lob
	@Column(name = "CERTIFICATEREQUEST", length = 5000)
	private byte[] certificateRequest;

	// Kind: (ByteBLOB)

	@Lob
	@Column(name = "CERTIFICATERESPONSE", length = 5000)
	private byte[] certificateResponse;
}
