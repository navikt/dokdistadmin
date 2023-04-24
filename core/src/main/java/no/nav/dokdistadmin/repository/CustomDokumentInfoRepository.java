package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;

import java.util.List;

public interface CustomDokumentInfoRepository {

	List<Long> findEkspedertDokumentInfo(int topN);

	List<DokumentInfo> fetchEkspedertDokumentInfo(List<Long> dokumentInfoIds);

	DokumentInfo fetchDokumentInfo(Long dokumentInfoId);

	void updateDokumentStatus(Long dokumentInfoId, DokumentStatusCode dokumentStatus, String endretAv);

	void updateDokumentKonversasjonsId(Long dokumentInfoId, String konversasjonId, String endretAv);

	void updateDokumentDigitalDistribujonAdresse(Long dokumentInfoId, String digitalPostkasseAdresse,
												 String digitalDistributorId, String endretAv);

}
