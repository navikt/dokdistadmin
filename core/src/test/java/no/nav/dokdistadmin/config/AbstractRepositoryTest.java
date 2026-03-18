package no.nav.dokdistadmin.config;

import jakarta.persistence.EntityManager;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import no.nav.dokdistadmin.repository.FeilkvitteringRepository;
import no.nav.dokdistadmin.repository.FilinfoRepository;
import no.nav.dokdistadmin.repository.LandkodePostDestRepository;
import no.nav.dokdistadmin.repository.VarselInfoRepository;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TestTransaction;

import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;

@DataJpaTest
@ContextConfiguration(classes = {RepositoryConfig.class})
@ActiveProfiles("itest")
public abstract class AbstractRepositoryTest {

	@Autowired
	protected EntityManager entityManager;

	@Autowired
	protected DokumentDistribusjonRepository dokumentDistribusjonRepository;

	@Autowired
	protected DokumentInfoRepository dokumentInfoRepository;

	@Autowired
	protected FeilkvitteringRepository feilkvitteringRepository;

	@Autowired
	protected FilinfoRepository filinfoRepository;

	@Autowired
	protected LandkodePostDestRepository landkodePostDestRepository;

	@Autowired
	protected VarselInfoRepository varselInfoRepository;

	@BeforeEach
	public void setUp() {
		if (MDC.get(USER_ID) == null) {
			MDC.put(USER_ID, "repoTest");
		}
		emptyDatabases();
	}

	public void emptyDatabases() {
		entityManager.getEntityManagerFactory()
				.unwrap(SessionFactoryImplementor.class)
				.getSchemaManager()
				.truncateMappedObjects();
	}

	public void commitAndBeginNewTransaction() {
		TestTransaction.flagForCommit();
		TestTransaction.end();
		TestTransaction.start();
	}

}
