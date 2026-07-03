package no.nav.dokdistadmin.administrerforsendelse.filinfo;

import no.nav.dokdistadmin.domain.FilInfo;
import no.nav.dokdistadmin.domain.FilStatusCode;
import no.nav.dokdistadmin.exception.functional.UgyldigInputException;
import org.junit.jupiter.api.Test;

import static no.nav.dokdistadmin.domain.FilStatusCode.OK;
import static no.nav.dokdistadmin.domain.FilStatusCode.OPPRETTET;
import static no.nav.dokdistadmin.domain.FilTypeCode.AVSTEMMING_JOARK;
import static no.nav.dokdistadmin.domain.FilTypeCode.BEST_INFO_PRINT;
import static no.nav.dokdistadmin.domain.FilTypeCode.DOK_RAPP_PRINT;
import static no.nav.dokdistadmin.domain.FilTypeCode.PRINTFIL;
import static no.nav.dokdistadmin.domain.KildeTypeCode.SITS;
import static no.nav.dokdistadmin.domain.KommunikasjonRetningCode.INNGAENDE;
import static no.nav.dokdistadmin.domain.KommunikasjonRetningCode.UTGAENDE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FilInfoMapperTest {

	private static final String FILNAVN = "test-filnavn";

	@Test
	void shouldMapFilInfoForInngaendeFiltype() {
		FilInfoRequest request = FilInfoRequest.builder()
				.filnavn(FILNAVN)
				.filtype(DOK_RAPP_PRINT.name())
				.status(OPPRETTET.name())
				.build();

		FilInfo filInfo = FilInfoMapper.mapTilFilInfo(request);

		assertThat(filInfo.getFilnavn()).isEqualTo(FILNAVN);
		assertThat(filInfo.getFilType()).isEqualTo(DOK_RAPP_PRINT);
		assertThat(filInfo.getFilStatus()).isEqualTo(OPPRETTET);
		assertThat(filInfo.getKildeType()).isEqualTo(SITS);
		assertThat(filInfo.getKommunikasjonRetning()).isEqualTo(INNGAENDE);
		assertThat(filInfo.getMottattDato()).isNotNull();
		assertThat(filInfo.getSendtDato()).isNull();
	}

	@Test
	void shouldMapFilInfoForUtgaendeFiltype() {
		FilInfoRequest request = FilInfoRequest.builder()
				.filnavn(FILNAVN)
				.filtype(BEST_INFO_PRINT.name())
				.status(OK.name())
				.build();

		FilInfo filInfo = FilInfoMapper.mapTilFilInfo(request);

		assertThat(filInfo.getFilnavn()).isEqualTo(FILNAVN);
		assertThat(filInfo.getFilType()).isEqualTo(BEST_INFO_PRINT);
		assertThat(filInfo.getFilStatus()).isEqualTo(OK);
		assertThat(filInfo.getKildeType()).isEqualTo(SITS);
		assertThat(filInfo.getKommunikasjonRetning()).isEqualTo(UTGAENDE);
		assertThat(filInfo.getMottattDato()).isNull();
		assertThat(filInfo.getSendtDato()).isNotNull();
	}

	@Test
	void skalMappeAllUtgaendeFiltyper() {
		for (String filtype : new String[]{BEST_INFO_PRINT.name(), PRINTFIL.name()}) {
			FilInfoRequest request = FilInfoRequest.builder()
					.filnavn(FILNAVN)
					.filtype(filtype)
					.status(OPPRETTET.name())
					.build();

			FilInfo filInfo = FilInfoMapper.mapTilFilInfo(request);

			assertThat(filInfo.getKommunikasjonRetning()).isEqualTo(UTGAENDE);
			assertThat(filInfo.getMottattDato()).isNull();
			assertThat(filInfo.getSendtDato()).isNotNull();
		}
	}

	@Test
	void skalMappeFilStatusCorrectly() {
		for (FilStatusCode status : FilStatusCode.values()) {
			FilInfoRequest request = FilInfoRequest.builder()
					.filnavn(FILNAVN)
					.filtype(BEST_INFO_PRINT.name())
					.status(status.name())
					.build();

			FilInfo filInfo = FilInfoMapper.mapTilFilInfo(request);

			assertThat(filInfo.getFilStatus()).isEqualTo(status);
		}
	}

	@Test
	void skalMappeFilnavn() {
		FilInfoRequest request = FilInfoRequest.builder()
				.filnavn(FILNAVN)
				.filtype(BEST_INFO_PRINT.name())
				.status(OPPRETTET.name())
				.build();

		FilInfo filInfo = FilInfoMapper.mapTilFilInfo(request);

		assertThat(filInfo.getFilnavn()).isEqualTo(FILNAVN);
	}

	@Test
	void skalSetteMottattDatoForInngaende() {
		FilInfoRequest request = FilInfoRequest.builder()
				.filnavn(FILNAVN)
				.filtype(DOK_RAPP_PRINT.name())
				.status(OPPRETTET.name())
				.build();

		FilInfo filInfo = FilInfoMapper.mapTilFilInfo(request);

		assertThat(filInfo.getMottattDato())
				.isNotNull();
	}

	@Test
	void shouldSetSendtDatoForUtgaende() {
		FilInfoRequest request = FilInfoRequest.builder()
				.filnavn(FILNAVN)
				.filtype(PRINTFIL.name())
				.status(OPPRETTET.name())
				.build();

		FilInfo filInfo = FilInfoMapper.mapTilFilInfo(request);

		assertThat(filInfo.getSendtDato())
				.isNotNull();
	}

	@Test
	void shouldThrownExceptionWhenFiltypeIsInvalid() {
		FilInfoRequest request = FilInfoRequest.builder()
				.filnavn(FILNAVN)
				.filtype("INVALID_TYPE")
				.status(OPPRETTET.name())
				.build();

		assertThatThrownBy(() -> FilInfoMapper.mapTilFilInfo(request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void shouldThrownExceptionWhenFilStatusIsInvalid() {
		FilInfoRequest request = FilInfoRequest.builder()
				.filnavn(FILNAVN)
				.filtype(AVSTEMMING_JOARK.name())
				.status(OPPRETTET.name())
				.build();

		assertThatThrownBy(() -> FilInfoMapper.mapTilFilInfo(request))
				.isInstanceOf(UgyldigInputException.class)
				.hasMessageContaining("filtype må være en av");
	}
}
