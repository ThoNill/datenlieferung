package entities;

import javax.persistence.Basic;
import javax.persistence.Column;
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
import tho.nill.datenlieferung.simpleAttributes.Bewertung;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "EINGANGTEXT")
@SequenceGenerator(name = "EINGANGTEXT_SEQ", sequenceName = "EINGANGTEXT_SEQ")
public class EingangText {

	@EqualsAndHashCode.Include
	@ToString.Include
	@Basic
	@Column(name = "EINGANGTEXTID")
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EINGANGTEXT_SEQ")
	private java.lang.Long EingangTextId;

	// Kind: (value)

	@Basic
	@Column(name = "REGEXP")
	private String regexp;

	public String getRegexp() {
		return regexp;
	}

	public void setRegexp(String value) {
		regexp = value;
	}

	// Kind: (enumeration)

	@Enumerated
	@Column(name = "BEWERTUNG")
	private Bewertung bewertung;

	public Bewertung getBewertung() {
		return bewertung;
	}

	public void setBewertung(Bewertung value) {
		bewertung = value;
	}
}
