package no.nav.dokdistadmin.administrerforsendelse.filinfo;

import no.nav.dokdistadmin.config.AbstractITest;
import no.nav.dokdistadmin.domain.FilInfo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static no.nav.dokdistadmin.domain.FilStatusCode.OK;
import static no.nav.dokdistadmin.domain.FilStatusCode.OPPRETTET;
import static no.nav.dokdistadmin.domain.FilTypeCode.BEST_INFO_PRINT;
import static no.nav.dokdistadmin.domain.FilTypeCode.DOK_RAPP_PRINT;
import static no.nav.dokdistadmin.domain.KildeTypeCode.SITS;
import static no.nav.dokdistadmin.domain.KommunikasjonRetningCode.INNGAENDE;
import static no.nav.dokdistadmin.domain.KommunikasjonRetningCode.UTGAENDE;
import static org.assertj.core.api.Assertions.assertThat;

class FilInfoIT extends AbstractITest {

	private static final String URI = "/rest/v1/administrerforsendelse/oppdaterfilinformasjon";
	private static final String KILDE = "kilde";

	@Test
	void skalOppdatereStatusForEksisterendeFil() {
		FilInfo eksisterendeFil = filinfoRepository.persist(buildTestFilInfo("eksisterende-fil"));
		commitAndBeginNewTransaction();

		FilInfoRequest request = FilInfoRequest.builder()
				.filInfoId(eksisterendeFil.getFilInfoId())
				.status(OK.name())
				.kilde(KILDE)
				.build();

		webTestClient.put()
				.uri(URI)
				.headers(h -> h.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.filInfoId").isEqualTo(eksisterendeFil.getFilInfoId().intValue());

		FilInfo oppdatertFil = filinfoRepository.findById(eksisterendeFil.getFilInfoId()).orElseThrow();
		assertThat(oppdatertFil.getFilStatus()).isEqualTo(OK);
		assertThat(oppdatertFil.getChangeStamp().getEndretAv()).isEqualTo(KILDE);
	}

	@Test
	void skalOppretteFilInfoHvisFilInfoIdIkkeErSatt() {
		FilInfoRequest request = FilInfoRequest.builder()
				.filnavn("ny-fil")
				.filtype(BEST_INFO_PRINT.name())
				.status(OPPRETTET.name())
				.kilde(KILDE)
				.build();

		webTestClient.put()
				.uri(URI)
				.headers(h -> h.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.filInfoId").isNumber();

		FilInfo opprettetFil = filinfoRepository.findFilInfoByFilnavn("ny-fil");
		assertThat(opprettetFil).isNotNull();
		assertThat(opprettetFil.getFilType()).isEqualTo(BEST_INFO_PRINT);
		assertThat(opprettetFil.getKommunikasjonRetning()).isEqualTo(UTGAENDE);
		assertThat(opprettetFil.getFilStatus()).isEqualTo(OPPRETTET);
		assertThat(opprettetFil.getKildeType()).isEqualTo(SITS);
		assertThat(opprettetFil.getSendtDato()).isNotNull();
		assertThat(opprettetFil.getMottattDato()).isNull();
	}

	@Test
	void skalReturnereBadRequestForUkjentFilInfoId() {
		FilInfoRequest request = FilInfoRequest.builder()
				.filInfoId(999L)
				.status(OK.name())
				.kilde(KILDE)
				.build();

		webTestClient.put()
				.uri(URI)
				.headers(h -> h.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.json("\"Fil med filInfoId=999 finnes ikke\"");
	}

	@Test
	void skalReturnereBadRequestForUgyldigStatus() {
		FilInfo eksisterendeFil = filinfoRepository.persist(buildTestFilInfo("fil-med-ugyldig-status"));
		commitAndBeginNewTransaction();

		FilInfoRequest request = FilInfoRequest.builder()
				.filInfoId(eksisterendeFil.getFilInfoId())
				.status("UGYLDIG")
				.kilde(KILDE)
				.build();

		webTestClient.put()
				.uri(URI)
				.headers(h -> h.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.json("\"Ugyldig input: UGYLDIG er ikke en gyldig kodeverdi for FilStatusCode\"");
	}

	@Test
	void skalReturnereBadRequestHvisFilnavnOgFiltypeErSattVedOppdatering() {
		FilInfo eksisterendeFil = filinfoRepository.persist(buildTestFilInfo("fil-med-ekstra-felter"));
		commitAndBeginNewTransaction();

		FilInfoRequest request = FilInfoRequest.builder()
				.filInfoId(eksisterendeFil.getFilInfoId())
				.filnavn("skal-ikke-vare-satt")
				.filtype(BEST_INFO_PRINT.name())
				.status(OK.name())
				.kilde(KILDE)
				.build();

		webTestClient.put()
				.uri(URI)
				.headers(h -> h.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.json("\"filnavn og filtype kan ikke oppgis når filInfoId er satt\"");
	}

	private FilInfo buildTestFilInfo(String filnavn) {
		return FilInfo.builder()
				.filnavn(filnavn)
				.filType(DOK_RAPP_PRINT)
				.kommunikasjonRetning(INNGAENDE)
				.filStatus(OPPRETTET)
				.kildeType(SITS)
				.mottattDato(LocalDateTime.now())
				.build();
	}
}
