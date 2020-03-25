package interfaces;



import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;


public interface IRechnungsGruppierung {

    Long getRechnungsGruppierungId();

         
        // Kind: value

     	    IK getVersenderIK();
     	    void setVersenderIK(IK value); 
        // Kind: value

     	    IK getDatenPrüfungsIK();
     	    void setDatenPrüfungsIK(IK value); 
        // Kind: enumeration


     	        DatenArt getDatenArt();
     	        void setDatenArt(DatenArt value); 
        // Kind: value

     	    int getMaxRechnungsAnzahl();
     	    void setMaxRechnungsAnzahl(int value);
}

