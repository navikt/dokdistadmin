package no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse;

import no.nav.dokdistadmin.config.AbstractITest;
import org.junit.jupiter.api.Test;

import static java.lang.String.format;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.RESENDING_DISTRIBUSJON_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createFeilregistrerForsendelseRequestWithForsendelseId;
import static org.assertj.core.api.Assertions.assertThat;

public class FeilregistrerForsendelseIT extends AbstractITest {

	private static final String FEILREGISTRERFORSENDELSE_URI = "/rest/v1/administrerforsendelse/feilregistrerforsendelse";

	@Test
	void skalFeilregistrereForsendelse() {
		var forsendelseId = setupDatabase();

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
		var distribusjonInfo = dokumentDistribusjonRepository.persist(createDistribusjonInfo());
		distribusjonInfo.setResendingDistribusjonId(RESENDING_DISTRIBUSJON_ID);
		distribusjonInfo.addDokumentInfo(createDokumentInfo());
		dokumentDistribusjonRepository.persist(distribusjonInfo);
		commitAndBeginNewTransaction();

		var forsendelseId = distribusjonInfo.getDokumentInfos().iterator().next().getDokumentInfoId();

		var response = webTestClient.put()
				.uri(FEILREGISTRERFORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(createFeilregistrerForsendelseRequestWithForsendelseId(forsendelseId))
				.exchange()
				.expectStatus()
				.isBadRequest()
				.returnResult(String.class)
				.getResponseBody()
				.blockFirst();

		assertThat(response)
				.satisfies(errorResponse -> assertThat(response)
						.contains(format("rdist001 kunne ikke feilregistrere forsendelse. Feilmelding=Feltet resendingDistribusjonId på forsendelsen du prøver å feilregistrere kan ikke ha en verdi, men har verdien=%s",
								RESENDING_DISTRIBUSJON_ID)));
	}

	private long setupDatabase() {
		var distribusjonInfo = dokumentDistribusjonRepository.persist(createDistribusjonInfo());
		distribusjonInfo.addDokumentInfo(createDokumentInfo());
		dokumentDistribusjonRepository.persist(distribusjonInfo);
		commitAndBeginNewTransaction();
		return distribusjonInfo.getDokumentInfos().iterator().next().getDokumentInfoId();
	}

}
