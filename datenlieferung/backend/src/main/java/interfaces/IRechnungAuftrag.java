package interfaces;



import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.MonatJahr;


public interface IRechnungAuftrag {

    Long getRechnungAuftragId();

         
        // Kind: value

     	    MonatJahr getMj();
     	    void setMj(MonatJahr value); 
        // Kind: value

     	    int getRechnungsNummer();
     	    void setRechnungsNummer(int value); 
        // Kind: value

     	    IK getVersenderIK();
     	    void setVersenderIK(IK value); 
        // Kind: value

     	    IK getDatenAnnahmeIK();
     	    void setDatenAnnahmeIK(IK value); 
        // Kind: value

     	    IK getDatenPrüfungsIK();
     	    void setDatenPrüfungsIK(IK value); 
        // Kind: enumeration


     	        DatenArt getDatenArt();
     	        void setDatenArt(DatenArt value); 
        // Kind: toone2many

     	      void setDatenlieferung(IDatenlieferung value);
}

