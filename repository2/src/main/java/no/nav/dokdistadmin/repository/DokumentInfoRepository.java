package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DokumentInfoRepository extends JpaRepository<DokumentInfo, Long> {

	DokumentInfo findDokumentInfoByDokumentId(String dokumentId);

	DokumentInfo findDokumentInfoByDokumentInfoId(Long dokumentInfoId);

	DokumentInfo findDokumentInfoByKonversasjonId(String konversasjonsId);

	DokumentInfo findDokumentInfoByArkivkode(String arkivkode);

	@Query("""
					select dok from DokumentInfo dok, DistribusjonInfo dis
					where dok.dokumentStatus IN (:dokumentStatusList)
					and dok.distribusjonInfo = dis
					and dis.distribusjonKanal = :distribusjonKanal
					and dok.changeStamp.opprettetDato >= :opprettetEtter
			""")
	List<DokumentInfo> findDokumentInfoByDokumentStatusAndDistribusjonKanal(
			@Param("dokumentStatusList") List<DokumentStatusCode> dokumentStatusList,
			@Param("distribusjonKanal") DistribusjonKanalCode distribusjonKanal,
			@Param("opprettetEtter") LocalDateTime opprettetEtter);

	@Query("""
				select dok from  DokumentInfo dok  inner join DistribusjonInfo dist on dist.distribusjonInfoId = dok.distribusjonInfo.distribusjonInfoId
						left outer join VarselInfo  vai on vai.dokumentInfo.dokumentInfoId = dok.dokumentInfoId
			                left outer join Postadresse pa on pa.postadresseId = dok.postadresse.postadresseId
			                 where dok.dokumentStatus = 'EKSPEDERT'
			                 and dok.avstemtArkivDato is null
			                 and dok.arkivSystem = 'JOARK'
			                 and dok.arkivkode is not null
			                 and dok.ekspedertDato is not null
			                 and dok.ekspedertDato >= '2022-10-01'
			                 order by dok.ekspedertDato
			""")
	Page<DokumentInfo> findEkspedertDokumentInfo(Pageable pageable);

}
