package no.nav.dokdistadmin.administrerforsendelse;

import no.nav.dokdistadmin.administrerforsendelse.eformidlingforsendelser.HentEformidlingforsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.AvstemEkspederteForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.AvstemForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.EkspedertForsendelse;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.HentEkspederteForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.HentEkspederteForsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.uekspederteforsendelser.HentUekspederteForsendelserResponse;
import no.nav.dokdistadmin.config.AbstractOauth2Test;
import no.nav.dokdistadmin.config.ApplicationTestConfig;
import no.nav.dokdistadmin.config.DatabaseTest;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import no.nav.dokdistadmin.repository.VarselInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfoWithDistribusjonKanal;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfoWithEkspedertDato;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfoWithStatusCode;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfoWithStatusCodeAndDokumentId;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.SDP;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.TRYGDERETTEN;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.BEKREFTET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OPPRETTET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OVERSENDT;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.apache.commons.lang3.StringUtils.truncate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Transactional
@SpringBootTest(
		classes = {ApplicationTestConfig.class},
		webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureTestDatabase
@ActiveProfiles({"itest"})
public class Rdist001ITest extends AbstractOauth2Test implements DatabaseTest {

	private static final String HENTEKSPEDERTEFORSENDELSER_URI = "/rest/v1/administrerforsendelse/hentekspederteforsendelser";
	private static final String AVSTEMEKSPEDERTEFORSENDELSER_URI = "/rest/v1/administrerforsendelse/avstemekspederteforsendelser";
	private static final String HENTUEKSPEDERTEFORSENDELSER_URI = "/rest/v1/administrerforsendelse/hentuekspederteforsendelser";
	private static final String AVSTEMFORSENDELSER_URI = "/rest/v1/administrerforsendelse/avstemforsendelser";
	private static final String HENTEFORMIDLINGFORSENDELSER_URI = "/rest/v1/administrerforsendelse/henteformidlingforsendelser";

	private static final String AVSTEMTREFERANSE = "MMA-1234";

	@Autowired
	public WebTestClient webTestClient;

	@Autowired
	protected DokumentInfoRepository dokumentInfoRepository;

	@Autowired
	protected DokumentDistribusjonRepository dokumentDistribusjonRepository;

	@Autowired
	protected VarselInfoRepository varselInfoRepository;

	@Autowired
	protected EntityManager entityManager;

	@BeforeEach
	void setup() {
		if (MDC.get(USER_ID) == null) {
			MDC.put(USER_ID, "Rdist001ITest");
		}

		varselInfoRepository.deleteAll();
		dokumentInfoRepository.deleteAll();
		dokumentDistribusjonRepository.deleteAll();
	}

	private DistribusjonInfo setupDatabase() {
		var distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfo());

		distribusjonInfo.addDokumentInfo(createDokumentInfoWithEkspedertDato(LocalDateTime.now().minusSeconds(1)));
		distribusjonInfo.addDokumentInfo(createDokumentInfoWithEkspedertDato(LocalDateTime.now().minusSeconds(2)));
		distribusjonInfo.addDokumentInfo(createDokumentInfoWithEkspedertDato(LocalDateTime.now().minusSeconds(3)));
		dokumentDistribusjonRepository.save(distribusjonInfo);

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
		var distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfo());
		distribusjonInfo.addDokumentInfo(createDokumentInfo());
		dokumentDistribusjonRepository.save(distribusjonInfo);
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

		var distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfoWithDistribusjonKanal(distribusjonkanal));
		distribusjonInfo.addDokumentInfo(createDokumentInfoWithStatusCode(dokumentStatusCode));
		dokumentDistribusjonRepository.save(distribusjonInfo);

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

		var distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfoWithDistribusjonKanal(distribusjonkanal));
		distribusjonInfo.addDokumentInfo(createDokumentInfoWithStatusCode(dokumentStatusCode));
		dokumentDistribusjonRepository.save(distribusjonInfo);

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

		var distribusjonSomSkalHentes = dokumentDistribusjonRepository.save(createDistribusjonInfo());
		var distribusjonSomErForNy = dokumentDistribusjonRepository.save(createDistribusjonInfo());

		distribusjonSomSkalHentes.addDokumentInfo(createDokumentInfoWithStatusCode(OPPRETTET));
		distribusjonSomSkalHentes.addDokumentInfo(createDokumentInfoWithStatusCode(OVERSENDT));
		distribusjonSomSkalHentes.addDokumentInfo(createDokumentInfoWithStatusCode(EKSPEDERT));
		distribusjonSomErForNy.addDokumentInfo(createDokumentInfoWithStatusCode(OPPRETTET));
		dokumentDistribusjonRepository.saveAll(List.of(distribusjonSomSkalHentes, distribusjonSomErForNy));

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
		var dokumenter = forsendelser.get(0).getDokumenter();
		var distribusjonId = forsendelser.get(0).getDistribusjonId();

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
	void skalGiBadRequestDersomAvstemEkspederteForsendelserErUgyldig(List<Forsendelse> forsendelser) {
		setupDatabase();

		var request = new AvstemEkspederteForsendelserRequest(forsendelser);

		webTestClient.put()
				.uri(AVSTEMEKSPEDERTEFORSENDELSER_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isBadRequest();
	}

	private static Stream<Arguments> skalGiBadRequestDersomAvstemEkspederteForsendelserErUgyldig() {
		return Stream.of(
				Arguments.of(Collections.emptyList()),
				Arguments.of(List.of(new Forsendelse(0L)))
		);
	}

	@Test
	void skalAvstemmeForsendelser() {
		setupDatabase();

		var dokumentInfoId = dokumentInfoRepository.findAll().iterator().next().getDokumentInfoId();

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

		dokumentDistribusjonRepository.save(distribusjonSomSkalHentes);

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
		dokumentDistribusjonRepository.save(distribusjonSomSkalHentes);

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
		dokumentDistribusjonRepository.save(distribusjonSomSkalHentes);

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
		dokumentDistribusjonRepository.save(distribusjonSomSkalHentes);

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
	void skalReturnereBadRequestGittUgyldigEllerManglendeDistribusjonKanal(String pathParam) {
		webTestClient.get()
				.uri(HENTEFORMIDLINGFORSENDELSER_URI + pathParam)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isBadRequest();
	}

}