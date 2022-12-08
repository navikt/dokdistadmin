package no.nav.dokdistadmin.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import no.nav.dokdistadmin.config.RepositoryTestConfig;
import no.nav.dokdistadmin.domain.util.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for repository tests.
 *
 * @author Thomas Eugen Bjørge, Visma Consulting
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RepositoryTestConfig.class })
@Transactional
public abstract class RepositoryTest {
	
	@PersistenceContext
	protected EntityManager entityManager;
	
	@Before
	public void setUp() {
		if (MDC.get(Constants.USER_ID) == null) {
			MDC.put(Constants.USER_ID, "repoTest");
		}
		cleanDatabase();
	}

	private void cleanDatabase() {
		entityManager.createNativeQuery("delete from DIST_INFO_FIL_INFO").executeUpdate();
		entityManager.createNativeQuery("delete from DOK_INFO_FIL_INFO").executeUpdate();
		entityManager.createQuery("delete from FilInfo").executeUpdate();
		entityManager.createQuery("delete from DokumentInfo").executeUpdate();
		entityManager.createQuery("delete from DistribusjonInfo").executeUpdate();
	}
	
	@After
	public void tearDown() {
		MDC.remove(Constants.USER_ID);
	}
	
}
