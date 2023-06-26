package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DistribusjonsTypeKode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
	public Stream<DokumentInfo> fetchDokumentInfoList(List<String> journalpostIds, List<DistribusjonsTypeKode> distribusjonsTyper, List<DokumentStatusCode> dokumentStatus, Optional<DistribusjonKanalCode> distribusjonsKanal) {
		TypedQuery<DokumentInfo> query = entityManager.createQuery(
						"""
									select distinct dok
									from DokumentInfo dok
									join fetch dok.distribusjonInfo distinfo
									left join fetch dok.postadresse
									left join fetch dok.dokumentReferanses
									where dok.arkivkode in (:journalpostIds)
								""" +
								(dokumentStatus.isEmpty() ? "" : " and dok.dokumentStatus in (:dokumentstatuser) ") +
								(distribusjonsTyper.isEmpty() ? "" : " and distinfo.distribusjonstype in (:distribusjonstyper) ") +
								(distribusjonsKanal.isEmpty() ? "" : " and distinfo.distribusjonKanal = :kanal ")
						, DokumentInfo.class)
				.setHint(PASS_DISTINCT_THROUGH, false)
				.setParameter("journalpostIds", journalpostIds);

		if (!dokumentStatus.isEmpty()) {
			query.setParameter("dokumentstatuser", dokumentStatus);
		}
		if (!distribusjonsTyper.isEmpty()) {
			query.setParameter("distribusjonstyper", distribusjonsTyper);
		}
		distribusjonsKanal.ifPresent(kanal ->
				query.setParameter("kanal", kanal)
		);

		return query.getResultStream();
	}
}
