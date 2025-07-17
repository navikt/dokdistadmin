package no.nav.dokdistadmin.administrerforsendelse.forsendelser;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequest.ArkivInformasjon;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequest.Dokument;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequest.Mottaker;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequest.Postadresse;
import no.nav.dokdistadmin.domain.ForsendelseMetadataTypeCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createOpprettForsendelseRequest;
import static no.nav.dokdistadmin.domain.ArkivSystemCode.JOARK;
import static no.nav.dokdistadmin.domain.ForsendelseMetadataTypeCode.DPO_ARKIVMELDING;
import static no.nav.dokdistadmin.domain.MottakerIdTypeCode.PERSON;
import static no.nav.dokdistadmin.domain.RefererTilCode.HOVEDDOKUMENT;
import static org.assertj.core.api.Assertions.assertThat;

class OpprettForsendelseValidationTest {

	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void skalValidereGyldigOpprettForsendelseRequest() {
		OpprettForsendelseRequest opprettForsendelseRequest = createOpprettForsendelseRequest();

		var violations = validator.validate(opprettForsendelseRequest);

		assertThat(violations).isEmpty();
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " "})
	@NullSource
	void skalFeilvalidereUgyldigBestillingsId(String bestillingsId) {
		OpprettForsendelseRequest opprettForsendelseRequest = createOpprettForsendelseRequest().toBuilder()
				.bestillingsId(bestillingsId)
				.build();

		var violations = validator.validate(opprettForsendelseRequest);

		assertThat(violations)
				.hasSize(1)
				.allSatisfy(it -> {
					assertThat(it.getMessage()).isEqualTo("bestillingsId må ha en verdi");
					assertThat(it.getPropertyPath().toString()).isEqualTo("bestillingsId");
				});
	}

	@Test
	void skalFeilvalidereUgyldigDistribusjonsKanal() {
		OpprettForsendelseRequest opprettForsendelseRequest = createOpprettForsendelseRequest().toBuilder()
				.distribusjonsKanal(null)
				.build();

		var violations = validator.validate(opprettForsendelseRequest);

		assertThat(violations)
				.hasSize(1)
				.allSatisfy(it -> {
					assertThat(it.getMessage()).isEqualTo("distribusjonsKanal kan ikke være null");
					assertThat(it.getPropertyPath().toString()).isEqualTo("distribusjonsKanal");
				});
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " "})
	@NullSource
	void skalFeilvalidereUgyldigBestillendeFagsystem(String bestillendeFagsystem) {
		OpprettForsendelseRequest opprettForsendelseRequest = createOpprettForsendelseRequest().toBuilder()
				.bestillendeFagsystem(bestillendeFagsystem)
				.build();

		var violations = validator.validate(opprettForsendelseRequest);

		assertThat(violations)
				.hasSize(1)
				.allSatisfy(it -> {
					assertThat(it.getMessage()).isEqualTo("bestillendeFagsystem må ha en verdi");
					assertThat(it.getPropertyPath().toString()).isEqualTo("bestillendeFagsystem");
				});
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " "})
	@NullSource
	void skalFeilvalidereUgyldigTema(String tema) {
		OpprettForsendelseRequest opprettForsendelseRequest = createOpprettForsendelseRequest().toBuilder()
				.tema(tema)
				.build();

		var violations = validator.validate(opprettForsendelseRequest);

		assertThat(violations)
				.hasSize(1)
				.allSatisfy(it -> {
					assertThat(it.getMessage()).isEqualTo("tema må ha en verdi");
					assertThat(it.getPropertyPath().toString()).isEqualTo("tema");
				});
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " "})
	@NullSource
	void skalFeilvalidereUgyldigForsendelseTittel(String forsendelseTittel) {
		OpprettForsendelseRequest opprettForsendelseRequest = createOpprettForsendelseRequest().toBuilder()
				.forsendelseTittel(forsendelseTittel)
				.build();

		var violations = validator.validate(opprettForsendelseRequest);

		assertThat(violations)
				.hasSize(1)
				.allSatisfy(it -> {
					assertThat(it.getMessage()).isEqualTo("forsendelseTittel må ha en verdi");
					assertThat(it.getPropertyPath().toString()).isEqualTo("forsendelseTittel");
				});
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " "})
	@NullSource
	void skalFeilvalidereUgyldigDokumentProdApp(String dokumentProdApp) {
		OpprettForsendelseRequest opprettForsendelseRequest = createOpprettForsendelseRequest().toBuilder()
				.dokumentProdApp(dokumentProdApp)
				.build();

		var violations = validator.validate(opprettForsendelseRequest);

		assertThat(violations)
				.hasSize(1)
				.allSatisfy(it -> {
					assertThat(it.getMessage()).isEqualTo("dokumentProdApp må ha en verdi");
					assertThat(it.getPropertyPath().toString()).isEqualTo("dokumentProdApp");
				});
	}

	@ParameterizedTest
	@MethodSource
	void shouldMapForsendelseMetadataAndTypeWhenBothSetOrNull(byte[] forsendelseMetadata, ForsendelseMetadataTypeCode type) {
		var request = createOpprettForsendelseRequest()
				.toBuilder()
				.forsendelseMetadata(forsendelseMetadata)
				.forsendelseMetadataType(type)
				.build();

		var violations = validator.validate(request);

		assertThat(violations).isEmpty();
	}

	private static Stream<Arguments> shouldMapForsendelseMetadataAndTypeWhenBothSetOrNull() {
		return Stream.of(
				Arguments.of(null, null),
				Arguments.of("forsendelseMetadata".getBytes(), DPO_ARKIVMELDING)
		);
	}

	@ParameterizedTest
	@MethodSource
	void shouldThrowExceptionWhenOnlyForsendelseMetadataOrTypeIsSet(byte[] forsendelseMetadata, ForsendelseMetadataTypeCode forsendelseMetadataType) {
		var request = createOpprettForsendelseRequest()
				.toBuilder()
				.forsendelseMetadata(forsendelseMetadata)
				.forsendelseMetadataType(forsendelseMetadataType)
				.build();

		var violations = validator.validate(request);

		assertThat(violations).hasSize(1)
				.allSatisfy(it ->
						assertThat(it.getMessage()).contains("Forsendelsesmetadata og ForsendelsesmetadataType må enten begge være satt, eller begge være null."));
	}

	private static Stream<Arguments> shouldThrowExceptionWhenOnlyForsendelseMetadataOrTypeIsSet() {
		return Stream.of(
				Arguments.of("forsendelseMetadata".getBytes(), null),
				Arguments.of(null, DPO_ARKIVMELDING)
		);
	}

	@ParameterizedTest
	@MethodSource
	void skalFeilvalidereUgyldigMottaker(Mottaker mottaker, String feilmelding) {
		OpprettForsendelseRequest opprettForsendelseRequest = createOpprettForsendelseRequest().toBuilder()
				.mottaker(mottaker)
				.build();

		var violations = validator.validate(opprettForsendelseRequest);

		assertThat(violations)
				.hasSize(1)
				.allSatisfy(it -> assertThat(it.getMessage()).isEqualTo(feilmelding));
	}

	private static Stream<Arguments> skalFeilvalidereUgyldigMottaker() {
		return Stream.of(
				Arguments.of(null, "mottaker kan ikke være null"),
				Arguments.of(new Mottaker("", "mottakerNavn", PERSON), "mottakerId må ha en verdi"),
				Arguments.of(new Mottaker(" ", "mottakerNavn", PERSON), "mottakerId må ha en verdi"),
				Arguments.of(new Mottaker(null, "mottakerNavn", PERSON), "mottakerId må ha en verdi"),
				Arguments.of(new Mottaker("mottakerId", "", PERSON), "mottakerNavn må ha en verdi"),
				Arguments.of(new Mottaker("mottakerId", " ", PERSON), "mottakerNavn må ha en verdi"),
				Arguments.of(new Mottaker("mottakerId", null, PERSON), "mottakerNavn må ha en verdi"),
				Arguments.of(new Mottaker("mottakerId", "mottakerNavn", null), "mottakerType kan ikke være null")
		);
	}

	@ParameterizedTest
	@MethodSource
	void skalFeilvalidereUgyldigDokumenter(List<Dokument> dokumenter, String feilmelding) {
		OpprettForsendelseRequest opprettForsendelseRequest = createOpprettForsendelseRequest().toBuilder()
				.dokumenter(dokumenter)
				.build();

		var violations = validator.validate(opprettForsendelseRequest);

		assertThat(violations)
				.hasSize(1)
				.allSatisfy(it -> assertThat(it.getMessage()).isEqualTo(feilmelding));
	}

	private static Stream<Arguments> skalFeilvalidereUgyldigDokumenter() {
		return Stream.of(
				Arguments.of(null, "dokumenter kan ikke være null eller en tom liste"),
				Arguments.of(Collections.emptyList(), "dokumenter kan ikke være null eller en tom liste"),
				Arguments.of(List.of(new Dokument(null, "ref", 1, "info", "id")), "tilknyttetSom kan ikke være null"),
				Arguments.of(List.of(new Dokument(HOVEDDOKUMENT, "", 1, "info", "id")), "dokumentObjektReferanse må ha en verdi"),
				Arguments.of(List.of(new Dokument(HOVEDDOKUMENT, " ", 1, "info", "id")), "dokumentObjektReferanse må ha en verdi"),
				Arguments.of(List.of(new Dokument(HOVEDDOKUMENT, null, 1, "info", "id")), "dokumentObjektReferanse må ha en verdi"),
				Arguments.of(List.of(new Dokument(HOVEDDOKUMENT, "ref", -1, "info", "id")), "rekkefolge må være 0 eller et positivt tall"),
				Arguments.of(List.of(new Dokument(HOVEDDOKUMENT, "ref", null, "info", "id")), "rekkefolge kan ikke være null"),
				Arguments.of(List.of(new Dokument(HOVEDDOKUMENT, "ref", 1, "info", "")), "dokumenttypeId må ha en verdi"),
				Arguments.of(List.of(new Dokument(HOVEDDOKUMENT, "ref", 1, "info", " ")), "dokumenttypeId må ha en verdi"),
				Arguments.of(List.of(new Dokument(HOVEDDOKUMENT, "ref", 1, "info", null)), "dokumenttypeId må ha en verdi")
				);
	}

	@ParameterizedTest
	@MethodSource
	void skalFeilvalidereUgyldigArkivInformasjon(ArkivInformasjon arkivInformasjon, String feilmelding) {
		OpprettForsendelseRequest opprettForsendelseRequest = createOpprettForsendelseRequest().toBuilder()
				.arkivInformasjon(arkivInformasjon)
				.build();

		var violations = validator.validate(opprettForsendelseRequest);

		assertThat(violations)
				.hasSize(1)
				.allSatisfy(it -> assertThat(it.getMessage()).isEqualTo(feilmelding));
	}

	private static Stream<Arguments> skalFeilvalidereUgyldigArkivInformasjon() {
		return Stream.of(
				Arguments.of(new ArkivInformasjon(null, "arkivId"), "arkivSystem kan ikke være null"),
				Arguments.of(new ArkivInformasjon(JOARK, ""), "arkivId må ha en verdi"),
				Arguments.of(new ArkivInformasjon(JOARK, " "), "arkivId må ha en verdi"),
				Arguments.of(new ArkivInformasjon(JOARK, null), "arkivId må ha en verdi")
		);
	}

	@ParameterizedTest
	@MethodSource
	void skalFeilvalidereUgyldigPostadresse(Postadresse postadresse, String feilmelding) {
		OpprettForsendelseRequest opprettForsendelseRequest = createOpprettForsendelseRequest().toBuilder()
				.postadresse(postadresse)
				.build();

		var violations = validator.validate(opprettForsendelseRequest);

		assertThat(violations)
				.hasSize(1)
				.allSatisfy(it -> assertThat(it.getMessage()).isEqualTo(feilmelding));
	}

	private static Stream<Arguments> skalFeilvalidereUgyldigPostadresse() {
		return Stream.of(
				Arguments.of(new Postadresse("1", "2", "3", "0001", "Sted", null), "landkode må ha en verdi"),
				Arguments.of(new Postadresse("1", "2", "3", "0001", "Sted", ""), "landkode må ha en verdi"),
				Arguments.of(new Postadresse("1", "2", "3", "0001", "Sted", " "), "landkode må ha en verdi")
		);
	}

}
