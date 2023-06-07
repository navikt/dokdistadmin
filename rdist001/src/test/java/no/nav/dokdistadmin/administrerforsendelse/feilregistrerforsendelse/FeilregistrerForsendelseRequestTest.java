package no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse;

import no.nav.dokdistadmin.domain.FeilTypeCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.Validation;
import javax.validation.Validator;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FeilregistrerForsendelseRequestTest {

	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void skalValidereFeilregistrerForsendelseRequest() {
		FeilregistrerForsendelseRequest request = new FeilregistrerForsendelseRequest();
		request.setForsendelseId(1L);
		request.setFeilTypeCode(FeilTypeCode.MELDINGSFEIL);
		request.setTidspunkt(LocalDateTime.now());
		request.setDetaljer("Detaljer");
		request.setPart("Part");
		request.setResendingDistribusjonId("ResendingDistribusjonId");

		var violations = validator.validate(request);

		assertThat(violations).isEmpty();
	}

	@ParameterizedTest
	@ValueSource(longs = {0L, -1L})
	void skalFeilvalidereUgyldigForsendelseId(Long forsendelseId) {
		FeilregistrerForsendelseRequest request = new FeilregistrerForsendelseRequest();
		request.setForsendelseId(forsendelseId);

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
		FeilregistrerForsendelseRequest request = new FeilregistrerForsendelseRequest();
		request.setFeilTypeCode(null);

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
		FeilregistrerForsendelseRequest request = new FeilregistrerForsendelseRequest();
		request.setTidspunkt(null);

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
		FeilregistrerForsendelseRequest request = new FeilregistrerForsendelseRequest();
		request.setDetaljer(detaljer);

		var violations = validator.validateProperty(request, "detaljer");

		assertThat(violations)
				.singleElement()
				.satisfies(it -> {
					assertThat(it.getMessage()).isEqualTo("detaljer må ha en verdi");
					assertThat(it.getPropertyPath().toString()).isEqualTo("detaljer");
				});
	}

}

