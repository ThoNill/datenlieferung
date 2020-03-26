package tho.nill.datenlieferung.modelle;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;

import org.nill.vorlagen.compiler.markerClasses.CharBLOB;
import org.nill.vorlagen.compiler.model.ObjectModell;
import org.nill.vorlagen.object.ddd.Aggregate;
import org.nill.vorlagen.object.ddd.Entity;

import tho.nill.datenlieferung.simpleAttributes.AdressArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;
import tho.nill.datenlieferung.verknüpfungen.Verknüpfungen;

public class EingeleseneDatei extends ObjectModell implements Entity, Aggregate {
	
	public String host;
	public String hostverzeichnis;
	public String emailFrom;
	public CharBLOB daten;
	public LocalDateTime erstellt;
	public LocalDateTime bestätigt;
	public int fehler;
	public Verbindungsart art;
		
	
	public EingeleseneDatei() throws Exception {
		super();
		addConnection(new Verknüpfungen());
	}

}
