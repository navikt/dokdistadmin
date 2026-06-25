package no.nav.dokdistadmin.administrerforsendelse.filinfo;

import no.nav.dokdistadmin.domain.FilStatusCode;
import no.nav.dokdistadmin.exception.functional.UgyldigInputException;
import org.junit.jupiter.api.Test;

import static no.nav.dokdistadmin.domain.FilStatusCode.OK;
import static no.nav.dokdistadmin.domain.FilStatusCode.OPPRETTET;
import static no.nav.dokdistadmin.domain.FilTypeCode.BEST_INFO_PRINT;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FilInfoValidatorTest {

	private static final String KILDE = "test-kilde";
	private static final String FILNAVN = "test-fil";

	@Test
	void shouldValidateWhenFilInfoIdIsNullOgFilnavnOgTypeAreSett() {
		FilInfoRequest validCreateRequest = FilInfoRequest.builder()
				.filnavn(FILNAVN)
				.filtype(BEST_INFO_PRINT.name())
				.status(OPPRETTET.name())
				.build();

		assertThatCode(() -> FilInfoValidator.validerFilInfoRequest(validCreateRequest))
				.doesNotThrowAnyException();
	}

	@Test
	void shouldValidateWhenFilInfoIdIsSettOgFilnavnOgTypeAreNull() {
		FilInfoRequest validUpdateRequest = FilInfoRequest.builder()
				.filInfoId(1L)
				.status(OK.name())
				.kilde(KILDE)
				.build();

		assertThatCode(() -> FilInfoValidator.validerFilInfoRequest(validUpdateRequest))
				.doesNotThrowAnyException();
	}

	@Test
	void shouldThrowExceptionWhenFilnavnOgTypeOgFilInfoIdAreSett() {
		FilInfoRequest invalidRequest = FilInfoRequest.builder()
				.filInfoId(1L)
				.filnavn(FILNAVN)
				.filtype(BEST_INFO_PRINT.name())
				.status(OK.name())
				.build();

		assertThatThrownBy(() -> FilInfoValidator.validerFilInfoRequest(invalidRequest))
				.isInstanceOf(UgyldigInputException.class)
				.hasMessage("filnavn og filtype kan ikke oppgis når filInfoId er satt");
	}

	@Test
	void shouldThrowExceptionWhenOnlyFilnavnIsSettWithFilInfoId() {
		FilInfoRequest invalidRequest = FilInfoRequest.builder()
				.filInfoId(1L)
				.filnavn("skal-ikke-settes")
				.status(OK.name())
				.build();

		assertThatThrownBy(() -> FilInfoValidator.validerFilInfoRequest(invalidRequest))
				.isInstanceOf(UgyldigInputException.class)
				.hasMessage("filnavn og filtype kan ikke oppgis når filInfoId er satt");
	}

	@Test
	void shouldThrowExceptionWhenFilnavnOgTypeOgFilInfoIdAreNull() {
		FilInfoRequest invalidRequest = FilInfoRequest.builder()
				.status(OPPRETTET.name())
				.build();

		assertThatThrownBy(() -> FilInfoValidator.validerFilInfoRequest(invalidRequest))
				.isInstanceOf(UgyldigInputException.class)
				.hasMessage("filnavn og filtype kan ikke være null eller tom");
	}

	@Test
	void shouldValidateWhenOnlyFilnavnIsSett() {
		FilInfoRequest validRequest = FilInfoRequest.builder()
				.filnavn(FILNAVN)
				.status(OPPRETTET.name())
				.build();

		assertThatCode(() -> FilInfoValidator.validerFilInfoRequest(validRequest))
				.doesNotThrowAnyException();
	}

	@Test
	void skalValidereNarKunTypeErSattVedCreate() {
		FilInfoRequest validRequest = FilInfoRequest.builder()
				.filtype(BEST_INFO_PRINT.name())
				.status(OPPRETTET.name())
				.build();

		assertThatCode(() -> FilInfoValidator.validerFilInfoRequest(validRequest))
				.doesNotThrowAnyException();
	}

	@Test
	void skalKasteExceptionNarFilnavnErTomStringVedCreate() {
		FilInfoRequest validRequest = FilInfoRequest.builder()
				.filnavn("")
				.filtype(BEST_INFO_PRINT.name())
				.status(OPPRETTET.name())
				.build();

		assertThatCode(() -> FilInfoValidator.validerFilInfoRequest(validRequest))
				.doesNotThrowAnyException();
	}

	@Test
	void shouldThrowExceptionWhenTypeErTomString() {
		FilInfoRequest validRequest = FilInfoRequest.builder()
				.filnavn(FILNAVN)
				.filtype("")
				.status(OPPRETTET.name())
				.build();

		assertThatCode(() -> FilInfoValidator.validerFilInfoRequest(validRequest))
				.doesNotThrowAnyException();
	}

	@Test
	void shouldThrowExceptionWhenFilTypeIsInvalid() {
		FilInfoRequest invalidRequest = FilInfoRequest.builder()
				.filnavn("test-fil")
				.filtype("INVALID_TYPE")
				.status(OPPRETTET.name())
				.build();

		assertThatThrownBy(() -> FilInfoValidator.validerFilInfoRequest(invalidRequest))
				.isInstanceOf(UgyldigInputException.class)
				.hasMessage("Ugyldig input: INVALID_TYPE er ikke en gyldig kodeverdi for FilTypeCode");
	}

	@Test
	void shoudThrowExceptionWhenFilStatusIsInvalid() {
		FilInfoRequest invalidRequest = FilInfoRequest.builder()
				.filnavn(FILNAVN)
				.filtype(BEST_INFO_PRINT.name())
				.status("INVALID_STATUS")
				.build();

		assertThatThrownBy(() -> FilInfoValidator.validerFilInfoRequest(invalidRequest))
				.isInstanceOf(UgyldigInputException.class)
				.hasMessage("Ugyldig input: INVALID_STATUS er ikke en gyldig kodeverdi for FilStatusCode");
	}

	@Test
	void shouldValideteAllValidFilStatuses() {
		for (FilStatusCode status : FilStatusCode.values()) {
			FilInfoRequest request = FilInfoRequest.builder()
					.filInfoId(1L)
					.status(status.name())
					.kilde(KILDE)
					.build();

			assertThatCode(() -> FilInfoValidator.validerFilInfoRequest(request))
					.doesNotThrowAnyException();
		}
	}
}
