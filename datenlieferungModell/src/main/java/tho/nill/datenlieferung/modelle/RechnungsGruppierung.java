package tho.nill.datenlieferung.modelle;

import org.nill.vorlagen.compiler.model.ObjectModell;
import org.nill.vorlagen.object.ddd.Aggregate;
import org.nill.vorlagen.object.ddd.Entity;

import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.MonatJahr;
import tho.nill.datenlieferung.verknüpfungen.Verknüpfungen;

public class RechnungsGruppierung extends ObjectModell implements Aggregate, Entity {
	
	public IK versenderIK;
	public IK datenPrüfungsIK;
	public DatenArt datenArt;
	public int maxRechnungsAnzahl;
	
	public RechnungsGruppierung() throws Exception {
		super();
		addConnection(new Verknüpfungen());
	}

}
