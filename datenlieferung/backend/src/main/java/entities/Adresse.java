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

import interfaces.IAdresse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import tho.nill.datenlieferung.simpleAttributes.AdressArt;
import tho.nill.datenlieferung.simpleAttributes.IK;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "ADRESSE")
@SequenceGenerator(name = "ADRESSE_SEQ", sequenceName = "ADRESSE_SEQ")
public class Adresse implements IAdresse {

    @EqualsAndHashCode.Include
    @ToString.Include
    @Basic
    @Column(name = "ADRESSEID")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADRESSE_SEQ")
    private java.lang.Long AdresseId;

         
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
     	    @Column(name = "ART")
     	    private AdressArt art;


     	    @Override
			public AdressArt getArt() {
     	    	return art;
     	    }

     	    @Override
			public void setArt(AdressArt value) {
     	    	art = value;
     	    }
         
        // Kind: (value)


     		@Basic
     	    @Column(name = "ANSPRECHPARTNER")
     	    private String ansprechpartner;


     	    @Override
			public String getAnsprechpartner() {
     	    	return ansprechpartner;
     	    }

     	    @Override
			public void setAnsprechpartner(String value) {
     	    	ansprechpartner = value;
     	    }

         
        // Kind: (value)


     		@Basic
     	    @Column(name = "FIRMA")
     	    private String firma;


     	    @Override
			public String getFirma() {
     	    	return firma;
     	    }

     	    @Override
			public void setFirma(String value) {
     	    	firma = value;
     	    }

         
        // Kind: (value)


     		@Basic
     	    @Column(name = "PLZ")
     	    private String plz;


     	    @Override
			public String getPlz() {
     	    	return plz;
     	    }

     	    @Override
			public void setPlz(String value) {
     	    	plz = value;
     	    }

         
        // Kind: (value)


     		@Basic
     	    @Column(name = "ORT")
     	    private String ort;


     	    @Override
			public String getOrt() {
     	    	return ort;
     	    }

     	    @Override
			public void setOrt(String value) {
     	    	ort = value;
     	    }

         
        // Kind: (value)


     		@Basic
     	    @Column(name = "STRASSE")
     	    private String straße;


     	    @Override
			public String getStraße() {
     	    	return straße;
     	    }

     	    @Override
			public void setStraße(String value) {
     	    	straße = value;
     	    }

         
        // Kind: (value)


     		@Basic
     	    @Column(name = "TELEFON")
     	    private String telefon;


     	    @Override
			public String getTelefon() {
     	    	return telefon;
     	    }

     	    @Override
			public void setTelefon(String value) {
     	    	telefon = value;
     	    }

         
        // Kind: (value)


     		@Basic
     	    @Column(name = "EMAIL")
     	    private String email;


     	    @Override
			public String getEmail() {
     	    	return email;
     	    }

     	    @Override
			public void setEmail(String value) {
     	    	email = value;
     	    }

}

