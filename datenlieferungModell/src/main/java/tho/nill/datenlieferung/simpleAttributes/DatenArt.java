package tho.nill.datenlieferung.simpleAttributes;

public enum DatenArt {
	PAR300RECP("APO","RECP","00","I1"),
	PAR300ABRP("APO","ABRP","00","I1"),
	PAR300DATEN("APO","ALLE","00","I1"),
	PAR300PDF("REC","SERA","BI","03"),
	PAR300VERWURF("ZVP","VERW","00","I1"),
	PAR300IMAGE("IMG","TA400","BI","03"),
	PAR302("SOL","","00","I1"),
	TA6_KKOR("APO","KKOR","00","I1"),
	TA6_LKOR("APO","LKOR","00","I1"),
	TA6_AKOR("APO","AKOR","00","I1"),
	CD_NUMMER("CD","","00","00");
	
	public String verfahrensKennung;
	public String verfahrensSpezifikation;
	public String zeichensatz;
	public String komprimierung;
	
	private DatenArt(String verfahrensKennung, String verfahrensSpezifikation, String zeichensatz,
			String komprimierung) {
		this.verfahrensKennung = verfahrensKennung;
		this.verfahrensSpezifikation = verfahrensSpezifikation;
		this.zeichensatz = zeichensatz;
		this.komprimierung = komprimierung;
	}
	

	

}
