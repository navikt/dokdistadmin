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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.BESTILLINGS_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfoWithDokumentId;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentReferanseWithRefererTilAndRekkefoelge;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createOpprettForsendelseRequest;
import static no.nav.dokdistadmin.domain.RefererTilCode.HOVEDDOKUMENT;
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

	private static final String ADMINISTRER_FORSENDELSE_URI = "/rest/v1/administrerforsendelse";

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
				.uri(ADMINISTRER_FORSENDELSE_URI)
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
				.uri(ADMINISTRER_FORSENDELSE_URI)
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
		request.setBestillendeFagsystem("");

		var response = webTestClient.post()
				.uri(ADMINISTRER_FORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).contains("bestillingsId må ha en verdi", "bestillendeFagsystem må ha en verdi");
	}

	@Test
	void skalHenteForsendelse() {
		var distribusjon = createDistribusjonInfo();
		distribusjon.addDokumentInfo(createDokumentInfoWithDokumentId(BESTILLINGS_ID));
		dokumentDistribusjonRepository.save(distribusjon);

		commitAndBeginNewTransaction();

		var eksisterendeForsendelse = dokumentInfoRepository.findDokumentInfoByDokumentId(BESTILLINGS_ID);

		var response = webTestClient.get()
				.uri(format(ADMINISTRER_FORSENDELSE_URI + "/%s", eksisterendeForsendelse.getDokumentInfoId()))
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isOk()
				.expectBody(HentForsendelseResponse.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).isNotNull()
				.extracting(HentForsendelseResponse::getBestillingsId)
				.isEqualTo(eksisterendeForsendelse.getDokumentId());
	}

	@ParameterizedTest
	@ValueSource(ints = {-1, 0})
	void skalGiBadRequestForUgyldigHentForsendelseRequest(int forsendelseId) {

		var response = webTestClient.get()
				.uri(format(ADMINISTRER_FORSENDELSE_URI + "/%s", forsendelseId))
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).contains("forsendelseId må være et positivt tall");
	}

	@Test
	void skalGiNotFoundDersomForsendelsenIkkeLiggerIDatabasenVedHentingAvForsendelse() {
		var forsendelseId = 123L;

		var response = webTestClient.get()
				.uri(format(ADMINISTRER_FORSENDELSE_URI + "/%s", forsendelseId))
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isNotFound()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).contains(format("Forsendelse med forsendelseId=%s ikke funnet i dokdistDb", forsendelseId));
	}

	@Test
	void skalGiNoContentVedHentingAvForsendelseMedUgyldigRekkefoelge() {
		var distribusjon = createDistribusjonInfo();
		var dokument = createDokumentInfoWithDokumentId(BESTILLINGS_ID);
		var dokumentreferanse = createDokumentReferanseWithRefererTilAndRekkefoelge(HOVEDDOKUMENT, -1);
		dokument.addDokumentReferanse(dokumentreferanse);
		distribusjon.addDokumentInfo(dokument);
		dokumentDistribusjonRepository.save(distribusjon);

		commitAndBeginNewTransaction();

		var eksisterendeForsendelse = dokumentInfoRepository.findDokumentInfoByDokumentId(BESTILLINGS_ID);

		webTestClient.get()
				.uri(format(ADMINISTRER_FORSENDELSE_URI + "/%s", eksisterendeForsendelse.getDokumentInfoId()))
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isNoContent();
	}
}