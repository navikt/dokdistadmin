package no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDateTime;

import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.DETALJER;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.PART;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.RESENDING_DISTRIBUSJON_ID;
import static no.nav.dokdistadmin.domain.FeilTypeCode.MELDINGSFEIL;
import static org.assertj.core.api.Assertions.assertThat;

class FeilregistrerForsendelseRequestTest {

	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void skalValidereFeilregistrerForsendelseRequest() {
		FeilregistrerForsendelseRequest request = FeilregistrerForsendelseRequest.builder()
				.forsendelseId(1L)
				.feilTypeCode(MELDINGSFEIL)
				.tidspunkt(LocalDateTime.now())
				.detaljer(DETALJER)
				.part(PART)
				.resendingDistribusjonId(RESENDING_DISTRIBUSJON_ID)
				.build();

		var violations = validator.validate(request);

		assertThat(violations).isEmpty();
	}

	@ParameterizedTest
	@ValueSource(longs = {0L, -1L})
	void skalFeilvalidereUgyldigForsendelseId(Long forsendelseId) {
		FeilregistrerForsendelseRequest request = FeilregistrerForsendelseRequest.builder()
				.forsendelseId(forsendelseId)
				.build();

		var violations = validator.validateProperty(request, "forsendelseId");

		assertThat(violations)
				.singleElement()
				.satisfies(it -> {
					assertThat(it.getMessage()).isEqualTo("forsendelseId må være et positivt tall");
					assertThat(it.getPropertyPath().toString()).isEqualTo("forsendelseId");
				});
	}

	@Test
	void skalFeilvalidereUgyldigFeiltype() {
		FeilregistrerForsendelseRequest request = FeilregistrerForsendelseRequest.builder()
				.feilTypeCode(null)
				.build();

		var violations = validator.validateProperty(request, "feilTypeCode");

		assertThat(violations)
				.singleElement()
				.satisfies(it -> {
					assertThat(it.getMessage()).isEqualTo("type kan ikke være null");
					assertThat(it.getPropertyPath().toString()).isEqualTo("feilTypeCode");
				});
	}

	@Test
	void skalFeilvalidereUgyldigTidspunkt() {
		FeilregistrerForsendelseRequest request = FeilregistrerForsendelseRequest.builder()
				.tidspunkt(null)
				.build();

		var violations = validator.validateProperty(request, "tidspunkt");

		assertThat(violations)
				.singleElement()
				.satisfies(it -> {
					assertThat(it.getMessage()).isEqualTo("tidspunkt kan ikke være null");
					assertThat(it.getPropertyPath().toString()).isEqualTo("tidspunkt");
				});
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " "})
	@NullSource
	void skalFeilvalidereUgyldigDetaljer(String detaljer) {
		FeilregistrerForsendelseRequest request = FeilregistrerForsendelseRequest.builder()
				.detaljer(detaljer)
				.build();

		var violations = validator.validateProperty(request, "detaljer");

		assertThat(violations)
				.singleElement()
				.satisfies(it -> {
					assertThat(it.getMessage()).isEqualTo("detaljer må ha en verdi");
					assertThat(it.getPropertyPath().toString()).isEqualTo("detaljer");
				});
	}

}

