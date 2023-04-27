package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DokumentStatusCode;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

public interface CustomDokumentDistribusjonRepository {

	List<DistribusjonInfo> findDistribusjonInfoByDokumentStatusAndDistribusjonKanal(
			EnumSet<DokumentStatusCode> dokumentStatus,
			DistribusjonKanalCode distribusjonKanal,
			LocalDateTime opprettetEtter,
			LocalDateTime opprettetFoer);
}
