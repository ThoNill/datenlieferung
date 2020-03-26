package tho.nill.datenlieferung.modelle;

import java.time.LocalDateTime;

import org.nill.vorlagen.compiler.markerClasses.ByteBLOB;
import org.nill.vorlagen.compiler.model.ObjectModell;
import org.nill.vorlagen.object.ddd.Aggregate;
import org.nill.vorlagen.object.ddd.Entity;

import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.MonatJahr;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;
import tho.nill.datenlieferung.verknüpfungen.Verknüpfungen;

public class Datenaustausch extends ObjectModell implements Aggregate, Entity{

	public IK versenderIK;
	public IK datenAnnahmeIK;
	public IK datenPrüfungsIK;
	
	public Richtung richtung;
	public DatenArt datenArt;
	
	public Verbindungsart verbindung;
	
	String host;
	int port;
	String hostVerzeichnis;
	
	String emailTo;
	String emailFrom;
	String loginNutzer;
	String loginPasswort;
	ByteBLOB loginCert;
	String name;
	String strasse;
	String plz;
	
	String annahmeClassName;
	String codepage;
	
	public String calcX() {
		return annahmeClassName;
	}
}
