package tho.nill.datenlieferung.modelle;

import java.math.BigInteger;
import java.time.Instant;

import org.nill.vorlagen.compiler.markerClasses.CharBLOB;
import org.nill.vorlagen.compiler.model.ObjectModell;
import org.nill.vorlagen.object.ddd.Aggregate;
import org.nill.vorlagen.object.ddd.Entity;

import tho.nill.datenlieferung.simpleAttributes.AdressArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.verknüpfungen.Verknüpfungen;

public class Adresse extends ObjectModell implements Entity, Aggregate {
	
	public IK ik;
	public AdressArt art;
	public String ansprechpartner;
	public String firma;
	public String plz;
	public String ort;
	public String straße;
	public String telefon;
	public String email;
		
	
	public Adresse() throws Exception {
		super();
		addConnection(new Verknüpfungen());
	}

}
