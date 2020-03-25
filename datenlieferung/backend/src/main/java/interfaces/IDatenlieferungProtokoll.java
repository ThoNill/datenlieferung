package interfaces;



import java.time.LocalDateTime;

import tho.nill.datenlieferung.simpleAttributes.AktionsArt;
import tho.nill.datenlieferung.simpleAttributes.FehlerMeldung;


public interface IDatenlieferungProtokoll {

    Long getDatenlieferungProtokollId();

         
        // Kind: LocalDateTime

     		     LocalDateTime getAm();
     		     void setAm(LocalDateTime value); 
        // Kind: enumeration


     	        AktionsArt getAktion();
     	        void setAktion(AktionsArt value); 
        // Kind: enumeration


     	        FehlerMeldung getFehler();
     	        void setFehler(FehlerMeldung value); 
        // Kind: value

     	    String getMeldung();
     	    void setMeldung(String value); 
        // Kind: value

     	    String getExeption_class();
     	    void setExeption_class(String value); 
        // Kind: value

     	    String getExeption_meldung();
     	    void setExeption_meldung(String value); 
        // Kind: toone2many

     	      void setDatenlieferung(IDatenlieferung value);
}

