package no.nav.dokdistadmin.administrerforsendelse.forsendelser;

import no.nav.dokdistadmin.administrerforsendelse.Forsendelse;
import no.nav.dokdistadmin.config.AbstractITest;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.utils.TestDatabaseCleanup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.BESTILLINGS_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfoWithDokumentId;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentReferanseWithRefererTilAndRekkefoelge;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createOpprettForsendelseRequest;
import static no.nav.dokdistadmin.domain.ForsendelseMetadataTypeCode.DPO_ARKIVMELDING;
import static no.nav.dokdistadmin.domain.RefererTilCode.HOVEDDOKUMENT;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class ForsendelseIT extends AbstractITest {

	private static final String ADMINISTRER_FORSENDELSE_URI = "/rest/v1/administrerforsendelse";

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
	void skalOppretteForsendelseMedForsendelseMetadataOgType() throws IOException {
		var jsonRequest = getFileFromResources("__files/forsendelsemetadata/opprett_forsendelse.json");

		var response = webTestClient.post()
				.uri(ADMINISTRER_FORSENDELSE_URI)
				.headers(headers -> {
					headers.setBearerAuth(jwt());
					headers.setContentType(APPLICATION_JSON);
				})
				.bodyValue(jsonRequest)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Forsendelse.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getForsendelseId()).isNotNull();

		var opprettetForsendelse = dokumentInfoRepository.findDokumentInfoByDokumentInfoId(response.getForsendelseId());

		var forventetXml = getFileFromResources("__files/forsendelsemetadata/forsendelsemetadata.xml");
		assertThat(opprettetForsendelse)
				.extracting(DokumentInfo::getForsendelseMetadata)
				.isEqualTo(forventetXml);
		assertThat(opprettetForsendelse.getForsendelseMetadataType()).isEqualTo(DPO_ARKIVMELDING);
	}

	@Test
	void skalReturnereEksisterendeForsendelseHvisDenEksisterer() {
		var request = createOpprettForsendelseRequest();

		var distribusjon = createDistribusjonInfo();
		distribusjon.addDokumentInfo(createDokumentInfoWithDokumentId(request.getBestillingsId()));
		dokumentDistribusjonRepository.persist(distribusjon);

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
		var request = createOpprettForsendelseRequest().toBuilder()
				.bestillingsId("")
				.bestillendeFagsystem("")
				.build();

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
		dokumentDistribusjonRepository.persist(distribusjon);

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
		dokumentDistribusjonRepository.persist(distribusjon);

		commitAndBeginNewTransaction();

		var eksisterendeForsendelse = dokumentInfoRepository.findDokumentInfoByDokumentId(BESTILLINGS_ID);

		webTestClient.get()
				.uri(format(ADMINISTRER_FORSENDELSE_URI + "/%s", eksisterendeForsendelse.getDokumentInfoId()))
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isNoContent();
	}

	private String getFileFromResources(String filename) throws IOException {
		try (InputStream is = requireNonNull(this.getClass().getResourceAsStream("/" + filename))) {
			return new String(is.readAllBytes(), UTF_8);
		}
	}
}