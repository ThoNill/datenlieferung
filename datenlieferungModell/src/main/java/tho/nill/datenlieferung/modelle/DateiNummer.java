package tho.nill.datenlieferung.modelle;

import org.nill.vorlagen.compiler.model.ObjectModell;
import org.nill.vorlagen.object.ddd.Entity;

import tho.nill.datenlieferung.simpleAttributes.DateiNummerArt;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.MonatJahr;
import tho.nill.datenlieferung.verknüpfungen.Verknüpfungen;

public class DateiNummer extends ObjectModell implements Entity {
	
	public IK versenderIK;
	public int  jahr;
	public IK ik;
	public DatenArt datenArt;
	public DateiNummerArt nummernArt;
	public int aktuelleNummer;
	
	
	public DateiNummer() throws Exception {
		super();
		addConnection(new Verknüpfungen());
	}

}
