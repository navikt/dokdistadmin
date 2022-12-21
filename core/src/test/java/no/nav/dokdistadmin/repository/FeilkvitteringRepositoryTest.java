package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.config.AbstractRepositoryTest;
import no.nav.dokdistadmin.domain.FeilTypeCode;
import no.nav.dokdistadmin.domain.Feilkvittering;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FeilkvitteringRepositoryTest extends AbstractRepositoryTest {

	@Test
	void shouldSaveFeilkvittering() {
		var feilkvittering = createFeilkvittering();

		feilkvitteringRepository.save(feilkvittering);

		assertNotNull(feilkvittering.getFeilkvitteringId());
	}

	private Feilkvittering createFeilkvittering() {
		return Feilkvittering.builder()
				.feiltype(FeilTypeCode.MELDINGSFEIL)
				.detaljer("En feil har skjedd")
				.feiletTidspunkt(LocalDateTime.now())
				.build();
	}
}