package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.config.RepositoryTestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { RepositoryTestConfig.class })
@Transactional
public abstract class RepositoryTest {
	
	@PersistenceContext
	protected EntityManager entityManager;
	
	@BeforeEach
	public void setUp() {
		if (MDC.get(USER_ID) == null) {
			MDC.put(USER_ID, "repoTest");
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
	
	@AfterEach
	public void tearDown() {
		MDC.remove(USER_ID);
	}
	
}
