package no.nav.dokdistadmin.domain;

import no.nav.dokdistadmin.exception.functional.UgyldigInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OppslagsnoekkelTest {

	@ParameterizedTest
	@EnumSource(value = Oppslagsnoekkel.class)
	void skalParseTilOppslagsnoekkel(Oppslagsnoekkel oppslagsnoekkel) {
		assertDoesNotThrow(() -> Oppslagsnoekkel.fromString(oppslagsnoekkel.name()));
	}

	@ParameterizedTest
	@ValueSource(strings = {"journalpostid", "journalpostId", "JOURNALPOSTID"})
	void skalParseTilOppslagsnoekkelUavhengigAvCase(String oppslagsnoekkel) {
		assertDoesNotThrow(() -> Oppslagsnoekkel.fromString(oppslagsnoekkel));
	}

	@Test
	void skalKasteExceptionForUgyldigOppslagsnoekkel() {
		assertThrows(UgyldigInputException.class, () -> Oppslagsnoekkel.fromString("UGYLDIG_OPPSLGSNOEKKEL"));
	}
}