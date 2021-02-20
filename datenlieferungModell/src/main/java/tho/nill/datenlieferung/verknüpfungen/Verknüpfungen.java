package tho.nill.datenlieferung.verknüpfungen;

import org.nill.vorlagen.compiler.model.ObjectModell;
import org.nill.vorlagen.compiler.util.Connection;
import tho.nill.datenlieferung.modelle.Datenlieferung;
import tho.nill.datenlieferung.modelle.DatenlieferungProtokoll;
import tho.nill.datenlieferung.modelle.EingeleseneDatei;
import tho.nill.datenlieferung.modelle.RechnungAuftrag;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;



public class Verknüpfungen extends ObjectModell {

	public Connection _auftrag = new Connection(ONE2MANY,Datenlieferung.class,RechnungAuftrag.class);
	public Connection _protokoll = new Connection(ONE2MANY,Datenlieferung.class,DatenlieferungProtokoll.class);
	public Connection _datei = new Connection(MANY2MANY,Datenlieferung.class,EingeleseneDatei.class);

	public Verknüpfungen() {
		super();
	}
	
}
