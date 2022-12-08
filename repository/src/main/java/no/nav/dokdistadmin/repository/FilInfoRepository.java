package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.FilInfo;
import no.nav.dokdistadmin.domain.FilStatusCode;

/**
 * Repository for working with FilInfo.
 *
 * @author Thomas Eugen Bjørge, Visma Consulting
 */
public interface FilInfoRepository {

	/**
	 * Persists av new FilInfo.
	 *
	 * @param filInfo The FilInfo to save.
	 * @return The saved FilInfo
	 */
	FilInfo saveNewFilInfo(FilInfo filInfo);

	/**
	 * Explicitly update a FilInfo
	 *
	 * @param filInfo The FilInfo to update.
	 */
	FilInfo updateFilInfo(FilInfo filInfo);

	/**
	 * Deletes a FilInfo by ID
	 *
	 * @param filInfoId The FilInfoId to delete.
	 */
	void deleteFilInfoById(Long filInfoId);

	/**
	 * Finds a FilInfo by ID
	 *
	 * @param filInfoId The filInfoId to look up
	 * @return The FilInfo or null if it can not be found
	 */
	FilInfo findFilInfoById(Long filInfoId);

	/**
	 * Finds a FilInfo by filnavn
	 *
	 * @param filnavn The filename to search for
	 * @return The FilInfo or null if it can not be found
	 */
	FilInfo findFilInfoByFilnavn(String filnavn);

	/**
	 * Updates the FilInfo identified by the Id to the given statusCode, transactional
	 *
	 * @param filInfoId  the filInfoId to update
	 * @param statusCode the status to set
	 */
	FilInfo updateFilInfoStatus(Long filInfoId, FilStatusCode statusCode);

}
