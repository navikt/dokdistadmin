package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.repository.projections.DokumentInfoIdHolder;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.EnumSet;
import java.util.List;

public interface DokumentInfoRepository extends CrudRepository<DokumentInfo, Long>, CustomDokumentInfoRepository {
	boolean existsByDokumentId(String dokumentId);

	DokumentInfo getReferenceById(Long dokumentInfoId);

	DokumentInfo findDokumentInfoByDokumentId(String dokumentId);

	DokumentInfo findDokumentInfoByDokumentInfoId(Long dokumentInfoId);

	DokumentInfo findDokumentInfoByKonversasjonId(String konversasjonsId);

	DokumentInfo findDokumentInfoByArkivkode(String arkivkode);

	List<DokumentInfoIdHolder> findIdsByDokumentId(String dokumentId);

	List<DokumentInfoIdHolder> findIdsByKonversasjonId(String konversasjonId);

	DokumentInfoIdHolder findTopByArkivkodeOrderByDokumentInfoIdDesc(String arkivkode);

	@Query("""
			select dok from DokumentInfo dok
			 join dok.distribusjonInfo dis
				where dok.dokumentStatus in (:dokumentStatusList)
				and dok.distribusjonInfo = dis
				and dis.distribusjonKanal = :distribusjonKanal
				and dok.changeStamp.opprettetDato >= TO_DATE('2022-01-01', 'yyyy-mm-dd')
			""")
	List<DokumentInfo> findDokumentInfoByDokumentStatusAndDistribusjonKanal(
			@Param("dokumentStatusList") EnumSet<DokumentStatusCode> dokumentStatusList,
			@Param("distribusjonKanal") DistribusjonKanalCode distribusjonKanal);

	@Modifying
	@Query("""
			update DokumentInfo dok
				set dok.avstemtArkivDato = current_timestamp,
				dok.changeStamp.endretAv = :endretAv,
				dok.changeStamp.endretDato = current_timestamp
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
				dok.changeStamp.endretDato = current_timestamp
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
				dok.changeStamp.endretDato = current_timestamp
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
				dok.changeStamp.endretDato = current_timestamp
				where dok.distribusjonInfo = :distribusjoninfo
			""")
	void updateStatusToEkspedertForAllDokumentInfosRelatedTo(
			@Param("distribusjoninfo") DistribusjonInfo distribusjoninfo,
			@Param("endretAv") String endretAv);


	@Modifying
	@Query("""
			update DokumentInfo dok set dok.dokumentStatus = :dokumentStatus,
			dok.changeStamp.endretAv = :endretAv,
			dok.changeStamp.endretDato = current_timestamp
			where dok.dokumentInfoId = :dokumentInfoId
			"""
	)
	void updateDokumentStatus(@Param("dokumentInfoId") Long dokumentInfoId,
							  @Param("dokumentStatus") DokumentStatusCode dokumentStatus,
							  @Param("endretAv") String endretAv);

	@Modifying
	@Query("""
			update DokumentInfo dok set dok.konversasjonId = :konversasjonId,
			dok.changeStamp.endretAv = :endretAv,
			dok.changeStamp.endretDato = current_timestamp
			where dok.dokumentInfoId = :dokumentInfoId
			"""
	)
	void updateDokumentKonversasjonsId(@Param("dokumentInfoId") Long dokumentInfoId,
									   @Param("konversasjonId") String konversasjonId,
									   @Param("endretAv") String endretAv);

	@Modifying
	@Query("""
			update DokumentInfo dok set dok.digitalPostkasseAdresse = :digitalPostkasseAdresse,
							dok.digitalDistributorId = :digitalDistributorId,
							dok.changeStamp.endretAv = :endretAv,
							dok.changeStamp.endretDato = current_timestamp
							where dok.dokumentInfoId = :dokumentInfoId
			""")
	void updateDokumentDigitalDistribujonAdresse(@Param("dokumentInfoId") Long dokumentInfoId,
												 @Param("digitalPostkasseAdresse") String digitalPostkasseAdresse,
												 @Param("digitalDistributorId") String digitalDistributorId,
												 @Param("endretAv") String endretAv);

}
