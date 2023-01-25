package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import static org.hibernate.annotations.QueryHints.PASS_DISTINCT_THROUGH;

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
						select distinct dist
							from DistribusjonInfo dist join fetch dist.dokumentInfos dok
							where dok.dokumentStatus not in (:dokumentStatus)
							and dok.avstemtDato is null
							and dist.distribusjonKanal = :distribusjonKanal
							and dist.changeStamp.opprettetDato between :opprettetEtter and :opprettetFoer
							order by dist.distribusjonInfoId, dok.dokumentId""", DistribusjonInfo.class)
				.setHint(PASS_DISTINCT_THROUGH, false)
				.setParameter("dokumentStatus", dokumentStatus)
				.setParameter("distribusjonKanal", distribusjonKanal)
				.setParameter("opprettetEtter", opprettetEtter)
				.setParameter("opprettetFoer", opprettetFoer)
				.getResultList();
	}
}
