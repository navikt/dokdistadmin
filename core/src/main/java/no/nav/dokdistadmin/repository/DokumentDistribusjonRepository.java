package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.VarselStatusCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface DokumentDistribusjonRepository extends CrudRepository<DistribusjonInfo, Long>, CustomDokumentDistribusjonRepository {

	DistribusjonInfo getDistribusjonInfoByDistribusjonId(String distribusjonId);

	@Query("""
				select dist from DistribusjonInfo dist
				join dist.dokumentInfos dok
				where dok.dokumentInfoId = :dokumentInfoId
			""")
	DistribusjonInfo getDistribusjonInfoByDokumentInfoId(@Param("dokumentInfoId") Long dokumentInfoId);

	@Modifying
	@Query("""
			update DistribusjonInfo dist set dist.distribusjonStatus = :distribusjonStatus,
			dist.changeStamp.endretAv = :endretAv,
			dist.changeStamp.endretDato = current_timestamp
			where dist.distribusjonInfoId in :distribusjonInfoId
			""")
	void updateDistribusjonStatus(@Param("distribusjonInfoId") Long distribusjonInfoId,
								  @Param("distribusjonStatus") DistribusjonStatusCode distribusjonStatus,
								  @Param("endretAv") String endretAv);

	@Modifying
	@Query("""
			update DistribusjonInfo dist set dist.varselStatus = :varselStatus,
			dist.changeStamp.endretAv = :endretAv,
			dist.changeStamp.endretDato = current_timestamp
			where dist.distribusjonInfoId in :distribusjonInfoId
							""")
	void updateDistribusjonInfoVarselStatus(@Param("distribusjonInfoId") Long distribusjonInfoId,
											@Param("varselStatus") VarselStatusCode varselStatus,
											@Param("endretAv") String endretAv);
}
