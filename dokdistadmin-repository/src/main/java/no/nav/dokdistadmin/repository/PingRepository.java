package no.nav.dokdistadmin.repository;

/**
 * Repository used to verify that the database connection is working.
 *
 * @author Thomas Eugen Bjørge, Visma Consulting
 */
public interface PingRepository {

	/**
	 * Count the number of rows in the K_DIST_STATUS table.
	 * 
	 * @return The number of rows.
	 */
	Long countDistStatusRows();
	
}
