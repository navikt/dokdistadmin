package no.nav.dokdistadmin.administrerforsendelse.distribuertilnykanal;

import no.nav.dokdistadmin.exception.functional.UgyldigInputException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class DistribuerTilNyKanalValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {"PRINT", "SDP", "DITTNAV", "DPVT"})
    void skalValidereGyldigeKanaler(String kanal) {
        assertThatCode(() -> DistribuerTilNyKanalValidator.validerKanal(kanal))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"UGYLDIG", "print", "sdp", "", "E_HANDEL", "TRYGDERETTEN"})
    void skalKasteExceptionForUgyldigeKanaler(String kanal) {
        assertThatExceptionOfType(UgyldigInputException.class)
                .isThrownBy(() -> DistribuerTilNyKanalValidator.validerKanal(kanal))
                .withMessageContaining("Ugyldig kanal '%s'", kanal);
    }

    @ParameterizedTest
    @ValueSource(strings = {"VARSLINGSFEIL", "MELDINGSFEIL"})
    void skalValidereGyldigeArsaker(String arsak) {
        assertThatCode(() -> DistribuerTilNyKanalValidator.validerArsak(arsak))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"UGYLDIG", "varslingsfeil", "", "ANNEN_ARSAK"})
    void skalKasteExceptionForUgyldigeArsaker(String arsak) {
        assertThatExceptionOfType(UgyldigInputException.class)
                .isThrownBy(() -> DistribuerTilNyKanalValidator.validerArsak(arsak))
                .withMessageContaining("Ugyldig årsak '%s'", arsak);
    }

    @ParameterizedTest
    @ValueSource(strings = {"OPPRETTET", "OVERSENDT", "BEKREFTET", "KLAR_FOR_DIST"})
    void skalValidereGyldigeStatuser(String status) {
        assertThatCode(() -> DistribuerTilNyKanalValidator.validerForsendelseStatus(status))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"EKSPEDERT", "RETURPOSTBEHANDLET", "FEILET"})
    void skalKasteExceptionForUgyldigeStatuser(String status) {
        assertThatExceptionOfType(UgyldigInputException.class)
                .isThrownBy(() -> DistribuerTilNyKanalValidator.validerForsendelseStatus(status))
                .withMessageContaining("Forsendelsen har status '%s' og kan ikke distribueres til ny kanal", status);
    }
}

