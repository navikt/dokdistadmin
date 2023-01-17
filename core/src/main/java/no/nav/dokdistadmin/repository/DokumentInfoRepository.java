package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;

import java.time.LocalDateTime;
import java.util.List;

public interface DokumentInfoRepository extends CrudRepository<DokumentInfo, Long>, CustomDokumentInfoRepository {

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

}
