package repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import entities.EingeleseneDatei;
import interfaces.IEingeleseneDateiRepository;

@Repository
public interface EingeleseneDateiRepository extends JpaRepository<EingeleseneDatei, Long>, IEingeleseneDateiRepository {

	@Query("select EingeleseneDateiId from EingeleseneDatei where bestätigt is null and fehler = 0 ")
	public List<Long> getNichtBestätigteDateien();

}