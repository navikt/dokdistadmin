package no.nav.dokdistadmin.repository.support.itest;

import no.nav.dokdistadmin.repository.PingRepository;
import no.nav.dokdistadmin.repository.RepositoryTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests PingRepository. Since Hibernate doesn't generate tables for codes in
 * HSQLDB, the test explicitly creates the K_DIST_STATUS table which already
 * exists in the Oracle DB.
 * 
 * @author Thomas Eugen Bjørge, Visma Consulting
 */
public class Jpa2PingRepositoryTest extends RepositoryTest {

	@Autowired
	private PingRepository pingRepository;
	
	@Before
	public void setUp() {
		entityManager.createNativeQuery("create table K_DIST_STATUS(k_dist_status VARCHAR(20) NOT NULL)").executeUpdate();
	}
	
	@After
	public void tearDown() {
		entityManager.createNativeQuery("drop table K_DIST_STATUS").executeUpdate();
	}
	
	@Test
	public void shouldCountDistStatusRows() {
		Long result = pingRepository.countDistStatusRows();

		assertThat(result, is(0L));
	}

}
