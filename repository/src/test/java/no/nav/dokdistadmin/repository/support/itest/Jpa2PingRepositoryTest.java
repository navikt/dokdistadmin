package no.nav.dokdistadmin.repository.support.itest;

import no.nav.dokdistadmin.repository.PingRepository;
import no.nav.dokdistadmin.repository.RepositoryTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests PingRepository. Since Hibernate doesn't generate tables for codes in
 * HSQLDB, the test explicitly creates the K_DIST_STATUS table which already
 * exists in the Oracle DB.
 */
public class Jpa2PingRepositoryTest extends RepositoryTest {

	@Autowired
	private PingRepository pingRepository;
	
	@BeforeEach
	public void setUp() {
		entityManager.createNativeQuery("create table K_DIST_STATUS(k_dist_status VARCHAR(20) NOT NULL)").executeUpdate();
	}
	
	@AfterEach
	public void tearDown() {
		entityManager.createNativeQuery("drop table K_DIST_STATUS").executeUpdate();
	}
	
	@Test
	public void shouldCountDistStatusRows() {
		assertEquals(0L, pingRepository.countDistStatusRows());
	}

}
