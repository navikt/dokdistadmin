package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

public interface DokumentDistribusjonRepository extends JpaRepository<DistribusjonInfo, Long> {

	DistribusjonInfo getDistribusjonInfoByDistribusjonId(String distribusjonId);

	@Modifying
	@Query("""
			update DokumentInfo dok
				set dok.avstemtArkivDato = current_timestamp,
				dok.changeStamp.endretAv = :endretAv
				where dok.dokumentInfoId in :dokumentInfoIds
			""")
	void updateDokumentInfosAvstemtArkivDato(
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

	@Query("""
			select dist
				from DistribusjonInfo dist join fetch dist.dokumentInfos dok
				where dok.dokumentStatus not in :dokumentStatus
			   	and dok.avstemtDato is null
				and dist.distribusjonKanal = :distribusjonKanal
				and dist.changeStamp.opprettetDato between :etterAntallDagerSiden and :foerAntallTimerSiden
			 	order by dist.distribusjonInfoId, dok.dokumentId
			""")
	List<DistribusjonInfo> findDistribusjonInfoByDokumentStatusAndDistribusjonKanal(
			@Param("dokumentStatus") EnumSet<DokumentStatusCode> dokumentStatus,
			@Param("distribusjonKanal") DistribusjonKanalCode distribusjonKanal,
			@Param("etterAntallDagerSiden") LocalDateTime etterAntallDagerSiden,
			@Param("foerAntallTimerSiden") LocalDateTime foerAntallTimerSiden);
}
