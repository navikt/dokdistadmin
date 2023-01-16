package no.nav.dokdistadmin.administrerforsendelse;

import no.nav.dokdistadmin.administrerforsendelse.AvstemEkspederteForsendelserRequest.Forsendelse;
import no.nav.dokdistadmin.config.AbstractOauth2Test;
import no.nav.dokdistadmin.config.ApplicationTestConfig;
import no.nav.dokdistadmin.domain.ArkivSystemCode;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.VarselInfo;
import no.nav.dokdistadmin.domain.VarslingKanalCode;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import no.nav.dokdistadmin.repository.VarselInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.createDistribusjonInfoWithoutDokumentInfo;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.createDokumentInfo;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.apache.commons.lang3.StringUtils.truncate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Transactional
@SpringBootTest(
		classes = {ApplicationTestConfig.class},
		webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureTestDatabase
@ActiveProfiles({"itest"})
public class Rdist001ITest extends AbstractOauth2Test {

	private static AtomicInteger EKSPEDERT_COUNTER = new AtomicInteger(0);

	@Autowired
	public WebTestClient webTestClient;

	@Autowired
	protected DokumentInfoRepository dokumentInfoRepository;

	@Autowired
	protected DokumentDistribusjonRepository dokumentDistribusjonRepository;

	@Autowired
	protected VarselInfoRepository varselInfoRepository;


	@BeforeEach
	void setup() {
		if (MDC.get(USER_ID) == null) {
			MDC.put(USER_ID, "Rdist001ITest");
		}

		varselInfoRepository.deleteAll();
		dokumentInfoRepository.deleteAll();
		dokumentDistribusjonRepository.deleteAll();
	}

	private DistribusjonInfo setupDatabase() {
		var distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfoWithoutDokumentInfo());

		distribusjonInfo.addDokumentInfo(getValidEkspedertDokumentInfo());
		distribusjonInfo.addDokumentInfo(getValidEkspedertDokumentInfo());
		distribusjonInfo.addDokumentInfo(getValidEkspedertDokumentInfo());
		dokumentDistribusjonRepository.save(distribusjonInfo);

		commitAndBeginNewTransaction();

		return distribusjonInfo;
	}

	private static DokumentInfo getValidEkspedertDokumentInfo() {
		var dokumentInfo = createDokumentInfo();
		dokumentInfo.setDokumentStatus(DokumentStatusCode.EKSPEDERT);
		dokumentInfo.setArkivSystem(ArkivSystemCode.JOARK);
		dokumentInfo.setEkspedertDato(LocalDateTime.now().minusSeconds(EKSPEDERT_COUNTER.getAndIncrement()));
		dokumentInfo.addVarselInfo(VarselInfo.builder()
				.epostAdresse("navn.navnesen@nav.no")
				.varslingKanal(VarslingKanalCode.EPOST)
				.varslingstekst("Varsel til deg")
				.build());
		dokumentInfo.addVarselInfo(VarselInfo.builder()
				.mobiltelefonNummer("99999999")
				.varslingKanal(VarslingKanalCode.MOBILTELEFON)
				.varslingstekst("Varsel til deg")
				.build());
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

		assertNotNull(response);
		assertEquals(forventetAntallForsendelser, response.forsendelser().size());
		assertThat(response.forsendelser())
				.extracting(EkspederteForsendelse::getForsendelseId)
				.doesNotHaveDuplicates();
	}

	// Gitt N treff i databasen (forsendelser som oppfyller kravene)
	// (x, y) der x = maksForsendelser, y = forventetAntallForsendelser
	private static Stream<Arguments> skalHenteEkspederteForsendelser() {
		return Stream.of(
				Arguments.of(0, 3), // maksForsendelser lik 0 -> returner MAX_FORSENDELSER = 10000 i db
				Arguments.of(2, 2), // maksForsendelser < N -> returner maksForsendelser fra db
				Arguments.of(4, 3) // maksForsendelser > N -> returner N treff i db
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

	@ParameterizedTest
	@CsvSource({
			"{\"ukjentFelt\": \"2\"}, maksForsendelser kan ikke være 'null'",
			"{\"maksForsendelser\": null}, maksForsendelser kan ikke være 'null'",
			"{\"maksForsendelser\": -1}, maksForsendelser må være et positivt tall"
	})
	void skalGiBadRequestDersomMaksForsendelserErUgyldigEllerMangler(String jsonRequest, String expectedResponse) {
		setupDatabase();

		var response = webTestClient.method(GET)
				.uri("/rest/v1/administrerforsendelse/hentekspederteforsendelser")
				.headers(headers -> headers.setBearerAuth(jwt()))
				.contentType(APPLICATION_JSON)
				.bodyValue(jsonRequest)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertEquals(expectedResponse, response);
	}


	@Test
	void skalAvstemmeEkspederteForsendelser() {
		var persistertDistribusjoninfo = setupDatabase();
		var dokumentinfoIdList = persistertDistribusjoninfo.getDokumentInfos().stream().map(DokumentInfo::getDokumentInfoId).toList();

		var request = new AvstemEkspederteForsendelserRequest(List.of(
				new Forsendelse(dokumentinfoIdList.get(0)),
				new Forsendelse(dokumentinfoIdList.get(1))
		));

		webTestClient.put()
				.uri("/rest/v1/administrerforsendelse/avstemekspederteforsendelser")
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk();

		assertNotNull(dokumentInfoRepository.findDokumentInfoByDokumentInfoId(dokumentinfoIdList.get(0)).getAvstemtArkivDato());
		assertNotNull(dokumentInfoRepository.findDokumentInfoByDokumentInfoId(dokumentinfoIdList.get(1)).getAvstemtArkivDato());
		assertNull(dokumentInfoRepository.findDokumentInfoByDokumentInfoId(dokumentinfoIdList.get(2)).getAvstemtArkivDato());
	}

	@Test
	void skalTrunkereUserIdOgSetteEndretAv() {
		var persistertDistribusjoninfo = setupDatabase();

		var dokumentinfoId = persistertDistribusjoninfo.getDokumentInfos().iterator().next().getDokumentInfoId();
		var request = new AvstemEkspederteForsendelserRequest(List.of(
				new Forsendelse(dokumentinfoId)
		));

		webTestClient.put()
				.uri("/rest/v1/administrerforsendelse/avstemekspederteforsendelser")
				.headers(headers -> headers.setBearerAuth(jwtWithoutAzpNameClaim()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk();

		assertEquals(truncate(OID, 20), dokumentInfoRepository.findDokumentInfoByDokumentInfoId(dokumentinfoId).getChangeStamp().getEndretAv());
	}

	@ParameterizedTest
	@MethodSource
	void skalGiBadRequestDersomAvstemEkspederteForsendelserErUgyldig(List<Forsendelse> forsendelser) {
		setupDatabase();

		var request = new AvstemEkspederteForsendelserRequest(forsendelser);

		webTestClient.put()
				.uri("/rest/v1/administrerforsendelse/avstemekspederteforsendelser")
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isBadRequest();
	}

	private static Stream<Arguments> skalGiBadRequestDersomAvstemEkspederteForsendelserErUgyldig() {
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

}