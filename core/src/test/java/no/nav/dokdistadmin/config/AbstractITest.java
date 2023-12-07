package no.nav.dokdistadmin.config;

import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import no.nav.dokdistadmin.repository.FeilkvitteringRepository;
import no.nav.dokdistadmin.repository.FilinfoRepository;
import no.nav.dokdistadmin.repository.LandkodePostDestRepository;
import no.nav.dokdistadmin.repository.VarselInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@Transactional
@SpringBootTest(
		classes = {ApplicationTestConfig.class},
		webEnvironment = RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles({"itest"})
public abstract class AbstractITest extends AbstractOauth2Test {

	@Autowired
	public WebTestClient webTestClient;

	@Autowired
	protected DokumentInfoRepository dokumentInfoRepository;

	@Autowired
	protected DokumentDistribusjonRepository dokumentDistribusjonRepository;

	@Autowired
	protected VarselInfoRepository varselInfoRepository;

	@Autowired
	protected FeilkvitteringRepository feilkvitteringRepository;

	@Autowired
	protected FilinfoRepository filinfoRepository;

	@Autowired
	protected LandkodePostDestRepository landkodePostDestRepository;

	@Autowired
	protected EntityManager entityManager;

	@BeforeEach
	public void setUp() {
		if (MDC.get(USER_ID) == null) {
			MDC.put(USER_ID, "ITest");
		}
		emptyDatabases();
	}

	protected void emptyDatabases() {
		varselInfoRepository.deleteAll();
		landkodePostDestRepository.deleteAll();
		feilkvitteringRepository.deleteAll();
		filinfoRepository.deleteAll();
		dokumentInfoRepository.deleteAll();
		dokumentDistribusjonRepository.deleteAll();
	}

	protected void commitAndBeginNewTransaction() {
		TestTransaction.flagForCommit();
		TestTransaction.end();
		TestTransaction.start();
	}

}
