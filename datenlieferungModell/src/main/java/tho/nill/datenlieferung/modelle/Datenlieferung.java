package tho.nill.datenlieferung.modelle;

import java.time.LocalDateTime;

import org.nill.vorlagen.compiler.model.ObjectModell;
import org.nill.vorlagen.object.ddd.Aggregate;
import org.nill.vorlagen.object.ddd.Entity;

import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.MonatJahr;
import tho.nill.datenlieferung.verknüpfungen.Verknüpfungen;

public class Datenlieferung extends ObjectModell implements Aggregate, Entity{

	public int lieferJahr;
	public MonatJahr mj;
	public IK versenderIK;
	public IK datenAnnahmeIK;
	public IK datenPrüfungsIK;
	public DatenArt datenArt;
	public AktionsArt letzteAktion;

	public int dateinummer;
	public int transfernummer_datenannahme;
	public int transfernummer_vorprüfung;
	public int korrekturnummer;
	public int cdnummer;
	public int par300Verbindung;
	
	public int fehler;
	
	public long dateigröße_nutzdaten;
	public long dateigröße_übertragung;

	public String logDateiname;
	public String physDateiname;
	
	public String testKennzeichen; 
	
	public LocalDateTime erstellt;
	public LocalDateTime verschlüsselt;
	public LocalDateTime gesendet;
	public LocalDateTime bestätigt;
	
	public Datenlieferung() throws Exception {
		super();
		addConnection(new Verknüpfungen());
	}
	
	public String getKennung() {
		return datenArt.verfahrensKennung;
	}

	public String getSpezifikation() {
		return datenArt.verfahrensSpezifikation;
	}
	
	public String getZeichensatz() {
		return datenArt.zeichensatz;
	}
	
	public String getKomprimierung() {
		return datenArt.komprimierung;
	}
	
	
	

}
