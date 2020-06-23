package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import entities.RechnungAuftrag;

@Repository
public interface RechnungAuftragRepository extends JpaRepository<RechnungAuftrag, Long> {

	@Query("select new map( ra.RechnungAuftragId as rechnungAuftragId, ra.mj as mj,	ra.rechnungsNummer as rechnungsnummer,	ra.versenderIK as versenderIK ,	ra.datenAnnahmeIK as datenAnnahmeIK,	ra.datenPrüfungsIK as datenPrüfungsIK,	ra.datenArt as datenArt)	from entities.RechnungAuftrag ra")
	public List<Object> getListe();

}