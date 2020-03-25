package repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import entities.DateiNummer;
import tho.nill.datenlieferung.simpleAttributes.DateiNummerArt;
import tho.nill.datenlieferung.simpleAttributes.DatenArt;
import tho.nill.datenlieferung.simpleAttributes.IK;

@Repository
public interface DateiNummerRepository extends JpaRepository<DateiNummer, Long> {

	@Modifying
	@Query(nativeQuery = true,value = "update DateiNummer set aktuelleNummer = aktuelleNummer + 1 where DateiNummerId = ?1")
	void updateAktuelleNummer(long DateiNummernId);
	
	@Query("select u.aktuelleNummer from DateiNummer u where u.DateiNummerId = ?1")
	int getAktuelleNummer(long DateiNummernId);
	
	@Query("select u.DateiNummerId  from DateiNummer u  where u.nummernArt = ?1 and u.datenArt = ?2 and u.ik = ?3 and u.versenderIK = ?4 and u.jahr = ?5")
	Optional<Long> getId(DateiNummerArt nummernArt,DatenArt datenArt,IK ik, IK versenderIK,int jahr);

	@Query("select max(u.korrekturnummer)+1 from Datenlieferung u, Datenlieferung orig where orig.DatenlieferungId = ?1 and u.mj = orig.mj and u.versenderIK = orig.versenderIK and u.datenPrüfungsIK = orig.datenPrüfungsIK and u.datenArt = orig.datenArt and u.dateinummer = orig.dateinummer  ")
	int getNächsteKorrekturNummer(long DatenlieferungId);
	
}