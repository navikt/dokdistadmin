package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.VarselStatusCode;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
							and dok.avstemtReferanse is null
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

	@Override
	public void updateDistribusjonStatus(Long distribusjonInfoId,
										 DistribusjonStatusCode distribusjonStatus,
										 String endretAv) {
		Query query = entityManager.createQuery(
						"""
										update DistribusjonInfo dist set dist.distribusjonStatus = :distribusjonStatus,
								dist.changeStamp.endretAv = :endretAv,
								dist.changeStamp.endretDato = current_timestamp
								where dist.distribusjonInfoId in :distribusjonInfoId
								""")
				.setParameter("distribusjonInfoId", distribusjonInfoId)
				.setParameter("distribusjonStatus", distribusjonStatus)
				.setParameter("endretAv", endretAv);
		query.executeUpdate();
	}

	@Override
	public void updateDistribusjonInfoVarselStatus(Long distribusjonInfoId,
											VarselStatusCode varselStatus,
											String endretAv) {
		Query query = entityManager.createQuery(
						"""
											update DistribusjonInfo dist set dist.varselStatus = :varselStatus,
								dist.changeStamp.endretAv = :endretAv,
								dist.changeStamp.endretDato = current_timestamp
								where dist.distribusjonInfoId in :distribusjonInfoId
												""")
				.setParameter("distribusjonInfoId", distribusjonInfoId)
				.setParameter("varselStatus", varselStatus)
				.setParameter("endretAv", endretAv);
		query.executeUpdate();
	}
}
