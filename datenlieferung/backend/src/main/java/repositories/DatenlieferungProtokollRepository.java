package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entities.DatenlieferungProtokoll;

@Repository
public interface DatenlieferungProtokollRepository extends JpaRepository<DatenlieferungProtokoll, Long> {

}