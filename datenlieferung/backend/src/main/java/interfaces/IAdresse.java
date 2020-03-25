package interfaces;



import tho.nill.datenlieferung.simpleAttributes.AdressArt;
import tho.nill.datenlieferung.simpleAttributes.IK;


public interface IAdresse {

    Long getAdresseId();

         
        // Kind: value

     	    IK getIk();
     	    void setIk(IK value); 
        // Kind: enumeration


     	        AdressArt getArt();
     	        void setArt(AdressArt value); 
        // Kind: value

     	    String getAnsprechpartner();
     	    void setAnsprechpartner(String value); 
        // Kind: value

     	    String getFirma();
     	    void setFirma(String value); 
        // Kind: value

     	    String getPlz();
     	    void setPlz(String value); 
        // Kind: value

     	    String getOrt();
     	    void setOrt(String value); 
        // Kind: value

     	    String getStraße();
     	    void setStraße(String value); 
        // Kind: value

     	    String getTelefon();
     	    void setTelefon(String value); 
        // Kind: value

     	    String getEmail();
     	    void setEmail(String value);
}

