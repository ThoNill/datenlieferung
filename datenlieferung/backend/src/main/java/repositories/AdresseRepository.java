package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import entities.Adresse;
import tho.nill.datenlieferung.simpleAttributes.AdressArt;
import tho.nill.datenlieferung.simpleAttributes.IK;

@Repository
public interface AdresseRepository extends JpaRepository<Adresse, Long> {
	@Query("select a from entities.Adresse a where a.ik = :ik and a.art = :art  ")
	public List<Adresse> getAdresse(@Param("ik") IK ik, @Param("art") AdressArt art);
}