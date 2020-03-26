package tho.nill.datenlieferung.modelle;

import java.time.LocalDateTime;

import org.nill.vorlagen.compiler.model.ObjectModell;
import org.nill.vorlagen.object.ddd.Entity;

import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.verknüpfungen.Verknüpfungen;

public class DatenlieferungProtokoll extends ObjectModell implements Entity {
	
	public LocalDateTime am;
	public AktionsArt    aktion;	
	public FehlerMeldung fehler;
	public String meldung;
	public String exeption_class;
	public String exeption_meldung;
	
	public DatenlieferungProtokoll() throws Exception {
		super();
		addConnection(new Verknüpfungen());
	}

}
