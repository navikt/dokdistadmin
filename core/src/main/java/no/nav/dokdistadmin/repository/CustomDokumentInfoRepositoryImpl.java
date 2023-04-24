package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

import static org.hibernate.annotations.QueryHints.PASS_DISTINCT_THROUGH;

@Repository
public class CustomDokumentInfoRepositoryImpl implements CustomDokumentInfoRepository {

	private final EntityManager entityManager;

	public CustomDokumentInfoRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<Long> findEkspedertDokumentInfo(int topN) {
		return entityManager.createQuery(
						"""
								select dok.dokumentInfoId
								from DokumentInfo dok
								where dok.dokumentStatus = 'EKSPEDERT'
								and dok.avstemtArkivDato is null
								and dok.arkivSystem = 'JOARK'
								and dok.arkivkode is not null
								and dok.ekspedertDato is not null
								and dok.ekspedertDato >= TO_DATE('2022-10-01', 'yyyy-mm-dd')
								order by dok.ekspedertDato""", Long.class)
				.setMaxResults(topN)
				.getResultList();
	}

	@Override
	public List<DokumentInfo> fetchEkspedertDokumentInfo(List<Long> dokumentInfoIds) {
		return entityManager.createQuery(
						"""
								select distinct dok
								from DokumentInfo dok
								join fetch dok.distribusjonInfo
								left join fetch dok.postadresse
								left join fetch dok.varselInfos
								where dok.dokumentInfoId in (:dokumentInfoIds)""", DokumentInfo.class)
				.setHint(PASS_DISTINCT_THROUGH, false)
				.setParameter("dokumentInfoIds", dokumentInfoIds)
				.getResultList();
	}

	@Override
	public DokumentInfo fetchDokumentInfo(Long dokumentInfoId) {
		return entityManager.createQuery(
						"""
								select distinct dok
								from DokumentInfo dok
								join fetch dok.distribusjonInfo
								left join fetch dok.postadresse
								left join fetch dok.dokumentReferanses
								where dok.dokumentInfoId = :dokumentInfoId""", DokumentInfo.class)
				.setHint(PASS_DISTINCT_THROUGH, false)
				.setParameter("dokumentInfoId", dokumentInfoId)
				.getResultStream().findFirst().orElse(null);
	}

	@Override
	public void updateDokumentStatus(Long dokumentInfoId, DokumentStatusCode dokumentStatus, String endretAv) {
		Query q = entityManager.createQuery(
						"""
								update DokumentInfo dok set dok.dokumentStatus = :dokumentStatus,
								dok.changeStamp.endretAv = :endretAv,
								dok.changeStamp.endretDato = current_timestamp
								where dok.dokumentInfoId in :dokumentInfoId
										"""
				).setParameter("dokumentInfoId", dokumentInfoId)
				.setParameter("dokumentStatus", dokumentStatus)
				.setParameter("endretAv", endretAv);

		q.executeUpdate();
	}

	@Override
	public void updateDokumentKonversasjonsId(Long dokumentInfoId, String konversasjonId, String endretAv) {
		Query q = entityManager.createQuery(
						"""
								update DokumentInfo dok set dok.konversasjonId = :konversasjonId,
								dok.changeStamp.endretAv = :endretAv,
								dok.changeStamp.endretDato = current_timestamp
								where dok.dokumentInfoId in :dokumentInfoId
										"""
				).setParameter("dokumentInfoId", dokumentInfoId)
				.setParameter("konversasjonId", konversasjonId)
				.setParameter("endretAv", endretAv);

		q.executeUpdate();
	}

	@Override
	public void updateDokumentDigitalDistribujonAdresse(Long dokumentInfoId, String digitalPostkasseAdresse,
														String digitalDistributorId, String endretAv) {

		Query q = entityManager.createQuery(
						"""
								update DokumentInfo dok set dok.digitalPostkasseAdresse = :digitalPostkasseAdresse,
												dok.digitalDistributorId = :digitalDistributorId,
												dok.changeStamp.endretAv = :endretAv,
												dok.changeStamp.endretDato = current_timestamp
												where dok.dokumentInfoId in :dokumentInfoId
								"""
				).setParameter("dokumentInfoId", dokumentInfoId)
				.setParameter("digitalPostkasseAdresse", digitalPostkasseAdresse)
				.setParameter("digitalDistributorId", digitalDistributorId)
				.setParameter("endretAv", endretAv);
		q.executeUpdate();

	}
}
