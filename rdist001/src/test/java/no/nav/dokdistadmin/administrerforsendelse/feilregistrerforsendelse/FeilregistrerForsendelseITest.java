package no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse;

import no.nav.dokdistadmin.config.AbstractITest;
import no.nav.dokdistadmin.exception.RestResponseExceptionHandler.ErrorResponseBody;
import org.junit.jupiter.api.Test;

import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createFeilregistrerForsendelseRequestWithForsendelseId;
import static org.assertj.core.api.Assertions.assertThat;

public class FeilregistrerForsendelseITest extends AbstractITest {

	private static final String FEILREGISTRERFORSENDELSE_URI = "/rest/v1/administrerforsendelse/feilregistrerforsendelse";

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
