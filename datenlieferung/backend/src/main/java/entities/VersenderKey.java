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

import interfaces.IVersenderKey;
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
public class VersenderKey implements IVersenderKey {

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

	@Override
	public IK getVersenderIK() {
		return versenderIK;
	}

	@Override
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

	@Override
	public Instant getVon() {
		return von;
	}

	@Override
	public void setVon(Instant value) {
		von = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "BIS")
	private Instant bis;

	@Override
	public Instant getBis() {
		return bis;
	}

	@Override
	public void setBis(Instant value) {
		bis = value;
	}

	// Kind: (value)

	@Basic
	@Column(name = "AKTIV")
	private boolean aktiv;

	@Override
	public boolean getAktiv() {
		return aktiv;
	}

	@Override
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
