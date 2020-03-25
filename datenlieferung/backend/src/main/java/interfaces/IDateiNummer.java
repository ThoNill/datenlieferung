package interfaces;



import tho.nill.datenlieferung.simpleAttributes.DateiNummerArt;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;


public interface IDateiNummer {

    Long getDateiNummerId();

         
        // Kind: value

     	    IK getVersenderIK();
     	    void setVersenderIK(IK value); 
        // Kind: value

     	    int getJahr();
     	    void setJahr(int value); 
        // Kind: value

     	    IK getIk();
     	    void setIk(IK value); 
        // Kind: enumeration


     	        DatenArt getDatenArt();
     	        void setDatenArt(DatenArt value); 
        // Kind: enumeration


     	        DateiNummerArt getNummernArt();
     	        void setNummernArt(DateiNummerArt value); 
        // Kind: value

     	    int getAktuelleNummer();
     	    void setAktuelleNummer(int value);
}

