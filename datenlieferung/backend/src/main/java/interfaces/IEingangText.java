package interfaces;



import tho.nill.datenlieferung.simpleAttributes.Bewertung;


public interface IEingangText {

    Long getEingangTextId();

         
        // Kind: value

     	    String getRegexp();
     	    void setRegexp(String value); 
        // Kind: enumeration


     	        Bewertung getBewertung();
     	        void setBewertung(Bewertung value);
}

