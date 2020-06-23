package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entities.EingangText;

@Repository
public interface EingangTextRepository extends JpaRepository<EingangText, Long> {

}