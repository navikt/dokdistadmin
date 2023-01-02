package no.nav.dokdistadmin.administrerforsendelse;

import no.nav.dokdistadmin.administrerforsendelse.AvstemEkspederteForsendelserRequest.Forsendelse;
import no.nav.dokdistadmin.config.ApplicationTestConfig;
import no.nav.dokdistadmin.domain.ArkivSystemCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback;
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.createDistribusjonInfoWithoutDokumentInfo;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.createDokumentInfo;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.GET;

@Transactional
@SpringBootTest(
		classes = {ApplicationTestConfig.class},
		webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@EnableMockOAuth2Server
@AutoConfigureTestDatabase
@ActiveProfiles({"itest"})
public class Rdist001ITest {

	@Autowired
	public WebTestClient webTestClient;

	@Autowired
	public MockOAuth2Server mockOAuth2Server;

	@Autowired
	protected DokumentInfoRepository dokumentInfoRepository;

	@Autowired
	protected DokumentDistribusjonRepository dokumentDistribusjonRepository;

	@BeforeEach
	void setup() {
		if (MDC.get(USER_ID) == null) {
			MDC.put(USER_ID, "Rdist001ITest");
		}

		dokumentInfoRepository.deleteAll();
		dokumentDistribusjonRepository.deleteAll();
	}

	private void setupDatabase() {
		var distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfoWithoutDokumentInfo());

		DokumentInfo dokumentInfo1 = getDokumentInfo();
		DokumentInfo dokumentInfo2 = getDokumentInfo();
		DokumentInfo dokumentInfo3 = getDokumentInfo();
		distribusjonInfo.addDokumentInfo(dokumentInfo1);
		distribusjonInfo.addDokumentInfo(dokumentInfo2);
		distribusjonInfo.addDokumentInfo(dokumentInfo3);
		dokumentDistribusjonRepository.save(distribusjonInfo);

		commitAndBeginNewTransaction();
	}

	private static DokumentInfo getDokumentInfo() {
		var dokumentInfo = createDokumentInfo();
		dokumentInfo.setDokumentStatus(DokumentStatusCode.EKSPEDERT);
		dokumentInfo.setArkivSystem(ArkivSystemCode.JOARK);
		dokumentInfo.setEkspedertDato(LocalDateTime.now());
		return dokumentInfo;
	}

	@ParameterizedTest
	@MethodSource
	void skalHenteEkspederteForsendelser(int maksForsendelser, int forventetAntallForsendelser) {
		setupDatabase();

		var response = webTestClient.method(GET)
				.uri("/rest/v1/administrerforsendelse/hentekspederteforsendelser")
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(new HentEkspederteForsendelserRequest(maksForsendelser))
				.exchange()
				.expectStatus().isOk()
				.returnResult(HentEkspederteForsendelserResponse.class)
				.getResponseBody()
				.blockFirst();

		assertEquals(forventetAntallForsendelser, response.forsendelser().size());
	}

	// Gitt N treff i databasen
	// (x, y) der x = maksForsendelser, y = forventetAntallForsendelser
	private static Stream<Arguments> skalHenteEkspederteForsendelser() {
		return Stream.of(
				Arguments.of(0, 3), // maksForsendelser lik 0 -> returner alle treff i db
				Arguments.of(2, 2), // maksForsendelser < N -> returner maksForsendelser fra db
				Arguments.of(4, 3) // maksForsendelser > N -> returner alle treff i db
		);
	}

	@Test
	void skalGiNoContentDersomIngenTreffVedHentingAvEkspederteForsendelser() {
		var distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfoWithoutDokumentInfo());
		distribusjonInfo.addDokumentInfo(createDokumentInfo());
		dokumentDistribusjonRepository.save(distribusjonInfo);
		commitAndBeginNewTransaction();

		webTestClient.method(GET)
				.uri("/rest/v1/administrerforsendelse/hentekspederteforsendelser")
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(new HentEkspederteForsendelserRequest(2))
				.exchange()
				.expectStatus().isNoContent();
	}

	@Test
	void skalGiBadRequestDersomMaksForsendelserErUgyldig() {
		setupDatabase();

		webTestClient.method(GET)
				.uri("/rest/v1/administrerforsendelse/hentekspederteforsendelser")
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(new HentEkspederteForsendelserRequest(-1))
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void skalAvstemmeEkspederteForsendelser() {
		setupDatabase();

		var forsendelse1 = new Forsendelse(1L);
		var forsendelse2 = new Forsendelse(2L);
		var request = new AvstemEkspederteForsendelserRequest(List.of(forsendelse1, forsendelse2));

		webTestClient.put()
				.uri("/rest/v1/administrerforsendelse/avstemekspederteforsendelser")
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk();
	}

	@ParameterizedTest
	@MethodSource
	void skalGiBadRequestDersomAvstemmeEkspederteForsendelserErUgyldig(List<Forsendelse> forsendelser) {
		setupDatabase();

		var request = new AvstemEkspederteForsendelserRequest(forsendelser);

		webTestClient.put()
				.uri("/rest/v1/administrerforsendelse/avstemekspederteforsendelser")
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isBadRequest();
	}

	private static Stream<Arguments> skalGiBadRequestDersomAvstemmeEkspederteForsendelserErUgyldig() {
		return Stream.of(
				Arguments.of(Collections.emptyList()),
				Arguments.of(List.of(new Forsendelse(0L)))
		);
	}

	public void commitAndBeginNewTransaction() {
		TestTransaction.flagForCommit();
		TestTransaction.end();
		TestTransaction.start();
	}

	private String jwt() {
		return jwt("azurev2");
	}

	private String jwt(String issuer) {
		String audience = "gosys";
		return mockOAuth2Server.issueToken(
				issuer,
				"gosys-clientid",
				new DefaultOAuth2TokenCallback(
						issuer,
						"subject",
						"JWT",
						List.of(audience),
						new HashMap<>(),
						60
				)
		).serialize();
	}
}