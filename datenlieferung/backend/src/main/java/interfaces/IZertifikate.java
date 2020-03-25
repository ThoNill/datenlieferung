package interfaces;



import java.math.BigInteger;

import tho.nill.datenlieferung.simpleAttributes.IK;


public interface IZertifikate {

    Long getZertifikateId();

         
        // Kind: value

     	    IK getIk();
     	    void setIk(IK value); 
        // Kind: value

     	    BigInteger getSerialId();
     	    void setSerialId(BigInteger value); 
        // Kind: CharBLOB

}

