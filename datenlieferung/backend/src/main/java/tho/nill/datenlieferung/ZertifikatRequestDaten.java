package tho.nill.datenlieferung;

import lombok.Data;
import tho.nill.datenlieferung.simpleAttributes.IK;

@Data
public class ZertifikatRequestDaten {
	private IK ik;
	private String name;

	public ZertifikatRequestDaten(IK ik, String name) {
		super();
		this.ik = ik;
		this.name = name;
	}
}
