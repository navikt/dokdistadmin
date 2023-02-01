package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DokumentDistribusjonRepository extends CrudRepository<DistribusjonInfo, Long>, CustomDokumentDistribusjonRepository {

	DistribusjonInfo getDistribusjonInfoByDistribusjonId(String distribusjonId);

	// TODO: Sjekk om noen av disse burde flyttes til DokumentInfoRepo
	@Modifying
	@Query("""
			update DokumentInfo dok
				set dok.avstemtArkivDato = current_timestamp,
				dok.changeStamp.endretAv = :endretAv,
				dok.changeStamp.endretDato = current_timestamp,
				dok.version = dok.version + 1
				where dok.dokumentInfoId in :dokumentInfoIds
			""")
	int updateDokumentInfosAvstemtArkivDato(
			@Param("dokumentInfoIds") List<Long> dokumentInfoIds,
			@Param("endretAv") String endretAv);

	@Modifying
	@Query("""
			update DokumentInfo di set di.dokumentStatus = :dokumentstatus,
				di.changeStamp.endretAv = :endretAv,
				di.changeStamp.endretDato = current_timestamp,
				di.version = di.version + 1
				where di.distribusjonInfo = :distribusjoninfo
			""")
	void updateStatusForAllDokumentInfosRelatedTo(
			@Param("distribusjoninfo") DistribusjonInfo distribusjoninfo,
			@Param("dokumentstatus") DokumentStatusCode dokumentstatus,
			@Param("endretAv") String endretAv);


	@Modifying
	@Query("""
			update DokumentInfo di set di.dokumentStatus = 'EKSPEDERT',
				di.ekspedertDato = current_timestamp,
				di.changeStamp.endretAv = :endretAv,
				di.changeStamp.endretDato = current_timestamp,
				di.version = di.version + 1
				where di.distribusjonInfo = :distribusjoninfo
			""")
	void updateStatusToEkspedertForAllDokumentInfosRelatedTo(
			@Param("distribusjoninfo") DistribusjonInfo distribusjoninfo,
			@Param("endretAv") String endretAv);

}
