package no.nav.dokdistadmin.administrerforsendelse;

import no.nav.dokdistadmin.administrerforsendelse.eformidlingforsendelser.HentEformidlingforsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.AvstemEkspederteForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.AvstemForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.EkspedertForsendelse;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.HentEkspederteForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.HentEkspederteForsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.HentForsendelseResponse;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.HentForsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.uekspederteforsendelser.HentUekspederteForsendelserResponse;
import no.nav.dokdistadmin.config.AbstractITest;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DistribusjonsTypeKode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfoWithDistribusjonKanal;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfoWithDokumentId;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfoWithEkspedertDato;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfoWithStatusCode;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfoWithStatusCodeAndDokumentId;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.DPO;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.SDP;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.TRYGDERETTEN;
import static no.nav.dokdistadmin.domain.DistribusjonsTypeKode.ANNET;
import static no.nav.dokdistadmin.domain.DistribusjonsTypeKode.VEDTAK;
import static no.nav.dokdistadmin.domain.DistribusjonsTypeKode.VIKTIG;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.BEKREFTET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OPPRETTET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OVERSENDT;
import static org.apache.commons.lang3.StringUtils.truncate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class Rdist001IT extends AbstractITest {

	private static final String HENTEKSPEDERTEFORSENDELSER_URI = "/rest/v1/administrerforsendelse/hentekspederteforsendelser";
	private static final String AVSTEMEKSPEDERTEFORSENDELSER_URI = "/rest/v1/administrerforsendelse/avstemekspederteforsendelser";
	private static final String HENTUEKSPEDERTEFORSENDELSER_URI = "/rest/v1/administrerforsendelse/hentuekspederteforsendelser";
	private static final String AVSTEMFORSENDELSER_URI = "/rest/v1/administrerforsendelse/avstemforsendelser";
	private static final String HENTEFORMIDLINGFORSENDELSER_URI = "/rest/v1/administrerforsendelse/henteformidlingforsendelser";
	private static final String EFORMIDLINGFORSENDELSER_URI = "/rest/v1/administrerforsendelse/eformidlingforsendelser";
	private static final String HENTFORSENDELSER_URI = "/rest/v1/administrerforsendelse/hentForsendelser";

	private static final String AVSTEMTREFERANSE = "MMA-1234";

	private DistribusjonInfo setupDatabase() {
		var distribusjonInfo = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

		distribusjonInfo.addDokumentInfo(createDokumentInfoWithEkspedertDato(LocalDateTime.now().minusSeconds(1)));
		distribusjonInfo.addDokumentInfo(createDokumentInfoWithEkspedertDato(LocalDateTime.now().minusSeconds(2)));
		distribusjonInfo.addDokumentInfo(createDokumentInfoWithEkspedertDato(LocalDateTime.now().minusSeconds(3)));
		dokumentDistribusjonRepository.persist(distribusjonInfo);

		commitAndBeginNewTransaction();

		return distribusjonInfo;
	}

	@ParameterizedTest
	@MethodSource
	void skalHenteEkspederteForsendelser(int maksForsendelser, int forventetAntallForsendelser) {
		setupDatabase();

		var response = webTestClient.method(GET)
				.uri(HENTEKSPEDERTEFORSENDELSER_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(new HentEkspederteForsendelserRequest(maksForsendelser))
				.exchange()
				.expectStatus().isOk()
				.returnResult(HentEkspederteForsendelserResponse.class)
				.getResponseBody()
				.blockFirst();

		assertNotNull(response);
		assertThat(response.forsendelser())
				.hasSize(forventetAntallForsendelser)
				.extracting(EkspedertForsendelse::getForsendelseId)
				.doesNotHaveDuplicates();
	}

	// Gitt N treff i databasen (forsendelser som oppfyller kravene)
	// (x, y) der x = maksForsendelser, y = forventetAntallForsendelser
	private static Stream<Arguments> skalHenteEkspederteForsendelser() {
		return Stream.of(
				Arguments.of(0, 3), // maksForsendelser lik 0 -> returner MAX_FORSENDELSER = 10000 i db
				Arguments.of(2, 2), // maksForsendelser < N -> returner maksForsendelser fra db
				Arguments.of(4, 3) // maksForsendelser > N -> returner N treff i db
		);
	}

	@Test
	void skalGiNoContentDersomIngenTreffVedHentingAvEkspederteForsendelser() {
		var distribusjonInfo = dokumentDistribusjonRepository.persist(createDistribusjonInfo());
		distribusjonInfo.addDokumentInfo(createDokumentInfo());
		dokumentDistribusjonRepository.persist(distribusjonInfo);
		commitAndBeginNewTransaction();

		webTestClient.method(GET)
				.uri(HENTEKSPEDERTEFORSENDELSER_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(new HentEkspederteForsendelserRequest(2))
				.exchange()
				.expectStatus().isNoContent();
	}

	@ParameterizedTest
	@CsvSource({
			"{\"ukjentFelt\": \"2\"}, maksForsendelser kan ikke være 'null'",
			"{\"maksForsendelser\": null}, maksForsendelser kan ikke være 'null'",
			"{\"maksForsendelser\": -1}, maksForsendelser må være et positivt tall"
	})
	void skalGiBadRequestDersomMaksForsendelserErUgyldigEllerMangler(String jsonRequest, String expectedResponse) {
		setupDatabase();

		var response = webTestClient.method(GET)
				.uri(HENTEKSPEDERTEFORSENDELSER_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.contentType(APPLICATION_JSON)
				.bodyValue(jsonRequest)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response).contains(expectedResponse);
	}

	@Test
	void skalAvstemmeEkspederteForsendelser() {
		var persistertDistribusjoninfo = setupDatabase();
		var dokumentinfoIdList = persistertDistribusjoninfo.getDokumentInfos().stream().map(DokumentInfo::getDokumentInfoId).toList();

		var request = new AvstemEkspederteForsendelserRequest(List.of(
				new Forsendelse(dokumentinfoIdList.get(0)),
				new Forsendelse(dokumentinfoIdList.get(1))
		));

		webTestClient.put()
				.uri(AVSTEMEKSPEDERTEFORSENDELSER_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk();

		assertNotNull(dokumentInfoRepository.findDokumentInfoByDokumentInfoId(dokumentinfoIdList.get(0)).getAvstemtArkivDato());
		assertNotNull(dokumentInfoRepository.findDokumentInfoByDokumentInfoId(dokumentinfoIdList.get(1)).getAvstemtArkivDato());
		assertNull(dokumentInfoRepository.findDokumentInfoByDokumentInfoId(dokumentinfoIdList.get(2)).getAvstemtArkivDato());
	}

	@ParameterizedTest
	@CsvSource(value = {
			"SDP, -1",
			"SDP, ikkeEtTall"})
	void skalReturnereBadRequestForUgyldigeRequests(String distribusjonkanal, String antallTimer) {

		webTestClient.get()
				.uri(format(HENTUEKSPEDERTEFORSENDELSER_URI + "/%s/%s", distribusjonkanal, antallTimer))
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus()
				.isBadRequest();
	}

	@ParameterizedTest
	@EnumSource(
			value = DokumentStatusCode.class,
			names = {"EKSPEDERT", "FEILET", "RETURPOSTBEHANDLET"},
			mode = EXCLUDE
	)
	void skalHenteUekspederteForsendelser(DokumentStatusCode dokumentStatusCode) {
		var distribusjonkanal = PRINT;
		var antallTimer = 0L;

		var distribusjonInfo = dokumentDistribusjonRepository.persist(createDistribusjonInfoWithDistribusjonKanal(distribusjonkanal));
		distribusjonInfo.addDokumentInfo(createDokumentInfoWithStatusCode(dokumentStatusCode));
		dokumentDistribusjonRepository.persist(distribusjonInfo);

		commitAndBeginNewTransaction();

		var response = webTestClient.get()
				.uri(format(HENTUEKSPEDERTEFORSENDELSER_URI + "/%s/%s", distribusjonkanal, antallTimer))
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(HentUekspederteForsendelserResponse.class)
				.returnResult()
				.getResponseBody();

		assertNotNull(response);
		assertThat(response.getUekspederteForsendelser()).hasSize(1);
	}

	@ParameterizedTest
	@EnumSource(
			value = DokumentStatusCode.class,
			names = {"EKSPEDERT", "FEILET", "RETURPOSTBEHANDLET"}
	)
	void skalIkkeHenteEkspederteForsendelser(DokumentStatusCode dokumentStatusCode) {
		var distribusjonkanal = PRINT;
		var antallTimer = 0L;

		var distribusjonInfo = dokumentDistribusjonRepository.persist(createDistribusjonInfoWithDistribusjonKanal(distribusjonkanal));
		distribusjonInfo.addDokumentInfo(createDokumentInfoWithStatusCode(dokumentStatusCode));
		dokumentDistribusjonRepository.persist(distribusjonInfo);

		commitAndBeginNewTransaction();

		webTestClient.get()
				.uri(format(HENTUEKSPEDERTEFORSENDELSER_URI + "/%s/%s", distribusjonkanal, antallTimer))
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus()
				.isNoContent();
	}

	@Test
	void skalHenteUekspederteForsendelserEldreEnnAntallTimer() {
		var antallTimer = 4L;

		var distribusjonSomSkalHentes = dokumentDistribusjonRepository.persist(createDistribusjonInfo());
		var distribusjonSomErForNy = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

		distribusjonSomSkalHentes.addDokumentInfo(createDokumentInfoWithStatusCode(OPPRETTET));
		distribusjonSomSkalHentes.addDokumentInfo(createDokumentInfoWithStatusCode(OVERSENDT));
		distribusjonSomSkalHentes.addDokumentInfo(createDokumentInfoWithStatusCode(EKSPEDERT));
		distribusjonSomErForNy.addDokumentInfo(createDokumentInfoWithStatusCode(OPPRETTET));
		dokumentDistribusjonRepository.persistAll(List.of(distribusjonSomSkalHentes, distribusjonSomErForNy));

		var id = distribusjonSomSkalHentes.getDistribusjonInfoId();

		entityManager.createQuery("update DistribusjonInfo di set di.changeStamp.opprettetDato = :tid where di.distribusjonInfoId=:id")
				.setParameter("tid", LocalDateTime.now().minusHours(antallTimer + 1))
				.setParameter("id", id)
				.executeUpdate();

		commitAndBeginNewTransaction();

		var response = webTestClient.get()
				.uri(format(HENTUEKSPEDERTEFORSENDELSER_URI + "/%s/%s", SDP.name(), antallTimer))
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(HentUekspederteForsendelserResponse.class)
				.returnResult()
				.getResponseBody();

		assertNotNull(response);

		var forsendelser = response.getUekspederteForsendelser();
		var dokumenter = forsendelser.getFirst().getDokumenter();
		var distribusjonId = forsendelser.getFirst().getDistribusjonId();

		assertThat(forsendelser).hasSize(1);
		assertThat(dokumenter).hasSize(2);
		assertThat(distribusjonId).isEqualTo(distribusjonSomSkalHentes.getDistribusjonId());
	}

	@Test
	void skalTrunkereUserIdOgSetteEndretAv() {
		var persistertDistribusjoninfo = setupDatabase();

		var dokumentinfoId = persistertDistribusjoninfo.getDokumentInfos().iterator().next().getDokumentInfoId();
		var request = new AvstemEkspederteForsendelserRequest(List.of(
				new Forsendelse(dokumentinfoId)
		));

		webTestClient.put()
				.uri(AVSTEMEKSPEDERTEFORSENDELSER_URI)
				.headers(headers -> headers.setBearerAuth(jwtWithoutAzpNameClaim()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk();

		var endretAv = dokumentInfoRepository.findDokumentInfoByDokumentInfoId(dokumentinfoId).getChangeStamp().getEndretAv();

		assertThat(endretAv).isEqualTo(truncate(OID, 20));
	}

	@ParameterizedTest
	@MethodSource
	@NullSource
	void skalGiBadRequestDersomAvstemEkspederteForsendelserErUgyldig(List<Forsendelse> forsendelser) {
		setupDatabase();

		var request = new AvstemEkspederteForsendelserRequest(forsendelser);

		var response = webTestClient.put()
				.uri(AVSTEMEKSPEDERTEFORSENDELSER_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertThat(response)
				.isNotNull()
				.contains("forsendelser kan ikke være null eller en tom liste");
	}

	private static Stream<Arguments> skalGiBadRequestDersomAvstemEkspederteForsendelserErUgyldig() {
		return Stream.of(
				Arguments.of(Collections.emptyList())
		);
	}

	@Test
	void skalAvstemmeForsendelser() {
		DistribusjonInfo distribusjonInfo = setupDatabase();
		var dokumentInfoId = distribusjonInfo.getDokumentInfos().iterator().next().getDokumentInfoId();

		var request = new AvstemForsendelserRequest(AVSTEMTREFERANSE, List.of(new Forsendelse(dokumentInfoId)));

		webTestClient.put()
				.uri(AVSTEMFORSENDELSER_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk();

		commitAndBeginNewTransaction();

		var dokumentInfo = dokumentInfoRepository.findDokumentInfoByDokumentInfoId(dokumentInfoId);

		assertThat(dokumentInfo)
				.isNotNull()
				.satisfies(it -> {
					assertThat(it.getAvstemtReferanse()).isEqualTo(AVSTEMTREFERANSE);
					assertThat(it.getAvstemtDato()).isNotNull();
				});
	}

	static Stream<Arguments> skalHenteForsendelserMedIdDistribusjonstype() {
		final String viktigEllerVedtak = Stream.of(VIKTIG, VEDTAK).map(DistribusjonsTypeKode::name).collect(Collectors.joining(","));
		final String ekspedertEllerBekreftet = Stream.of(EKSPEDERT, BEKREFTET).map(DokumentStatusCode::name).collect(Collectors.joining(","));

		return Stream.of(
				Arguments.of(null, null, null, TRYGDERETTEN, new DistribusjonsTypeKode[]{ANNET, VEDTAK, VIKTIG, null}, OK, 4 * 4),
				Arguments.of(TRYGDERETTEN.name(), null, null, TRYGDERETTEN, new DistribusjonsTypeKode[]{VEDTAK}, OK, 4),
				Arguments.of(TRYGDERETTEN.name(), null, null, SDP, new DistribusjonsTypeKode[]{VEDTAK}, OK, 0),
				Arguments.of(null, viktigEllerVedtak, null, TRYGDERETTEN, new DistribusjonsTypeKode[]{VEDTAK, ANNET}, OK, 4),
				Arguments.of(null, null, ekspedertEllerBekreftet, TRYGDERETTEN, new DistribusjonsTypeKode[]{VEDTAK}, OK, 2),
				Arguments.of(null, viktigEllerVedtak, null, TRYGDERETTEN, new DistribusjonsTypeKode[]{VEDTAK, null}, OK, 4),
				Arguments.of(TRYGDERETTEN.name(), viktigEllerVedtak, null, TRYGDERETTEN, new DistribusjonsTypeKode[]{VIKTIG}, OK, 4)
		);
	}

	@ParameterizedTest
	@MethodSource
	public void skalHenteForsendelserMedIdDistribusjonstype(String distribusjonskanalQueryParam, String distribusjonstyperQueryParam, String dokumentstatusQueryParam,
															DistribusjonKanalCode distribusjonskanal, DistribusjonsTypeKode[] distribusjonstyper,
															HttpStatus expectedStatus, int dokumentInfosMatched) {
		String journalpostIdsParam = Stream.of(distribusjonstyper).flatMap(distribusjonstype -> {
			var distribusjonSomSkalHentes = createDistribusjonInfoWithDistribusjonKanal(distribusjonskanal);
			distribusjonSomSkalHentes.setDistribusjonstype(distribusjonstype);

			var opprettetDokument = createDokumentInfoWithStatusCodeAndDokumentId(OPPRETTET, "opprettetDokument" + distribusjonstype);
			var oversendtDokument = createDokumentInfoWithStatusCodeAndDokumentId(OVERSENDT, "oversendtDokument" + distribusjonstype);
			var ekspedertDokument = createDokumentInfoWithStatusCodeAndDokumentId(EKSPEDERT, "ekspedertDokument" + distribusjonstype);
			var bekreftetDokument = createDokumentInfoWithStatusCodeAndDokumentId(BEKREFTET, "bekreftetDokument" + distribusjonstype);

			distribusjonSomSkalHentes.addDokumentInfo(opprettetDokument);
			distribusjonSomSkalHentes.addDokumentInfo(oversendtDokument);
			distribusjonSomSkalHentes.addDokumentInfo(ekspedertDokument);
			distribusjonSomSkalHentes.addDokumentInfo(bekreftetDokument);

			dokumentDistribusjonRepository.persist(distribusjonSomSkalHentes);
			return distribusjonSomSkalHentes.getDokumentInfos().stream();
		}).map(DokumentInfo::getArkivkode).collect(Collectors.joining(","));

		commitAndBeginNewTransaction();

		var request = webTestClient.get()
				.uri(HENTFORSENDELSER_URI +
						"?journalpostliste=" + journalpostIdsParam +
						(distribusjonskanalQueryParam != null ? "&distribusjonkanal=" + distribusjonskanalQueryParam : "") +
						(distribusjonstyperQueryParam != null ? "&distribusjonstyper=" + distribusjonstyperQueryParam : "") +
						(dokumentstatusQueryParam != null ? "&dokumentstatus=" + dokumentstatusQueryParam : "")
				)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange();

		if (expectedStatus == OK) {
			var response = request
					.expectStatus().isOk()
					.returnResult(HentForsendelserResponse.class)
					.getResponseBody()
					.blockFirst();

			assertNotNull(response);
			assertThat(response.forsendelseListe()).hasSize(dokumentInfosMatched);
		} else {
			request.expectStatus().isEqualTo(expectedStatus);
		}
	}


	static Stream<Arguments> skalValidereParametreForHentForsendelser() {
		return Stream.of(
				Arguments.of(null, null, null, null),
				Arguments.of(null, "TRYGDERETTEN", null, null),
				Arguments.of("1200,1201,1202,1034", "RYGDERETTEN", null, null),
				Arguments.of("1200,1201,1202,1034", null, "IKTIG", null),
				Arguments.of("1200,1201,1202,1034", null, null, "KSPEDERT")
		);
	}

	@ParameterizedTest
	@MethodSource
	public void skalValidereParametreForHentForsendelser(String journalpostListQueryParam, String distribusjonskanalQueryParam, String distribusjonstyperQueryParam, String dokumentstatusQueryParam) {
		webTestClient.get()
				.uri(HENTFORSENDELSER_URI +
						"?" + Stream.of(
								(journalpostListQueryParam != null ? "journalpostliste=" + journalpostListQueryParam : null),
								(distribusjonskanalQueryParam != null ? "distribusjonkanal=" + distribusjonskanalQueryParam : null),
								(distribusjonstyperQueryParam != null ? "distribusjonstyper=" + distribusjonstyperQueryParam : null),
								(dokumentstatusQueryParam != null ? "dokumentstatus=" + dokumentstatusQueryParam : null))
						.filter(Objects::nonNull)
						.collect(Collectors.joining("&"))
				)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isBadRequest();
	}

	/*
	 * 'inkluderAvstemte' = false: skal kun returnere forsendelser uten avstemtDato
	 * 'inkluderAvstemte' = true: skal returnere alle forsendelser
	 * 'inkluderAvstemte' = null/mangler: skal returnere alle forsendelser
	 */
	@ParameterizedTest
	@MethodSource
	void skalReturnereForsendelserBasertPaaInkluderAvstemte(Boolean inkluderAvstemte, List<String> forventedeForsendelser) {

		var distribusjon = createDistribusjonInfo();
		var dokumentInfoUtenAvstemtDato = createDokumentInfoWithDokumentId("ikkeAvstemt");
		var dokumentInfoMedAvstemtDato = createDokumentInfoWithDokumentId("avstemt");
		dokumentInfoMedAvstemtDato.setAvstemtDato(LocalDateTime.now());

		distribusjon.addDokumentInfo(dokumentInfoMedAvstemtDato);
		distribusjon.addDokumentInfo(dokumentInfoUtenAvstemtDato);

		dokumentDistribusjonRepository.persist(distribusjon);

		commitAndBeginNewTransaction();

		String journalpostliste = Stream.of(dokumentInfoMedAvstemtDato.getArkivkode(), dokumentInfoUtenAvstemtDato.getArkivkode())
				.map(String::valueOf)
				.collect(Collectors.joining(","));

		var response = webTestClient.get()
				.uri(uriBuilder -> uriBuilder
						.path(HENTFORSENDELSER_URI)
						.queryParam("journalpostliste", journalpostliste)
						.queryParamIfPresent("inkluderAvstemte", Optional.ofNullable(inkluderAvstemte))
						.build())
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isOk()
				.returnResult(HentForsendelserResponse.class)
				.getResponseBody()
				.map(HentForsendelserResponse::forsendelseListe)
				.blockFirst();

		assertThat(response)
				.isNotNull()
				.extracting(HentForsendelseResponse::getBestillingsId)
				.containsExactlyInAnyOrderElementsOf(forventedeForsendelser);
	}

	static Stream<Arguments> skalReturnereForsendelserBasertPaaInkluderAvstemte() {
		var alleForsendelser = List.of("avstemt", "ikkeAvstemt");
		var ikkeAvstemteForsendelser = List.of("ikkeAvstemt");

		return Stream.of(
				Arguments.of(true, alleForsendelser),
				Arguments.of(false, ikkeAvstemteForsendelser),
				Arguments.of(null, alleForsendelser)
		);
	}

	@Test
	void skalKunHenteEformidlingforsendelserMedDokumentstatusOversendtEllerBekreftet() {
		var distribusjonSomSkalHentes = createDistribusjonInfoWithDistribusjonKanal(TRYGDERETTEN);

		var opprettetDokument = createDokumentInfoWithStatusCodeAndDokumentId(OPPRETTET, "opprettetDokument");
		var oversendtDokument = createDokumentInfoWithStatusCodeAndDokumentId(OVERSENDT, "oversendtDokument");
		var ekspedertDokument = createDokumentInfoWithStatusCodeAndDokumentId(EKSPEDERT, "ekspedertDokument");
		var bekreftetDokument = createDokumentInfoWithStatusCodeAndDokumentId(BEKREFTET, "bekreftetDokument");

		distribusjonSomSkalHentes.addDokumentInfo(opprettetDokument);
		distribusjonSomSkalHentes.addDokumentInfo(oversendtDokument);
		distribusjonSomSkalHentes.addDokumentInfo(ekspedertDokument);
		distribusjonSomSkalHentes.addDokumentInfo(bekreftetDokument);

		dokumentDistribusjonRepository.persist(distribusjonSomSkalHentes);

		commitAndBeginNewTransaction();

		var response = webTestClient.get()
				.uri(HENTEFORMIDLINGFORSENDELSER_URI + "?distribusjonKanal=TRYGDERETTEN")
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isOk()
				.returnResult(HentEformidlingforsendelserResponse.class)
				.getResponseBody()
				.blockFirst();

		assertNotNull(response);
		assertThat(response.getForsendelser()).hasSize(2);

		var forventetDokumentMedStatusOversendt = dokumentInfoRepository.findDokumentInfoByDokumentId(oversendtDokument.getDokumentId());
		var forventetDokumentMedStatusBekreftet = dokumentInfoRepository.findDokumentInfoByDokumentId(bekreftetDokument.getDokumentId());
		var forventedeIder = List.of(forventetDokumentMedStatusOversendt.getDokumentInfoId(), forventetDokumentMedStatusBekreftet.getDokumentInfoId());
		var faktiskeIder = response.getForsendelser().stream()
				.map(HentEformidlingforsendelserResponse.Forsendelse::getForsendelseId)
				.toList();

		assertThat(forventedeIder).containsExactlyInAnyOrderElementsOf(faktiskeIder);
	}

	@ParameterizedTest
	@EnumSource(value = DistribusjonKanalCode.class)
	void skalHenteEformidlingforsendelserForGittDistribusjonskanal(DistribusjonKanalCode distribusjonskanal) {
		var distribusjonSomSkalHentes = createDistribusjonInfoWithDistribusjonKanal(distribusjonskanal);

		distribusjonSomSkalHentes.addDokumentInfo(createDokumentInfoWithStatusCode(BEKREFTET));
		dokumentDistribusjonRepository.persist(distribusjonSomSkalHentes);

		commitAndBeginNewTransaction();

		var response = webTestClient.get()
				.uri(uriBuilder -> uriBuilder
						.path(HENTEFORMIDLINGFORSENDELSER_URI)
						.queryParam("distribusjonKanal", distribusjonskanal.name())
						.build())
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isOk()
				.returnResult(HentEformidlingforsendelserResponse.class)
				.getResponseBody()
				.blockFirst();

		assertNotNull(response);
		assertThat(response.getForsendelser()).hasSize(1);
	}

	@Test
	void skalIkkeHenteGamleEformidlingforsendelser() {
		var distribusjonSomSkalHentes = createDistribusjonInfoWithDistribusjonKanal(TRYGDERETTEN);
		var dokument = createDokumentInfoWithStatusCode(BEKREFTET);
		distribusjonSomSkalHentes.addDokumentInfo(dokument);
		dokumentDistribusjonRepository.persist(distribusjonSomSkalHentes);

		commitAndBeginNewTransaction();

		entityManager.createQuery("update DokumentInfo dok set dok.changeStamp.opprettetDato = :tid where dok.dokumentId=:id")
				.setParameter("tid", LocalDateTime.of(2021, 12, 31, 0, 0, 0))
				.setParameter("id", dokument.getDokumentId())
				.executeUpdate();

		commitAndBeginNewTransaction();

		var response = webTestClient.get()
				.uri(HENTEFORMIDLINGFORSENDELSER_URI + "?distribusjonKanal=TRYGDERETTEN")
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isOk()
				.returnResult(HentEformidlingforsendelserResponse.class)
				.getResponseBody()
				.blockFirst();

		assertNotNull(response);
		assertThat(response.getForsendelser()).isEmpty();
	}

	@ParameterizedTest
	@EnumSource(value = DistribusjonKanalCode.class, names = {"TRYGDERETTEN"}, mode = EXCLUDE)
	void skalIkkeHenteEformidlingforsendelserMedFeilDistribusjonskanal(DistribusjonKanalCode distribusjonskanal) {
		var distribusjonSomSkalHentes = createDistribusjonInfoWithDistribusjonKanal(distribusjonskanal);
		var dokument = createDokumentInfoWithStatusCode(BEKREFTET);
		distribusjonSomSkalHentes.addDokumentInfo(dokument);
		dokumentDistribusjonRepository.persist(distribusjonSomSkalHentes);

		commitAndBeginNewTransaction();

		var response = webTestClient.get()
				.uri(HENTEFORMIDLINGFORSENDELSER_URI + "?distribusjonKanal=TRYGDERETTEN")
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isOk()
				.returnResult(HentEformidlingforsendelserResponse.class)
				.getResponseBody()
				.blockFirst();

		assertNotNull(response);
		assertThat(response.getForsendelser()).isEmpty();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"?distribusjonKanal=UGYLDIG_KANAL",
			"?kanalDistribusjon=TRYGDERETTEN",
	})
	@EmptySource
	void skalReturnereBadRequestGittUgyldigDistribusjonKanal(String kanal) {
		webTestClient.get()
				.uri(HENTEFORMIDLINGFORSENDELSER_URI + kanal)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void skalHenteEformidlingforsendelserForKonfigurerteDistribusjonskanaler() {
		var distribusjonTrygderetten = createDistribusjonInfoWithDistribusjonKanal(TRYGDERETTEN);
		var trygderettenDokument = createDokumentInfoWithStatusCodeAndDokumentId(OVERSENDT, "trygderettenDokument");
		distribusjonTrygderetten.addDokumentInfo(trygderettenDokument);
		dokumentDistribusjonRepository.persist(distribusjonTrygderetten);

		var distribusjonDpo = createDistribusjonInfoWithDistribusjonKanal(DPO);
		var dpoDokument = createDokumentInfoWithStatusCodeAndDokumentId(BEKREFTET, "dpoDokument");
		distribusjonDpo.addDokumentInfo(dpoDokument);
		dokumentDistribusjonRepository.persist(distribusjonDpo);

		var distribusjonPrint = createDistribusjonInfoWithDistribusjonKanal(PRINT);
		var printDokument = createDokumentInfoWithStatusCodeAndDokumentId(OVERSENDT, "printDokument");
		distribusjonPrint.addDokumentInfo(printDokument);
		dokumentDistribusjonRepository.persist(distribusjonPrint);

		commitAndBeginNewTransaction();

		var response = webTestClient.get()
				.uri(uriBuilder -> uriBuilder
						.path(EFORMIDLINGFORSENDELSER_URI)
						.queryParam("distribusjonKanaler", DPO.name(), TRYGDERETTEN.name())
						.build())
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isOk()
				.returnResult(HentEformidlingforsendelserResponse.class)
				.getResponseBody()
				.blockFirst();

		assertNotNull(response);

		assertThat(response.getForsendelser()).hasSize(2);
	}

	@Test
	void skalIkkeHenteEformidlingforsendelserForKanalerSomIkkeErKonfigurert() {
		var distribusjonPrint = createDistribusjonInfoWithDistribusjonKanal(PRINT);
		distribusjonPrint.addDokumentInfo(createDokumentInfoWithStatusCodeAndDokumentId(OVERSENDT, "printDokument"));
		dokumentDistribusjonRepository.persist(distribusjonPrint);

		var distribusjonSdp = createDistribusjonInfoWithDistribusjonKanal(SDP);
		distribusjonSdp.addDokumentInfo(createDokumentInfoWithStatusCodeAndDokumentId(BEKREFTET, "sdpDokument"));
		dokumentDistribusjonRepository.persist(distribusjonSdp);

		commitAndBeginNewTransaction();

		var response = webTestClient.get()
				.uri(uriBuilder -> uriBuilder
						.path(EFORMIDLINGFORSENDELSER_URI)
						.queryParam("distribusjonKanaler", DPO.name(), TRYGDERETTEN.name())
						.build())
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isOk()
				.returnResult(HentEformidlingforsendelserResponse.class)
				.getResponseBody()
				.blockFirst();

		assertNotNull(response);
		assertThat(response.getForsendelser()).isEmpty();
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"?distribusjonKanal=UGYLDIG_KANAL",
			"?kanal=DPO",
	})
	@EmptySource
	void skalReturnereBadRequestForAlleEformidlingforsendelserGittUgyldigEllerManglendeDistribusjonKanal(String pathParam) {
		webTestClient.get()
				.uri(EFORMIDLINGFORSENDELSER_URI + pathParam)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isBadRequest();
	}

}