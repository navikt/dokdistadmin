package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DistribusjonsTypeKode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface CustomDokumentInfoRepository {

	List<Long> findEkspedertDokumentInfo(int topN);

	List<DokumentInfo> fetchEkspedertDokumentInfo(List<Long> dokumentInfoIds);

	DokumentInfo fetchDokumentInfo(Long dokumentInfoId);

	Stream<DokumentInfo> fetchDokumentInfoList(List<String> journalpostIds, List<DistribusjonsTypeKode> distribusjonsTyper, List<DokumentStatusCode> dokumentStatus, Optional<DistribusjonKanalCode> distribusjonsKanal);

}
