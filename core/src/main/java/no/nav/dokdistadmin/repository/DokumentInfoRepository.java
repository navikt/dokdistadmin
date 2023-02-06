package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DokumentInfoRepository extends CrudRepository<DokumentInfo, Long>, CustomDokumentInfoRepository {

	DokumentInfo findDokumentInfoByDokumentId(String dokumentId);

	DokumentInfo findDokumentInfoByDokumentInfoId(Long dokumentInfoId);

	DokumentInfo findDokumentInfoByKonversasjonId(String konversasjonsId);

	DokumentInfo findDokumentInfoByArkivkode(String arkivkode);

	@Query("""
			select dok from DokumentInfo dok, DistribusjonInfo dis
				where dok.dokumentStatus in (:dokumentStatusList)
				and dok.distribusjonInfo = dis
				and dis.distribusjonKanal = :distribusjonKanal
				and dok.changeStamp.opprettetDato >= :opprettetEtter
			""")
	List<DokumentInfo> findDokumentInfoByDokumentStatusAndDistribusjonKanal(
			@Param("dokumentStatusList") List<DokumentStatusCode> dokumentStatusList,
			@Param("distribusjonKanal") DistribusjonKanalCode distribusjonKanal,
			@Param("opprettetEtter") LocalDateTime opprettetEtter);

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
			update DokumentInfo dok set
			   	dok.avstemtReferanse = :avstemtReferanse,
			   	dok.avstemtDato = current_timestamp,
				dok.changeStamp.endretAv = :endretAv,
				dok.changeStamp.endretDato = current_timestamp,
				dok.version = dok.version + 1
				where dok.dokumentInfoId in (:dokumentInfoIdList)
				and dok.avstemtReferanse is null
			""")
	int updateAvstemtReferanseAndAvstemtDatoForIdIn(
			@Param("avstemtReferanse") String avstemtReferanse,
			@Param("dokumentInfoIdList") List<Long> dokumentInfoIdList,
			@Param("endretAv") String endretAv);

	@Modifying
	@Query("""
			update DokumentInfo dok set dok.dokumentStatus = :dokumentstatus,
				dok.changeStamp.endretAv = :endretAv,
				dok.changeStamp.endretDato = current_timestamp,
				dok.version = dok.version + 1
				where dok.distribusjonInfo = :distribusjoninfo
			""")
	void updateStatusForAllDokumentInfosRelatedTo(
			@Param("distribusjoninfo") DistribusjonInfo distribusjoninfo,
			@Param("dokumentstatus") DokumentStatusCode dokumentstatus,
			@Param("endretAv") String endretAv);

	@Modifying
	@Query("""
			update DokumentInfo dok set dok.dokumentStatus = 'EKSPEDERT',
				dok.ekspedertDato = current_timestamp,
				dok.changeStamp.endretAv = :endretAv,
				dok.changeStamp.endretDato = current_timestamp,
				dok.version = dok.version + 1
				where dok.distribusjonInfo = :distribusjoninfo
			""")
	void updateStatusToEkspedertForAllDokumentInfosRelatedTo(
			@Param("distribusjoninfo") DistribusjonInfo distribusjoninfo,
			@Param("endretAv") String endretAv);

}
