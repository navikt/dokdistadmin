package no.nav.dokdistadmin.administrerforsendelse.finnforsendelse;

import no.nav.dokdistadmin.administrerforsendelse.Forsendelse;
import no.nav.dokdistadmin.config.AbstractITest;
import no.nav.dokdistadmin.utils.TestDatabaseCleanup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static java.lang.String.format;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.BESTILLINGS_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfoWithDokumentId;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;

public class FinnForsendelseITest extends AbstractITest {

	private static final String FINN_FORSENDELSE_URI = "/rest/v1/administrerforsendelse/finnforsendelse";

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


	@ParameterizedTest
	@EnumSource(value = Oppslagsnoekkel.class)
	void skalFinneForsendelse(Oppslagsnoekkel oppslagsnoekkel) {
		var distribusjon = createDistribusjonInfo();
		var dokument = createDokumentInfo();
		dokument.setDokumentId("123");
		dokument.setKonversasjonId("123");
		dokument.setArkivkode("123");
		distribusjon.addDokumentInfo(dokument);
		dokumentDistribusjonRepository.save(distribusjon);

		commitAndBeginNewTransaction();

		var request = FinnForsendelseRequest.builder()
				.oppslagsnoekkel(oppslagsnoekkel)
				.verdi("123")
				.build();

		var response = webTestClient.method(HttpMethod.GET)
				.uri(FINN_FORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Forsendelse.class)
				.returnResult()
				.getResponseBody();

		var dokumentInfoId = dokumentInfoRepository.findAll().iterator().next().getDokumentInfoId();

		assertThat(response).isNotNull()
				.extracting(Forsendelse::getForsendelseId)
				.isEqualTo(dokumentInfoId);
	}

	@ParameterizedTest
	@EnumSource(value = Oppslagsnoekkel.class)
	void skalGiNotFoundDersomForsendelsenIkkeFinnes(Oppslagsnoekkel oppslagsnoekkel) {
		var request = FinnForsendelseRequest.builder()
				.oppslagsnoekkel(oppslagsnoekkel)
				.verdi("verdi")
				.build();

		var response = webTestClient.method(HttpMethod.GET)
				.uri(FINN_FORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).contains(format("Fant ikke forsendelse med %s=%s", request.getOppslagsnoekkel().getValue(), request.getVerdi()));
	}

	@Test
	void skalGiConflictDersomDetFinnesFlereForsendelserMedSammeVerdiForGittOppslagsnoekkel() {
		var distribusjon = createDistribusjonInfo();
		var dokument1 = createDokumentInfoWithDokumentId(BESTILLINGS_ID);
		var dokument2 = createDokumentInfoWithDokumentId(BESTILLINGS_ID);
		distribusjon.addDokumentInfo(dokument1);
		distribusjon.addDokumentInfo(dokument2);
		dokumentDistribusjonRepository.save(distribusjon);

		commitAndBeginNewTransaction();

		var request = FinnForsendelseRequest.builder()
				.oppslagsnoekkel(Oppslagsnoekkel.BESTILLINGSID)
				.verdi(BESTILLINGS_ID)
				.build();

		var response = webTestClient.method(HttpMethod.GET)
				.uri(FINN_FORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.CONFLICT)
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).contains(format("Fant flere enn en forsendelse med %s=%s", request.getOppslagsnoekkel().getValue(), request.getVerdi()));
	}

}