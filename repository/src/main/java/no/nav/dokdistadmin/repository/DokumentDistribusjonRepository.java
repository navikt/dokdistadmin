package no.nav.dokdistadmin.repository;


import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.Feilkvittering;
import no.nav.dokdistadmin.domain.exception.DuplicateResponseException;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

/**
 * Interface for data access operations
 *
 * @author Joakim Bjørnstad, Visma Consulting
 */
public interface DokumentDistribusjonRepository {
	/**
	 * Persists a new DistribusjonInfo
	 *
	 * @param distribusjonInfo The object to persist
	 * @return The persisted object in a managed state
	 */
	DistribusjonInfo saveNewDistribusjonInfo(DistribusjonInfo distribusjonInfo);

	/**
	 * Explicitly updates a DistribusjonInfo
	 *
	 * @param distribusjonInfo The object to update
	 */
	void updateDistribusjonInfo(DistribusjonInfo distribusjonInfo);

	/**
	 * Explicitly updates a DokumentInfo
	 *
	 * @param dokumentInfo The object to update
	 */
	void updateDokumentInfo(DokumentInfo dokumentInfo);

	/**
	 * Explicitly updates a DokumentInfo i bulk
	 *
	 * @param dokumentInfoIds The object to update
	 */
	void updateDokumentInfosAvstemtArkivDato(List<Long> dokumentInfoIds);

	/**
	 * Finds a DistribusjonInfo based on the Id.
	 *
	 * @param distribusjonInfoId The id to find with
	 * @return null if distribusjonInfoId is null or not found. Otherwise, the DistribusjonInfo
	 */
	DistribusjonInfo findDistribusjonInfoById(Long distribusjonInfoId);

	/**
	 * Finds a DistribusjonInfo based on distribusjonId
	 *
	 * @param distribusjonId The distribusjonId to find with
	 * @return null if distribusjonId is null or not found. Otherwise, the DistribusjonInfo
	 */
	DistribusjonInfo findDistribusjonInfoByDistribusjonId(String distribusjonId);

	/**
	 * Finds a DokumentInfo based on dokumentId
	 *
	 * @param dokumentId The dokumentId to find with
	 * @return null if dokumentId is null or not found. Otherwise, the DokumentInfo
	 */
	DokumentInfo findDokumentInfoByDokumentId(String dokumentId);

	/**
	 * Persists a new DokumentInfo
	 *
	 * @param dokumentInfo The object to persist.
	 */
	void saveNewDokumentInfo(DokumentInfo dokumentInfo);

	DokumentInfo findDokumentInfoByDokumentInfoId(Long dokumentInfoId);

	/**
	 * Update status of all DokumentInfos that are related to the supplied DistribusjonInfo
	 *
	 * @param distribusjonInfo The DistribusjonInfo
	 * @param dokumentStatus   The new dokumentStatus
	 */
	void updateStatusForAllDokumentInfosRelatedTo(DistribusjonInfo distribusjonInfo, DokumentStatusCode dokumentStatus);

	/**
	 * Update status of all DokumentInfos that are related to the supplied DistribusjonInfo to Ekspedert (And sets ekspedert dato)
	 */
	void updateStatusToEkspedertForAllDokumentInfosRelatedTo(DistribusjonInfo distribusjonInfo);

	/**
	 * Find a DokumentInfo based on konversasjonsId
	 *
	 * @param konversasjonsId The konversasjonsid to find with
	 * @return null if konversasjonsId is null or not found. Otherwise, the DokumentInfo
	 * @throws no.nav.dokdistadmin.domain.exception.DuplicateResponseException if more than one result is found
	 */
	DokumentInfo findDokumentInfoByKonversasjonId(String konversasjonsId) throws DuplicateResponseException;

	/**
	 * Find a DokumentInfo based on konversasjonsId
	 *
	 * @param arkivkode The arkivkode to find with
	 * @return null if arkivkode is null or not found. Otherwise, return DokumentInfo
	 */
	DokumentInfo findDokumentInfoByJournalpostId(String arkivkode);

	/**
	 * Finds the PostDestinasjon based on Landkode
	 *
	 * @param landkode the Landkode
	 * @return the PostDestinasjon
	 */
	String findPostDestinasjon(String landkode);

	/**
	 * Finds a list of DokumentInfo that has a status from dokumentStatusList and is set to be distributed to distribusjonKanal
	 *
	 * @param dokumentStatusList A list of dokument statuses which DokumentInfo must have one of
	 * @param distribusjonKanal  The distribusjonKanal the DokumentInfo is (to be) distributed to
	 * @return a list of DokumentInfo satisfying the given criterias
	 */
	List<DokumentInfo> findDokumentInfoByDokumentStatusAndDistribusjonKanal(List<DokumentStatusCode> dokumentStatusList, DistribusjonKanalCode distribusjonKanal, LocalDateTime nyereAv);

	/**
	 * Finds a list of DistribusjonInfo that has a status from dokumentStatusList and is set to be distributed to distribusjonKanal
	 *
	 * @param dokumentStatusCodes A list of dokument statuses which DokumentInfo must have one of
	 * @param distribusjonKanal   The distribusjonKanal the DokumentInfo is (to be) distributed to
	 * @return a list of DistribusjonInfo satisfying the given criterias
	 */
	List<DistribusjonInfo> findDistribusjonInfoByDokumentStatusAndDistribusjonKanal(EnumSet<DokumentStatusCode> dokumentStatusCodes, DistribusjonKanalCode distribusjonKanal, long antallTimer);

	DistribusjonInfo findDistribusjonInfoByDokumentInfoId(Long dokumnentInfoId);

	Feilkvittering saveFeilkvittering(Feilkvittering feilkvittering);

	List<DokumentInfo> findEkspedertDokumentInfo(int maxResult);
}
