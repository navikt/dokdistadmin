package no.nav.dokdistadmin.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DistribusjonsTypeKode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import org.springframework.stereotype.Repository;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public class CustomDokumentInfoRepositoryImpl implements CustomDokumentInfoRepository {

	private final EntityManager entityManager;

	public CustomDokumentInfoRepositoryImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public List<Long> findEkspedertDokumentInfo(int topN, EnumSet<DistribusjonKanalCode> distribusjonKanal) {
		return entityManager.createQuery(
						"""
								select dok.dokumentInfoId
								from DokumentInfo dok
								join dok.distribusjonInfo di
								where dok.dokumentStatus = 'EKSPEDERT'
								and dok.avstemtArkivDato is null
								and dok.arkivSystem = 'JOARK'
								and dok.arkivkode is not null
								and dok.ekspedertDato is not null
								and dok.ekspedertDato >= TO_DATE('2022-10-01', 'yyyy-mm-dd')
								and di.distribusjonKanal in(:distribusjonKanal)
								order by dok.ekspedertDato""", Long.class)
				.setParameter("distribusjonKanal", distribusjonKanal)
				.setMaxResults(topN)
				.getResultList();
	}

	@Override
	public List<DokumentInfo> fetchEkspedertDokumentInfo(List<Long> dokumentInfoIds) {
		return entityManager.createQuery(
						"""
								select dok
								from DokumentInfo dok
								join fetch dok.distribusjonInfo
								left join fetch dok.postadresse
								left join fetch dok.varselInfos
								where dok.dokumentInfoId in (:dokumentInfoIds)""", DokumentInfo.class)
				.setParameter("dokumentInfoIds", dokumentInfoIds)
				.getResultList();
	}

	@Override
	public DokumentInfo fetchDokumentInfo(Long dokumentInfoId) {
		return entityManager.createQuery(
						"""
								select dok
								from DokumentInfo dok
								join fetch dok.distribusjonInfo
								left join fetch dok.postadresse
								left join fetch dok.dokumentReferanses
								where dok.dokumentInfoId = :dokumentInfoId""", DokumentInfo.class)
				.setParameter("dokumentInfoId", dokumentInfoId)
				.getResultStream().findFirst().orElse(null);
	}

	@Override
	public Stream<DokumentInfo> fetchDokumentInfoList(List<Long> journalpostIds,
													  List<DistribusjonsTypeKode> distribusjonstyper,
													  List<DokumentStatusCode> dokumentstatus,
													  boolean inkluderAvstemte,
													  Optional<DistribusjonKanalCode> distribusjonskanal) {
		TypedQuery<DokumentInfo> query = entityManager.createQuery(
						"""
									select dok
									from DokumentInfo dok
									join fetch dok.distribusjonInfo distinfo
									left join fetch dok.postadresse
									left join fetch dok.dokumentReferanses
									where dok.arkivkode in (:journalpostIds)
								""" +
								(dokumentstatus.isEmpty() ? "" : " and dok.dokumentStatus in (:dokumentstatuser) ") +
								(distribusjonstyper.isEmpty() ? "" : " and distinfo.distribusjonstype in (:distribusjonstyper) ") +
								(distribusjonskanal.isEmpty() ? "" : " and distinfo.distribusjonKanal = :kanal ") +
								(inkluderAvstemte ? "" : " and dok.avstemtDato is null ")
						, DokumentInfo.class)
				.setParameter("journalpostIds", journalpostIds);

		if (!dokumentstatus.isEmpty()) {
			query.setParameter("dokumentstatuser", dokumentstatus);
		}
		if (!distribusjonstyper.isEmpty()) {
			query.setParameter("distribusjonstyper", distribusjonstyper);
		}
		distribusjonskanal.ifPresent(kanal ->
				query.setParameter("kanal", kanal)
		);

		return query.getResultStream();
	}
}
