package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import entities.VersenderKey;
import tho.nill.datenlieferung.simpleAttributes.IK;

@Repository
public interface VersenderKeyRepository extends JpaRepository<VersenderKey, Long> {

	@Query("select k from VersenderKey k where k.aktiv = true and k.versenderIK = ?1 order by k.von desc")
	List<VersenderKey> geKey(IK versenderIK);

}