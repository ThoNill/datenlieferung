package repositories;

import java.math.BigInteger;
import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import entities.Zertifikat;
import tho.nill.datenlieferung.simpleAttributes.IK;

@Repository
public interface ZertifikatRepository extends JpaRepository<Zertifikat, Long> {

	@Query("select z from entities.Zertifikat z where z.ik = :ik and z.von <= :zeit and z.bis >= :zeit order by z.von desc")
	List<Zertifikat> getZertifikat(@Param("ik") IK versenderIK, @Param("zeit") Instant zeit);

	@Query("select z from entities.Zertifikat z where z.serialId = :serialId ")
	List<Zertifikat> getZertifikatFromSerialId(@Param("serialId") BigInteger serialId);

}