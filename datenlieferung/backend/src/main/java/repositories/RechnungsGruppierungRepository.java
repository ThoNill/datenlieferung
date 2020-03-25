package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entities.RechnungsGruppierung;
import interfaces.IRechnungsGruppierungRepository;

@Repository
public interface RechnungsGruppierungRepository
		extends JpaRepository<RechnungsGruppierung, Long>, IRechnungsGruppierungRepository {

}