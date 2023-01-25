package no.nav.dokdistadmin.domain;

import no.nav.dokdistadmin.exception.functional.UgyldigInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DistribusjonKanalCodeTest {


	@ParameterizedTest
	@EnumSource(value = DistribusjonKanalCode.class)
	void skalParseTilDistribusjonskanal(DistribusjonKanalCode distribusjonKanalCode) {
		assertDoesNotThrow(() -> DistribusjonKanalCode.fromString(distribusjonKanalCode.name()));
	}

	@ParameterizedTest
	@ValueSource(strings = {"trygderetten", "TRYGDEretten"})
	void skalParseTilDistribusjonskanalUavhengigAvCase(String distribusjonKanalCode) {
		assertDoesNotThrow(() -> DistribusjonKanalCode.fromString(distribusjonKanalCode));
	}

	@Test
	void skalKasteExceptionForUgyldigDistribusjonskanal() {
		assertThrows(UgyldigInputException.class, () -> DistribusjonKanalCode.fromString("UGYLDIG_DISTRIBUSJONSKANAL"));
	}
}