package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

@Repository
public class CustomDokumentDistribusjonRepositoryImpl implements CustomDokumentDistribusjonRepository {

	private final EntityManager entityManager;

	public CustomDokumentDistribusjonRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<DistribusjonInfo> findDistribusjonInfoByDokumentStatusAndDistribusjonKanal(
			EnumSet<DokumentStatusCode> dokumentStatus,
			DistribusjonKanalCode distribusjonKanal,
			LocalDateTime opprettetEtter,
			LocalDateTime opprettetFoer) {

		return entityManager.createQuery("""
						select dist
							from DistribusjonInfo dist join fetch dist.dokumentInfos dok
							where dok.dokumentStatus not in (:dokumentStatus)
							and dok.avstemtDato is null
							and dok.avstemtReferanse is null
							and dist.distribusjonKanal = :distribusjonKanal
							and dist.changeStamp.opprettetDato between :opprettetEtter and :opprettetFoer
							order by dist.distribusjonInfoId, dok.dokumentId""", DistribusjonInfo.class)
				.setParameter("dokumentStatus", dokumentStatus)
				.setParameter("distribusjonKanal", distribusjonKanal)
				.setParameter("opprettetEtter", opprettetEtter)
				.setParameter("opprettetFoer", opprettetFoer)
				.getResultList();
	}
}
