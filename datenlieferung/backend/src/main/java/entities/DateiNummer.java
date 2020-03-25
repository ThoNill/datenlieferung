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

import interfaces.IDateiNummer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import tho.nill.datenlieferung.simpleAttributes.DateiNummerArt;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "DATEINUMMER")
@SequenceGenerator(name = "DATEINUMMER_SEQ", sequenceName = "DATEINUMMER_SEQ")
public class DateiNummer implements IDateiNummer {

    @EqualsAndHashCode.Include
    @ToString.Include
    @Basic
    @Column(name = "DATEINUMMERID")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DATEINUMMER_SEQ")
    private java.lang.Long DateiNummerId;

         
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
     	    @Column(name = "JAHR")
     	    private int jahr;


     	    @Override
			public int getJahr() {
     	    	return jahr;
     	    }

     	    @Override
			public void setJahr(int value) {
     	    	jahr = value;
     	    }

         
        // Kind: (value)


     		@Basic
     	    @Column(name = "IK")
     	     @Convert(converter = tho.nill.datenlieferung.simpleAttributes.IKAdapter.class)
     	    private IK ik;


     	    @Override
			public IK getIk() {
     	    	return ik;
     	    }

     	    @Override
			public void setIk(IK value) {
     	    	ik = value;
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
         
        // Kind: (enumeration)


     	    @Enumerated
     	    @Column(name = "NUMMERNART")
     	    private DateiNummerArt nummernArt;


     	    @Override
			public DateiNummerArt getNummernArt() {
     	    	return nummernArt;
     	    }

     	    @Override
			public void setNummernArt(DateiNummerArt value) {
     	    	nummernArt = value;
     	    }
         
        // Kind: (value)


     		@Basic
     	    @Column(name = "AKTUELLENUMMER")
     	    private int aktuelleNummer;


     	    @Override
			public int getAktuelleNummer() {
     	    	return aktuelleNummer;
     	    }

     	    @Override
			public void setAktuelleNummer(int value) {
     	    	aktuelleNummer = value;
     	    }

}

