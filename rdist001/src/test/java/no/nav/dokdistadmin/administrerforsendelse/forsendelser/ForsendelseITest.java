package no.nav.dokdistadmin.administrerforsendelse.forsendelser;

import no.nav.dokdistadmin.administrerforsendelse.Forsendelse;
import no.nav.dokdistadmin.config.AbstractOauth2Test;
import no.nav.dokdistadmin.config.ApplicationTestConfig;
import no.nav.dokdistadmin.config.DatabaseTest;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import no.nav.dokdistadmin.utils.TestDatabaseCleanup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfoWithDokumentId;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createOpprettForsendelseRequest;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Transactional
@SpringBootTest(
		classes = {ApplicationTestConfig.class},
		webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureTestDatabase
@ActiveProfiles({"itest"})
public class ForsendelseITest extends AbstractOauth2Test implements DatabaseTest {

	private static final String OPPRETT_FORSENDELSE_URI = "/rest/v1/administrerforsendelse";

	@Autowired
	public WebTestClient webTestClient;

	@Autowired
	protected DokumentInfoRepository dokumentInfoRepository;

	@Autowired
	protected DokumentDistribusjonRepository dokumentDistribusjonRepository;

	@Autowired
	private TestDatabaseCleanup testDatabaseCleanup;

	@BeforeAll
	static void setupAll() {
		if (MDC.get(USER_ID) == null) {
			MDC.put(USER_ID, "ForsendelseITest");
		}
	}

	@BeforeEach
	void setup() {
		testDatabaseCleanup.execute();
	}

	@AfterEach
	void cleanUp() {
		testDatabaseCleanup.execute();
	}

	@Test
	void skalOppretteForsendelse() {
		var request = createOpprettForsendelseRequest();

		var response = webTestClient.post()
				.uri(OPPRETT_FORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Forsendelse.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getForsendelseId()).isNotNull();

		var opprettetForsendelse = dokumentInfoRepository.findDokumentInfoByDokumentInfoId(response.getForsendelseId());

		assertThat(opprettetForsendelse)
				.extracting(DokumentInfo::getDokumentId)
				.isEqualTo(request.getBestillingsId());
	}

	@Test
	void skalReturnereEksisterendeForsendelseHvisDenEksisterer() {
		var request = createOpprettForsendelseRequest();

		var distribusjon = createDistribusjonInfo();
		distribusjon.addDokumentInfo(createDokumentInfoWithDokumentId(request.getBestillingsId()));
		dokumentDistribusjonRepository.save(distribusjon);

		commitAndBeginNewTransaction();

		var forsendelseSomEksisterer = dokumentInfoRepository.findDokumentInfoByDokumentId(request.getBestillingsId());

		var response = webTestClient.post()
				.uri(OPPRETT_FORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Forsendelse.class)
				.returnResult()
				.getResponseBody();

		assertThat(response)
				.extracting(Forsendelse::getForsendelseId)
				.isEqualTo(forsendelseSomEksisterer.getDokumentInfoId());
	}

	@Test
	void skalReturnereBadRequestGittUgyldigInput() {
		var request = createOpprettForsendelseRequest();
		request.setBestillingsId("");

		var response = webTestClient.post()
				.uri(OPPRETT_FORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).contains("bestillingsId må ha en verdi");
	}
}
