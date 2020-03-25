package interfaces;



import java.time.Instant;

import tho.nill.datenlieferung.simpleAttributes.IK;


public interface IVersenderKey {

    Long getPrivateKeyId();

         
        // Kind: value

     	    IK getVersenderIK();
     	    void setVersenderIK(IK value); 
        // Kind: ByteBLOB
         
        // Kind: ByteBLOB
         
        // Kind: value

     	    Instant getVon();
     	    void setVon(Instant value); 
        // Kind: value

     	    Instant getBis();
     	    void setBis(Instant value); 
        // Kind: value

     	    boolean getAktiv();
     	    void setAktiv(boolean value); 
        // Kind: ByteBLOB
         
        // Kind: ByteBLOB

}

