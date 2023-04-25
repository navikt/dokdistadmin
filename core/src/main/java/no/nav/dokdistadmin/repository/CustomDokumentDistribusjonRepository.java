package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.VarselStatusCode;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

public interface CustomDokumentDistribusjonRepository {

	List<DistribusjonInfo> findDistribusjonInfoByDokumentStatusAndDistribusjonKanal(
			EnumSet<DokumentStatusCode> dokumentStatus,
			DistribusjonKanalCode distribusjonKanal,
			LocalDateTime opprettetEtter,
			LocalDateTime opprettetFoer);

	void updateDistribusjonStatus(Long distribusjonInfoId,
								  DistribusjonStatusCode distribusjonStatus,
								  String endretAv);

	void updateDistribusjonInfoVarselStatus(Long distribusjonInfoId,
											VarselStatusCode varselStatus,
											String endretAv);
}
