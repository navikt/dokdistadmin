package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DokumentInfo;

import java.util.List;

public interface CustomDokumentInfoRepository {

	List<Long> findEkspedertDokumentInfo(int topN);

	List<DokumentInfo> fetchEkspedertDokumentInfo(List<Long> dokumentInfoIds);

	DokumentInfo fetchDokumentInfo(Long dokumentInfoId);

}
