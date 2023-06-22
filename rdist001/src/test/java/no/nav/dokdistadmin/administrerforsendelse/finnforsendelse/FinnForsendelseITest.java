package no.nav.dokdistadmin.administrerforsendelse.finnforsendelse;

import no.nav.dokdistadmin.administrerforsendelse.Forsendelse;
import no.nav.dokdistadmin.config.AbstractITest;
import no.nav.dokdistadmin.domain.Oppslagsnoekkel;
import no.nav.dokdistadmin.utils.TestDatabaseCleanup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import static java.lang.String.format;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.BESTILLINGS_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfoWithDokumentId;
import static no.nav.dokdistadmin.domain.Oppslagsnoekkel.BESTILLINGSID;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CONFLICT;

public class FinnForsendelseITest extends AbstractITest {

	private static final String FINN_FORSENDELSE_URI = "/rest/v1/administrerforsendelse/finnforsendelse/%s/%s";

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

		var response = webTestClient.get()
				.uri(format(FINN_FORSENDELSE_URI, oppslagsnoekkel.name().toLowerCase(), "123"))
				.headers(headers -> headers.setBearerAuth(jwt()))
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

		var response = webTestClient.get()
				.uri(format(FINN_FORSENDELSE_URI, oppslagsnoekkel.name().toLowerCase(), "123"))
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isNotFound()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).contains(format("finnForsendelse fant ikke forsendelse med %s=%s",
				oppslagsnoekkel.value,
				"123"));
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

		var response = webTestClient.get()
				.uri(format(FINN_FORSENDELSE_URI, BESTILLINGSID.name().toLowerCase(), BESTILLINGS_ID))
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isEqualTo(CONFLICT)
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).contains(format("finnForsendelse fant flere enn en forsendelse med %s=%s",
				BESTILLINGSID.value,
				BESTILLINGS_ID));
	}

}