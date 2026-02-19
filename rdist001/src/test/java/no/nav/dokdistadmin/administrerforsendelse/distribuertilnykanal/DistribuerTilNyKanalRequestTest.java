package no.nav.dokdistadmin.administrerforsendelse.distribuertilnykanal;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DistribuerTilNyKanalRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void skalValidere() {
        assertThat(validator.validate(gyldigRequest().build())).isEmpty();
    }

    @ParameterizedTest
    @MethodSource
    void skalKasteExceptionVedUgyldigInput(DistribuerTilNyKanalRequest.DistribuerTilNyKanalRequestBuilder requestBuilder, String expectedMessage) {

        assertThat(validator.validate(requestBuilder.build()))
                .singleElement()
                .extracting(ConstraintViolation::getMessage)
                .isEqualTo(expectedMessage);
    }

     private static Stream<Arguments> skalKasteExceptionVedUgyldigInput() {
        return Stream.of(
                Arguments.of(gyldigRequest().forsendelseId(0), "forsendelseId må være et positivt tall"),
                Arguments.of(gyldigRequest().kanal(""), "kanal må ha en verdi"),
                Arguments.of(gyldigRequest().arsak(""), "arsak må ha en verdi"),
                Arguments.of(gyldigRequest().arsakBeskrivelse(""), "arsakBeskrivelse må ha en verdi"),
                Arguments.of(gyldigRequest().arsakBeskrivelse("a".repeat(1001)), "arsakBeskrivelse kan ikke være lengre enn 1000 tegn"));
     }

    private static DistribuerTilNyKanalRequest.DistribuerTilNyKanalRequestBuilder gyldigRequest() {
        return DistribuerTilNyKanalRequest.builder()
                .forsendelseId(1)
                .kanal("PRINT")
                .arsak("VARSLINGSFEIL")
                .arsakBeskrivelse("En beskrivelse");
    }
}
