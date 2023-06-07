package no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse;

import no.nav.dokdistadmin.config.AbstractOauth2Test;
import no.nav.dokdistadmin.config.ApplicationTestConfig;
import no.nav.dokdistadmin.config.DatabaseTest;
import no.nav.dokdistadmin.exception.RestResponseExceptionHandler.ErrorResponseBody;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import no.nav.dokdistadmin.repository.FeilkvitteringRepository;
import no.nav.dokdistadmin.repository.VarselInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createFeilregistrerForsendelseRequestWithForsendelseId;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Transactional
@SpringBootTest(
		classes = {ApplicationTestConfig.class},
		webEnvironment = RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles({"itest"})
public class FeilregistrerForsendelseITest extends AbstractOauth2Test implements DatabaseTest {

	private static final String FEILREGISTRERFORSENDELSE_URI = "/rest/v1/administrerforsendelse/feilregistrerforsendelse";

	@Autowired
	public WebTestClient webTestClient;

	@Autowired
	protected DokumentInfoRepository dokumentInfoRepository;

	@Autowired
	protected DokumentDistribusjonRepository dokumentDistribusjonRepository;

	@Autowired
	protected VarselInfoRepository varselInfoRepository;

	@Autowired
	FeilkvitteringRepository feilkvitteringRepository;

	@BeforeEach
	void setup() {
		if (MDC.get(USER_ID) == null) {
			MDC.put(USER_ID, "FeilregistrerITest");
		}

		varselInfoRepository.deleteAll();
		feilkvitteringRepository.deleteAll();
		dokumentInfoRepository.deleteAll();
		dokumentDistribusjonRepository.deleteAll();
	}

	private void setupDatabase() {
		var distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfo());
		distribusjonInfo.addDokumentInfo(createDokumentInfo());
		dokumentDistribusjonRepository.save(distribusjonInfo);
		commitAndBeginNewTransaction();
	}

	@Test
	void skalFeilregistrereForsendelse() {
		setupDatabase();

		var forsendelseId = dokumentInfoRepository.findAll().iterator().next().getDokumentInfoId();

		webTestClient.put()
				.uri(FEILREGISTRERFORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(createFeilregistrerForsendelseRequestWithForsendelseId(forsendelseId))
				.exchange()
				.expectStatus()
				.isOk();
	}

	@Test
	void skalReturnereNotFoundDersomDistribusjonenIkkeFinnes() {
		setupDatabase();

		var response = webTestClient.put()
				.uri(FEILREGISTRERFORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(createFeilregistrerForsendelseRequestWithForsendelseId(4L))
				.exchange()
				.expectStatus()
				.isNotFound()
				.returnResult(ErrorResponseBody.class)
				.getResponseBody()
				.blockFirst();

		assertThat(response)
				.satisfies(errorResponse -> assertThat(errorResponse.message())
						.contains("Feilmelding=Fant ikke distribusjon tilhørende forsendelse"));
	}

	@Test
	void skalReturnereBadRequestVedFeilregistrerForsendelseValideringsfeil() {
		var distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfo());
		distribusjonInfo.setResendingDistribusjonId("1");
		distribusjonInfo.addDokumentInfo(createDokumentInfo());
		dokumentDistribusjonRepository.save(distribusjonInfo);
		commitAndBeginNewTransaction();

		var forsendelseId = dokumentInfoRepository.findAll().iterator().next().getDokumentInfoId();

		var response = webTestClient.put()
				.uri(FEILREGISTRERFORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(createFeilregistrerForsendelseRequestWithForsendelseId(forsendelseId))
				.exchange()
				.expectStatus()
				.isBadRequest()
				.returnResult(ErrorResponseBody.class)
				.getResponseBody()
				.blockFirst();

		assertThat(response)
				.satisfies(errorResponse -> assertThat(errorResponse.message())
						.contains("Feltet resendingDistribusjonId på forsendelsen du prøver å feilregistrere kan ikke ha en verdi, men har verdien=1"));
	}

}
