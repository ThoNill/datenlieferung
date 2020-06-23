package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entities.RechnungsGruppierung;

@Repository
public interface RechnungsGruppierungRepository extends JpaRepository<RechnungsGruppierung, Long> {

}