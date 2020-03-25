package entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import interfaces.IRechnungsGruppierung;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "RECHNUNGSGRUPPIERUNG")
@SequenceGenerator(name = "RECHNUNGSGRUPPIERUNG_SEQ", sequenceName = "RECHNUNGSGRUPPIERUNG_SEQ")
public class RechnungsGruppierung implements IRechnungsGruppierung {

    @EqualsAndHashCode.Include
    @ToString.Include
    @Basic
    @Column(name = "RECHNUNGSGRUPPIERUNGID")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECHNUNGSGRUPPIERUNG_SEQ")
    private java.lang.Long RechnungsGruppierungId;

         
        // Kind: (value)


     		@Basic
     	    @Column(name = "VERSENDERIK")
     	     @Convert(converter = tho.nill.datenlieferung.simpleAttributes.IKAdapter.class)
     	    private IK versenderIK;


     	    @Override
			public IK getVersenderIK() {
     	    	return versenderIK;
     	    }

     	    @Override
			public void setVersenderIK(IK value) {
     	    	versenderIK = value;
     	    }

         
        // Kind: (value)


     		@Basic
     	    @Column(name = "DATENPRÜFUNGSIK")
     	     @Convert(converter = tho.nill.datenlieferung.simpleAttributes.IKAdapter.class)
     	    private IK datenPrüfungsIK;


     	    @Override
			public IK getDatenPrüfungsIK() {
     	    	return datenPrüfungsIK;
     	    }

     	    @Override
			public void setDatenPrüfungsIK(IK value) {
     	    	datenPrüfungsIK = value;
     	    }

         
        // Kind: (enumeration)


     	    @Enumerated
     	    @Column(name = "DATENART")
     	    private DatenArt datenArt;


     	    @Override
			public DatenArt getDatenArt() {
     	    	return datenArt;
     	    }

     	    @Override
			public void setDatenArt(DatenArt value) {
     	    	datenArt = value;
     	    }
         
        // Kind: (value)


     		@Basic
     	    @Column(name = "MAXRECHNUNGSANZAHL")
     	    private int maxRechnungsAnzahl;


     	    @Override
			public int getMaxRechnungsAnzahl() {
     	    	return maxRechnungsAnzahl;
     	    }

     	    @Override
			public void setMaxRechnungsAnzahl(int value) {
     	    	maxRechnungsAnzahl = value;
     	    }

}

