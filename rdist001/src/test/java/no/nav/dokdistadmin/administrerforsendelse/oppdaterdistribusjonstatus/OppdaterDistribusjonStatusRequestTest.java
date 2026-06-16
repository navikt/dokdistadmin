package no.nav.dokdistadmin.administrerforsendelse.oppdaterdistribusjonstatus;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import no.nav.dokdistadmin.administrerforsendelse.oppdaterdistribusjonstatus.OppdaterDistribusjonStatusRequest.OppdaterDistribusjonStatusRequestBuilder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class OppdaterDistribusjonStatusRequestTest {

	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	private static OppdaterDistribusjonStatusRequestBuilder request() {
		return OppdaterDistribusjonStatusRequest.builder()
				.distribusjonId("distribusjonId")
				.distribusjonstatus("distribusjonstatus")
				.dokumentstatus("dokumentstatus")
				.kilde("kilde");
	}

	@ParameterizedTest
	@MethodSource
	void skalValidere(OppdaterDistribusjonStatusRequestBuilder request, String feilmelding) {
		assertThat(validator.validate(request.build()))
				.singleElement()
				.extracting(ConstraintViolation::getMessage)
				.isEqualTo(feilmelding);
	}

	static Stream<Arguments> skalValidere() {
		return Stream.of(
				Arguments.of(request().distribusjonId(""), "distribusjonId må ha en verdi"),
				Arguments.of(request().distribusjonId(null), "distribusjonId må ha en verdi"),
				Arguments.of(request().distribusjonstatus(""), "distribusjonstatus må ha en verdi"),
				Arguments.of(request().distribusjonstatus(null), "distribusjonstatus må ha en verdi"),
				Arguments.of(request().dokumentstatus(""), "dokumentstatus må ha en verdi"),
				Arguments.of(request().dokumentstatus(null), "dokumentstatus må ha en verdi"),
				Arguments.of(request().kilde(""), "kilde må ha en verdi"),
				Arguments.of(request().kilde(null), "kilde må ha en verdi")
		);
	}

	@ParameterizedTest
	@MethodSource
	void skalTrunkereKilde(String input, String forventet) {
		assertThat(request().kilde(input).build().kilde()).isEqualTo(forventet);
	}

	static Stream<Arguments> skalTrunkereKilde() {
		return Stream.of(
				arguments("  kilde-med-whitespace  ", "kilde-med-whitespace"),
				arguments("kilde-med-mer-enn-20-tegn", "kilde-med-mer-enn-20"),
				arguments("     kilde-med-mer-enn-20-tegn-og-leading-whitespace", "kilde-med-mer-enn-20"),
				arguments(null, null),
				arguments("", ""),
				arguments("       ", "")
		);
	}
}
