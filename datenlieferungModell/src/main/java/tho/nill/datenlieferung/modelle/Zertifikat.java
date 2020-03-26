package tho.nill.datenlieferung.modelle;

import java.math.BigInteger;
import java.time.Instant;

import org.nill.vorlagen.compiler.markerClasses.CharBLOB;
import org.nill.vorlagen.compiler.model.ObjectModell;
import org.nill.vorlagen.object.ddd.Entity;

import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.verknüpfungen.Verknüpfungen;

public class Zertifikat extends ObjectModell implements Entity {
	
	public IK ik;
	public BigInteger serialId;
	public CharBLOB pemZertifikat;
	public Instant von;
	public Instant bis;
	
	
	
	
	public Zertifikat() throws Exception {
		super();
		addConnection(new Verknüpfungen());
	}

}
