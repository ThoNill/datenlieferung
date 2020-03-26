package tho.nill.datenlieferung.modelle;

import java.math.BigInteger;
import java.time.Instant;

import org.nill.vorlagen.compiler.markerClasses.ByteBLOB;
import org.nill.vorlagen.compiler.markerClasses.CharBLOB;
import org.nill.vorlagen.compiler.model.ObjectModell;
import org.nill.vorlagen.object.ddd.Aggregate;
import org.nill.vorlagen.object.ddd.Entity;

import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.verknüpfungen.Verknüpfungen;

public class VersenderKey extends ObjectModell implements Entity, Aggregate {
	
	public IK versenderIK;
	public ByteBLOB privKey;
	public ByteBLOB pubKey;
	public Instant von;
	public Instant bis;
	public boolean aktiv;
	public ByteBLOB certificateRequest;
	public ByteBLOB certificateResponse;
	
	
	public VersenderKey() throws Exception {
		super();
		addConnection(new Verknüpfungen());
	}

}
