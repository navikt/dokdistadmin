package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DistribusjonsTypeKode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface CustomDokumentInfoRepository {

	List<Long> findEkspedertDokumentInfo(int topN, EnumSet<DistribusjonKanalCode> distribusjonKanal);

	List<DokumentInfo> fetchEkspedertDokumentInfo(List<Long> dokumentInfoIds);

	DokumentInfo fetchDokumentInfo(Long dokumentInfoId);

	Stream<DokumentInfo> fetchDokumentInfoList(List<Long> journalpostIds,
											   List<DistribusjonsTypeKode> distribusjonstyper,
											   List<DokumentStatusCode> dokumentstatus,
											   boolean inkluderAvstemte,
											   Optional<DistribusjonKanalCode> distribusjonskanal);

}
