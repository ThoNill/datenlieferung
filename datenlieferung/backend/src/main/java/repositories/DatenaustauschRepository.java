package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import entities.Datenaustausch;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;
import tho.nill.datenlieferung.simpleAttributes.Richtung;
import tho.nill.datenlieferung.simpleAttributes.Verbindungsart;

@Repository
public interface DatenaustauschRepository extends JpaRepository<Datenaustausch, Long> {

	@Query("select d from entities.Datenaustausch d where d.versenderIK = :versenderIK and d.datenAnnahmeIK = :datenAnnahmeIK  and d.datenPrüfungsIK = :datenPrüfungsIK and d.richtung = :richtung and d.datenArt = :datenArt and d.verbindung = :verbindung ")
	public List<Datenaustausch> getDatenaustausch(@Param("versenderIK") IK versenderIK,
			@Param("datenAnnahmeIK") IK datenAnnahmeIK, @Param("datenPrüfungsIK") IK datenPrüfungsIK,
			@Param("richtung") Richtung richtung, @Param("datenArt") DatenArt datenArt,
			@Param("verbindung") Verbindungsart verbindung);

	public default List<Datenaustausch> sucheDatenaustausch(IK versenderIK, IK datenAnnahmeIK, IK datenPrüfungsIK,
			Richtung richtung, DatenArt datenArt, Verbindungsart verbindung) {
		List<Datenaustausch> l = getDatenaustausch(versenderIK, datenAnnahmeIK, datenPrüfungsIK, richtung, datenArt,
				verbindung);
		if (!l.isEmpty()) {
			return l;
		}
		return getDatenaustausch(versenderIK, datenAnnahmeIK, new IK(0), richtung, datenArt, verbindung);
	}

	@Query("select d.verbindung from entities.Datenaustausch d where d.versenderIK = :versenderIK and d.datenAnnahmeIK = :datenAnnahmeIK  and d.datenPrüfungsIK = :datenPrüfungsIK and d.richtung = :richtung and d.datenArt = :datenArt ")
	public List<Verbindungsart> getVerbindung(@Param("versenderIK") IK versenderIK,
			@Param("datenAnnahmeIK") IK datenAnnahmeIK, @Param("datenPrüfungsIK") IK datenPrüfungsIK,
			@Param("richtung") Richtung richtung, @Param("datenArt") DatenArt datenArt);

	public default List<Verbindungsart> sucheVerbindung(IK versenderIK, IK datenAnnahmeIK, IK datenPrüfungsIK,
			Richtung richtung, DatenArt datenArt) {
		List<Verbindungsart> l = getVerbindung(versenderIK, datenAnnahmeIK, datenPrüfungsIK, richtung, datenArt);
		if (!l.isEmpty()) {
			return l;
		}
		return getVerbindung(versenderIK, datenAnnahmeIK, new IK(0), richtung, datenArt);
	}

	@Query("select d from entities.Datenaustausch d where  d.richtung = :richtung and d.verbindung = :verbindungsArt ")
	public List<Datenaustausch> getVerbindung(@Param("richtung") Richtung richtung,
			@Param("verbindungsArt") Verbindungsart verbindungsArt);

}