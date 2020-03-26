package tho.nill.datenlieferung.modelle;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;

import org.nill.vorlagen.compiler.markerClasses.CharBLOB;
import org.nill.vorlagen.compiler.model.ObjectModell;
import org.nill.vorlagen.object.ddd.Aggregate;
import org.nill.vorlagen.object.ddd.Entity;

import tho.nill.datenlieferung.simpleAttributes.AdressArt;
import tho.nill.datenlieferung.simpleAttributes.Bewertung;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;
import tho.nill.datenlieferung.verknüpfungen.Verknüpfungen;

public class EingangText extends ObjectModell implements Entity, Aggregate {
	
	
	public String regexp;
	public Bewertung bewertung;
		
	
	public EingangText() throws Exception {
		super();
		addConnection(new Verknüpfungen());
	}

}
