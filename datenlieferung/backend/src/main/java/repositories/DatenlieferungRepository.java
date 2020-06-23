package repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import entities.Datenlieferung;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.MonatJahr;

@Repository
public interface DatenlieferungRepository extends JpaRepository<Datenlieferung, Long> {

	@Query("select d from entities.Datenlieferung d where d.cdnummer = :cdnummer  ")
	public List<Datenlieferung> getDatenlieferungenZurCD(@Param("cdnummer") int cdnummer);

	@Query("select max(d.korrekturnummer)+1 from entities.Datenlieferung d where d.mj = :mj and d.lieferJahr = :lieferJahr and d.versenderIK = :versenderIK and d.datenPrüfungsIK = :datenPrüfungsIK  and d.datenArt = :datenArt and d.dateinummer = :dateinummer")
	public int getKorrekturnummer(@Param("lieferJahr") int lieferJahr, @Param("mj") MonatJahr mj,
			@Param("versenderIK") IK versenderIK, @Param("datenPrüfungsIK") IK datenPrüfungsIK,
			@Param("datenArt") DatenArt datenArt, @Param("dateinummer") int dateinummer);

	@Query("select d from entities.Datenlieferung d where d.par300Verbindung = :par300Verbindung  ")
	public List<Datenlieferung> getDatenlieferungenZurVerbindung(@Param("par300Verbindung") int par300Verbindung);

	@Query("select new map(d.DatenlieferungId as datenlieferungId ,d.lieferJahr as lieferJahr,d.mj as mj,	d.versenderIK as versenderIK,	d.datenAnnahmeIK as datenAnnahmeIK,	d.datenPrüfungsIK as datenPrüfungsIK,	d.datenArt as datenArt,	d.letzteAktion as letzteAktion,	d.dateinummer as dateinummer,	d.transfernummer_datenannahme as transfernummer_datenannahme ,	d.transfernummer_vorprüfung as transfernummer_vorprüfung,	d.korrekturnummer as korrekturnummer,	d.cdnummer as cdnummer,	d.par300Verbindung as par300Verbindung,	d.fehler as fehler,	d.dateigröße_nutzdaten as dateigröße_nutzdaten,	d.dateigröße_übertragung as dateigröße_übertragung,	d.logDateiname as logDateiname,	d.physDateiname as physDateiname,	d.testKennzeichen as testKennzeichen,	d.erstellt as erstellt,	d.verschlüsselt as verschlüsselt,d.gesendet as gesendet,	d.bestätigt as bestätigt) from entities.Datenlieferung d")
	public List<Object> getListe();

	@Query("select new map(d.DatenlieferungId as datenlieferungId ,d.lieferJahr as lieferJahr,d.mj as mj,	d.versenderIK as versenderIK,	d.datenAnnahmeIK as datenAnnahmeIK,	d.datenPrüfungsIK as datenPrüfungsIK,	d.datenArt as datenArt,	d.letzteAktion as letzteAktion,	d.dateinummer as dateinummer,	d.transfernummer_datenannahme as transfernummer_datenannahme ,	d.transfernummer_vorprüfung as transfernummer_vorprüfung,	d.korrekturnummer as korrekturnummer,	d.cdnummer as cdnummer,	d.par300Verbindung as par300Verbindung,	d.fehler as fehler,	d.dateigröße_nutzdaten as dateigröße_nutzdaten,	d.dateigröße_übertragung as dateigröße_übertragung,	d.logDateiname as logDateiname,	d.physDateiname as physDateiname,	d.testKennzeichen as testKennzeichen,	d.erstellt as erstellt,	d.verschlüsselt as verschlüsselt,d.gesendet as gesendet,	d.bestätigt as bestätigt) from entities.Datenlieferung d where d.DatenlieferungId = :id")
	public Optional<Object> getListeneintrag(@Param("id") long id);

	@Query("select d.DatenlieferungId from entities.Datenlieferung d, entities.Datenaustausch a where a.richtung = tho.nill.datenlieferung.simpleAttributes.Richtung.EINGANG and d.bestätigt is null and d.fehler=0 and d.datenAnnahmeIK = a.datenAnnahmeIK and d.versenderIK = a.versenderIK and d.datenArt = a.datenArt and a.emailFrom = :mailfrom and d.physDateiname = :dateiname and d.dateigröße_übertragung =  :dateigröße  and d.erstellt = :erstellt and a.verbindung = tho.nill.datenlieferung.simpleAttributes.Verbindungsart.EMAIL")
	public List<Long> getNichtBestätigteDatenlieferungenEMail(@Param("mailfrom") String mailfrom,
			@Param("dateiname") String dateiname, @Param("dateigröße") long dateigröße,
			@Param("erstellt") LocalDateTime erstellt);

	@Query("select d.DatenlieferungId from entities.Datenlieferung d, entities.Datenaustausch a where a.richtung = tho.nill.datenlieferung.simpleAttributes.Richtung.EINGANG and d.bestätigt is null and d.fehler=0 and d.datenAnnahmeIK = a.datenAnnahmeIK and d.versenderIK = a.versenderIK and d.datenArt = a.datenArt and a.host = :host and d.physDateiname = :dateiname and d.dateigröße_übertragung =  :dateigröße  and d.erstellt = :erstellt and a.verbindung = tho.nill.datenlieferung.simpleAttributes.Verbindungsart.SFTP")
	public List<Long> getNichtBestätigteDatenlieferungenSftp(@Param("host") String host,
			@Param("dateiname") String dateiname, @Param("dateigröße") long dateigröße,
			@Param("erstellt") LocalDateTime erstellt);

}