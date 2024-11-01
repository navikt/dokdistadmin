package no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser;

import no.nav.dokdistadmin.config.AbstractITest;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.VarselStatusCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.SECONDS;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.DOKDISTADMIN;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfoWithDistribusjonKanal;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfoWithStatusCode;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.KLAR_FOR_DIST;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OVERSENDT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.springframework.http.HttpMethod.PUT;

public class OppdaterForsendelseITest extends AbstractITest {

	private static final String OPPDATERFORSENDELSE_URI = "/rest/v1/administrerforsendelse/oppdaterforsendelse";

	private DistribusjonInfo setupDatabaseWithStatus(String dokumentStatus, VarselStatusCode varselStatus) {
		var distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfo());
		DokumentInfo dokumentInfo = createDokumentInfoWithStatusCode(valueOf(dokumentStatus));
		distribusjonInfo.addDokumentInfo(dokumentInfo);
		distribusjonInfo.setVarselStatus(varselStatus);
		distribusjonInfo.setDistribusjonStatus(DistribusjonStatusCode.valueOf(dokumentStatus));
		dokumentDistribusjonRepository.save(distribusjonInfo);

		commitAndBeginNewTransaction();

		return distribusjonInfo;
	}

	@Test
	void skalOppdatereForsendelse() {
		DistribusjonInfo distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfo());
		DokumentInfo dokumentInfo = createDokumentInfo();
		distribusjonInfo.addDokumentInfo(dokumentInfo);

		dokumentDistribusjonRepository.save(distribusjonInfo);

		commitAndBeginNewTransaction();

		var dokumentInfoId = distribusjonInfo.getDokumentInfos().stream()
				.map(DokumentInfo::getDokumentInfoId)
				.toList().getFirst();

		var nyForsendelsestatus = KLAR_FOR_DIST.name();
		var nyKonversasjonsId = "nyKonversasjonsId";
		var nyVarselstatus = VarselStatusCode.FERDIGSTILT;
		var nyDigitalDistributorId = "nyDigitalDistributor";
		var nyDigitalPostkasseAdresse = "nyDigitalPostkasse";

		var request = OppdaterForsendelseRequest.builder()
				.forsendelseId(dokumentInfoId)
				.forsendelseStatus(nyForsendelsestatus)
				.konversasjonId(nyKonversasjonsId)
				.varselStatus(nyVarselstatus)
				.digitalLeverandoeradresse(nyDigitalDistributorId)
				.digitalPostkasseadresse(nyDigitalPostkasseAdresse)
				.build();

		webTestClient.put()
				.uri(OPPDATERFORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk();

		var oppdatertDokumentInfo = dokumentInfoRepository.findDokumentInfoByDokumentInfoId(dokumentInfoId);
		var oppdatertDistribusjonInfo = dokumentDistribusjonRepository.getReferenceById(distribusjonInfo.getDistribusjonInfoId());

		assertThat(oppdatertDokumentInfo)
				.satisfies(it -> {
					assertThat(it.getDokumentStatus()).isEqualTo(DokumentStatusCode.valueOf(nyForsendelsestatus));
					assertThat(it.getKonversasjonId()).isEqualTo(nyKonversasjonsId);
					assertThat(it.getDigitalDistributorId()).isEqualTo(nyDigitalDistributorId);
					assertThat(it.getDigitalPostkasseAdresse()).isEqualTo(nyDigitalPostkasseAdresse);
					assertThat(it.getChangeStamp().getEndretAv()).isEqualTo(DOKDISTADMIN);
					assertThat(it.getChangeStamp().getEndretDato()).isCloseTo(LocalDateTime.now(), within(1, SECONDS));
				});

		assertThat(oppdatertDistribusjonInfo)
				.satisfies(it -> {
					assertThat(it.getVarselStatus()).isEqualTo(nyVarselstatus);
					assertThat(it.getChangeStamp().getEndretAv()).isEqualTo(DOKDISTADMIN);
					assertThat(it.getChangeStamp().getEndretDato()).isCloseTo(LocalDateTime.now(), within(1, SECONDS));
				});
	}

	@ParameterizedTest
	@CsvSource(value = {
			"OPPRETTET,KLAR_FOR_DIST",
			"KLAR_FOR_DIST,OVERSENDT",
			"KLAR_FOR_DIST,EKSPEDERT",
			"OVERSENDT,EKSPEDERT",
			"OVERSENDT,FEILET",
			"OVERSENDT,BEKREFTET"
	})
	void skalOppdatereForsendelseStatus(String oldForsendelseStatus, String newForsendelseStatus) {
		DistribusjonInfo distribusjonInfo = setupDatabaseWithStatus(oldForsendelseStatus, VarselStatusCode.OPPRETTET);
		long dokumentInfoId = distribusjonInfo.getDokumentInfos().stream()
				.map(DokumentInfo::getDokumentInfoId)
				.toList().getFirst();

		webTestClient.method(PUT)
				.uri(OPPDATERFORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(OppdaterForsendelseRequest.builder()
						.forsendelseId(dokumentInfoId)
						.forsendelseStatus(newForsendelseStatus)
						.build())
				.exchange()
				.expectStatus().isOk();

		commitAndBeginNewTransaction();

		var oppdatertDokumentinfo = dokumentInfoRepository.findDokumentInfoByDokumentInfoId(dokumentInfoId);
		var oppdatertDistribusjonInfo = oppdatertDokumentinfo.getDistribusjonInfo();

		assertThat(oppdatertDokumentinfo.getChangeStamp())
				.satisfies(changeStamp -> {
					assertThat(changeStamp.getEndretDato()).isCloseTo(LocalDateTime.now(), within(1, SECONDS));
					assertThat(changeStamp.getEndretAv()).isEqualTo(DOKDISTADMIN);
				});

		assertThat(oppdatertDistribusjonInfo.getChangeStamp())
				.satisfies(changeStamp -> {
					assertThat(changeStamp.getEndretDato()).isCloseTo(LocalDateTime.now(), within(1, SECONDS));
					assertThat(changeStamp.getEndretAv()).isEqualTo(DOKDISTADMIN);
				});
	}

	@ParameterizedTest
	@CsvSource(value = {
			"eBoks,ola#123",
			"Posten,hei#123"
	})
	void skalOppdatereDokumentDistribusjonAdresse(String digitalLeverandoeradresse, String digitalPostkasseadresse) {
		DistribusjonInfo distribusjonInfo = setupDatabaseWithStatus(KLAR_FOR_DIST.name(), VarselStatusCode.OPPRETTET);
		long dokumentInfoId = distribusjonInfo.getDokumentInfos().stream()
				.map(DokumentInfo::getDokumentInfoId)
				.toList().getFirst();

		webTestClient.method(PUT)
				.uri(OPPDATERFORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(OppdaterForsendelseRequest.builder()
						.forsendelseId(dokumentInfoId)
						.digitalLeverandoeradresse(digitalLeverandoeradresse)
						.digitalPostkasseadresse(digitalPostkasseadresse)
						.build())
				.exchange()
				.expectStatus().isOk();

		commitAndBeginNewTransaction();

		var oppdatertDokumentinfo = dokumentInfoRepository.findDokumentInfoByDokumentInfoId(dokumentInfoId);

		assertThat(oppdatertDokumentinfo.getDigitalDistributorId()).isEqualTo(digitalLeverandoeradresse);
		assertThat(oppdatertDokumentinfo.getDigitalPostkasseAdresse()).isEqualTo(digitalPostkasseadresse);
		assertThat(oppdatertDokumentinfo.getChangeStamp())
				.satisfies(changeStamp -> {
					assertThat(changeStamp.getEndretDato()).isCloseTo(LocalDateTime.now(), within(1, SECONDS));
					assertThat(changeStamp.getEndretAv()).isEqualTo(DOKDISTADMIN);
				});
	}

	@ParameterizedTest
	@CsvSource(value = {
			"OPPRETTET,FERDIGSTILT",
			"OPPRETTET,FEILET"
	})
	void skalOppdatereVarselStatus(VarselStatusCode oldVarselStatus, VarselStatusCode newVarselStatus) {
		DistribusjonInfo distribusjonInfo = setupDatabaseWithStatus(OVERSENDT.name(), oldVarselStatus);

		long dokumentInfoIds = distribusjonInfo.getDokumentInfos().stream()
				.map(DokumentInfo::getDokumentInfoId)
				.toList().getFirst();

		webTestClient.method(PUT)
				.uri(OPPDATERFORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(OppdaterForsendelseRequest.builder()
						.forsendelseId(dokumentInfoIds)
						.varselStatus(newVarselStatus)
						.build())
				.exchange()
				.expectStatus()
				.isOk();

		commitAndBeginNewTransaction();

		var updatedDistributionInfo = dokumentDistribusjonRepository.getReferenceById(distribusjonInfo.getDistribusjonInfoId());

		assertThat(updatedDistributionInfo)
				.satisfies(distributionInfo -> {
					assertThat(distributionInfo.getVarselStatus()).isEqualTo(newVarselStatus);
					assertThat(distributionInfo.getChangeStamp())
							.satisfies(changeStamp -> {
								assertThat(changeStamp.getEndretDato()).isCloseTo(LocalDateTime.now(), within(1, SECONDS));
								assertThat(changeStamp.getEndretAv()).isEqualTo(DOKDISTADMIN);
							});
				});
	}

	@Test
	void skalReturnereNotFoundDersomForsendelseIkkeEksisterer() {

		webTestClient.put()
				.uri(OPPDATERFORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(OppdaterForsendelseRequest.builder()
						.forsendelseId(1L)
						.forsendelseStatus(KLAR_FOR_DIST.name())
						.build())
				.exchange()
				.expectStatus()
				.isNotFound()
				.expectBody(String.class)
				.isEqualTo("\"Forsendelse med forsendelseId=1 ikke funnet i dokdistDb\"");
	}

	@Test
	void skalReturnereInternalServerErrorDersomDokumentstatusOgDistribusjonstatusErUlike() {
		DistribusjonInfo distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfo());

		DokumentInfo dokumentInfo = createDokumentInfoWithStatusCode(EKSPEDERT);
		distribusjonInfo.addDokumentInfo(dokumentInfo);

		dokumentDistribusjonRepository.save(distribusjonInfo);

		commitAndBeginNewTransaction();

		long dokumentInfoId = distribusjonInfo.getDokumentInfos().stream()
				.map(DokumentInfo::getDokumentInfoId)
				.toList().getFirst();

		var response = webTestClient.method(PUT)
				.uri(OPPDATERFORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(OppdaterForsendelseRequest.builder()
						.forsendelseId(dokumentInfoId)
						.forsendelseStatus(OVERSENDT.name())
						.build())
				.exchange()
				.expectStatus().is5xxServerError()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response).contains("Ikke sammenfallende statuser på forsendelse: distribusjonStatus er ikke lik dokumentStatus. distribusjonStatus=%s, dokumentStatus=%s".formatted(
				distribusjonInfo.getDistribusjonStatus().name(), dokumentInfo.getDokumentStatus().name()));
	}

	@Test
	void skalReturnereOkDersomForsendelsestatusErLikEksisterendeDokumentstatus() {
		DistribusjonInfo distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfo());

		DokumentInfo dokumentInfo = createDokumentInfo();
		distribusjonInfo.addDokumentInfo(dokumentInfo);

		dokumentDistribusjonRepository.save(distribusjonInfo);

		commitAndBeginNewTransaction();

		long dokumentInfoId = distribusjonInfo.getDokumentInfos().stream()
				.map(DokumentInfo::getDokumentInfoId)
				.toList().getFirst();

		var response = webTestClient.method(PUT)
				.uri(OPPDATERFORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(OppdaterForsendelseRequest.builder()
						.forsendelseId(dokumentInfoId)
						.forsendelseStatus(dokumentInfo.getDokumentStatus().name())
						.build())
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response).contains("DokumentStatus er allerede satt: Fikk forespørsel om å sette ny dokumentStatus=%s. Dokumentstatus på forsendelse er allerede dokumentStatus=%s".formatted(
				distribusjonInfo.getDistribusjonStatus().name(), dokumentInfo.getDokumentStatus().name()));
	}

	@ParameterizedTest
	@CsvSource(value = {
			"OPPRETTET, BEKREFTET",
			"OPPRETTET, EKSPEDERT",
			"KLAR_FOR_DIST, BEKREFTET",
			"KLAR_FOR_DIST, FEILET",
			"OVERSENDT, KLAR_FOR_DIST",
			"BEKREFTET, KLAR_FOR_DIST",
			"BEKREFTET, OPPRETTET",
			"EKSPEDERT, OPPRETTET",
			"EKSPEDERT, KLAR_FOR_DIST",
			"EKSPEDERT, OVERSENDT",
			"EKSPEDERT, BEKREFTET",
			"FEILET, OPPRETTET",
			"FEILET, KLAR_FOR_DIST",
			"FEILET, OVERSENDT",
			"FEILET, BEKREFTET",
			"FEILET, EKSPEDERT"
	})
	void skalReturnereBadRequestVedUlovligDokumentstatusOvergang(String oldForsendelseStatus, String newForsendelseStatus) {
		DistribusjonInfo distribusjonInfo = setupDatabaseWithStatus(oldForsendelseStatus, VarselStatusCode.OPPRETTET);

		long dokumentInfoId = distribusjonInfo.getDokumentInfos().stream()
				.map(DokumentInfo::getDokumentInfoId)
				.toList().getFirst();

		var response = webTestClient.method(PUT)
				.uri(OPPDATERFORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(OppdaterForsendelseRequest.builder()
						.forsendelseId(dokumentInfoId)
						.varselStatus(VarselStatusCode.OPPRETTET)
						.forsendelseStatus(newForsendelseStatus)
						.build())
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response).contains("Ulovlig statusovergang: kan ikke sette ny dokumentStatus=%s når dokumentStatus=%s".formatted(newForsendelseStatus, oldForsendelseStatus));

	}

	@ParameterizedTest
	@CsvSource(value = {
			"FERDIGSTILT, FEILET",
			"FERDIGSTILT, OPPRETTET",
			"FEILET, FERDIGSTILT",
			"FEILET, OPPRETTET",
	})
	void skalReturnereBadRequestVedUlovligVarselstatusOvergang(String oldVarselStatus, String newVarselStatus) {
		DistribusjonInfo distribusjonInfo = setupDatabaseWithStatus(OVERSENDT.name(), VarselStatusCode.valueOf(oldVarselStatus));

		long dokumentInfoId = distribusjonInfo.getDokumentInfos().stream()
				.map(DokumentInfo::getDokumentInfoId)
				.toList().getFirst();

		var response = webTestClient.method(PUT)
				.uri(OPPDATERFORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(OppdaterForsendelseRequest.builder()
						.forsendelseId(dokumentInfoId)
						.varselStatus(VarselStatusCode.valueOf(newVarselStatus))
						.build())
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response).contains("Ulovlig varselstatusovergang: kan ikke sette ny varselStatus=%s for distribusjon når varselStatus=%s".formatted(newVarselStatus, oldVarselStatus));
	}

	@ParameterizedTest
	@EnumSource(value = DistribusjonKanalCode.class, names = {"SDP"}, mode = EXCLUDE)
	void skalIkkeOppdatereDigitalDistribusjonsadresseDersomDistribusjonskanalIkkeErSDP(DistribusjonKanalCode distribusjonskanal) {
		DistribusjonInfo distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfoWithDistribusjonKanal(distribusjonskanal));

		DokumentInfo dokumentInfo = createDokumentInfo();
		distribusjonInfo.addDokumentInfo(dokumentInfo);
		dokumentDistribusjonRepository.save(distribusjonInfo);

		commitAndBeginNewTransaction();

		long dokumentInfoId = distribusjonInfo.getDokumentInfos().stream()
				.map(DokumentInfo::getDokumentInfoId)
				.toList().getFirst();

		webTestClient.method(PUT)
				.uri(OPPDATERFORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(OppdaterForsendelseRequest.builder()
						.forsendelseId(dokumentInfoId)
						.digitalLeverandoeradresse("nyDigitalDistributor")
						.digitalPostkasseadresse("nyDigitalPostkasse")
						.build())
				.exchange()
				.expectStatus().isOk();

		var oppdatertDokumentinfo = dokumentInfoRepository.findDokumentInfoByDokumentInfoId(dokumentInfoId);

		assertThat(oppdatertDokumentinfo.getDigitalDistributorId()).isEqualTo(dokumentInfo.getDigitalDistributorId());
		assertThat(oppdatertDokumentinfo.getDigitalPostkasseAdresse()).isEqualTo(dokumentInfo.getDigitalPostkasseAdresse());
	}

	@Test
	void skalReturnereBadRequestHvisForsendelseIdErNull() {
		webTestClient.method(PUT)
				.uri(OPPDATERFORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(OppdaterForsendelseRequest.builder()
						.forsendelseId(null)
						.build())
				.exchange()
				.expectStatus()
				.isBadRequest();
	}

	@Test
	void skalReturnereBadRequestHvisBodyRequestErNull() {
		webTestClient.method(PUT)
				.uri(OPPDATERFORSENDELSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus()
				.isBadRequest();
	}
}