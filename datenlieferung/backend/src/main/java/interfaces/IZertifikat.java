package interfaces;



import java.math.BigInteger;
import java.time.Instant;

import tho.nill.datenlieferung.simpleAttributes.IK;


public interface IZertifikat {

    Long getZertifikatId();

         
        // Kind: value

     	    IK getIk();
     	    void setIk(IK value); 
        // Kind: value

     	    BigInteger getSerialId();
     	    void setSerialId(BigInteger value); 
        // Kind: CharBLOB
         
        // Kind: value

     	    Instant getVon();
     	    void setVon(Instant value); 
        // Kind: value

     	    Instant getBis();
     	    void setBis(Instant value);
}

