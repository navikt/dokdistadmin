package no.nav.dokdistadmin.administrerforsendelse;

import no.nav.dokdistadmin.administrerforsendelse.varselinfo.Notifikasjon;
import no.nav.dokdistadmin.administrerforsendelse.varselinfo.OppdaterVarselInfoRequest;
import no.nav.dokdistadmin.config.AbstractOauth2Test;
import no.nav.dokdistadmin.config.ApplicationTestConfig;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.VarslingKanalCode;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import no.nav.dokdistadmin.repository.VarselInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.DISTRIBUSJON_KANAL_PRINT;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.EPOSTADDRESS;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.VARSLINGSTEKST;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.VARSLINGSTITTEL;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.createDistribusjonInfoWithoutDokumentInfo;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.DITTNAV;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.EPOST;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.MOBILTELEFON;
import static no.nav.dokdistadmin.repository.TestUtils.DOKUMENT_ID_1;
import static no.nav.dokdistadmin.repository.TestUtils.createDokumentInfo;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Transactional
@SpringBootTest(
		classes = {ApplicationTestConfig.class},
		webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@AutoConfigureTestDatabase
@ActiveProfiles({"itest"})
public class VarselinfoITest extends AbstractOauth2Test {

	private static final String OPPDATERVARSELINFO_URI = "/rest/v1/administrerforsendelse/oppdatervarselinfo";

	private static final String KONTAKTINFO_SMS = "98765432";
	private static final String KONTAKTINFO_EPOST = "mottaker@nav.no";
	private static final LocalDateTime VARSEL_SENDT_DATO = LocalDateTime.now().minusMinutes(3);
	private static final LocalDateTime SECOND_VARSEL_SENDT_DATO = LocalDateTime.now().minusMinutes(2);

	@Autowired
	public WebTestClient webTestClient;

	@Autowired
	protected DokumentInfoRepository dokumentInfoRepository;

	@Autowired
	protected DokumentDistribusjonRepository dokumentDistribusjonRepository;

	@Autowired
	protected VarselInfoRepository varselInfoRepository;

	@BeforeEach
	void setup() {
		if (MDC.get(USER_ID) == null) {
			MDC.put(USER_ID, "VarselInfoITest");
		}

		varselInfoRepository.deleteAll();
		dokumentInfoRepository.deleteAll();
		dokumentDistribusjonRepository.deleteAll();
	}

	private DistribusjonInfo setupDatabase(DistribusjonInfo distribusjonInfo) {
		distribusjonInfo.setDistribusjonKanal(DITTNAV);
		var nyDistribusjonInfo = dokumentDistribusjonRepository.save(distribusjonInfo);

		nyDistribusjonInfo.addDokumentInfo(createDokumentInfo());
		dokumentDistribusjonRepository.save(nyDistribusjonInfo);

		commitAndBeginNewTransaction();

		return nyDistribusjonInfo;
	}

	@Test
	void skalOppdatereVarselinfo() {
		setupDatabase(createDistribusjonInfoWithoutDokumentInfo());
		var dokument = dokumentInfoRepository.findDokumentInfoByDokumentId(DOKUMENT_ID_1);

		var request = createOppdaterVarselInfoRequest(dokument.getDokumentInfoId());

		webTestClient.put()
				.uri(OPPDATERVARSELINFO_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk();

		assertThat(StreamSupport.stream(varselInfoRepository.findAll().spliterator(), false).count()).isEqualTo(2);

		var varsler = dokumentInfoRepository.findDokumentInfoByDokumentId(DOKUMENT_ID_1).getVarselInfos();

		assertThat(varsler).anySatisfy(varsel -> {
			assertEquals(MOBILTELEFON, varsel.getVarslingKanal());
			assertNull(varsel.getVarslingstittel());
			assertEquals(VARSLINGSTEKST, varsel.getVarslingstekst());
			assertEquals(KONTAKTINFO_SMS, varsel.getMobiltelefonNummer());
			assertEquals(VARSEL_SENDT_DATO, varsel.getVarslingstidspunkt());
			assertNull(varsel.getEpostAdresse());
		});

		assertThat(varsler).anySatisfy(varsel -> {
			assertEquals(EPOST, varsel.getVarslingKanal());
			assertEquals(VARSLINGSTITTEL, varsel.getVarslingstittel());
			assertEquals(VARSLINGSTEKST, varsel.getVarslingstekst());
			assertEquals(KONTAKTINFO_EPOST, varsel.getEpostAdresse());
			assertEquals(VARSEL_SENDT_DATO, varsel.getVarslingstidspunkt());
			assertNull(varsel.getMobiltelefonNummer());
		});
	}

	@Test
	void skalOppdatereListOfVarselinfo() {

		setupDatabase(createDistribusjonInfoWithoutDokumentInfo());
		var dokument = dokumentInfoRepository.findDokumentInfoByDokumentId(DOKUMENT_ID_1);

		var request = createOppdaterVarselInfoRequest(dokument.getDokumentInfoId());
		List<Notifikasjon> notifikasjoner = request.getNotifikasjoner();
		notifikasjoner.add(createNotifikasjon(EPOST, EPOSTADDRESS, VARSLINGSTITTEL, SECOND_VARSEL_SENDT_DATO));

		webTestClient.put()
				.uri(OPPDATERVARSELINFO_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk();

		assertThat(StreamSupport.stream(varselInfoRepository.findAll().spliterator(), false).count()).isEqualTo(3);

		var varsler = dokumentInfoRepository.findDokumentInfoByDokumentId(DOKUMENT_ID_1).getVarselInfos();

		assertThat(varsler).anySatisfy(varsel -> {
			assertEquals(MOBILTELEFON, varsel.getVarslingKanal());
			assertNull(varsel.getVarslingstittel());
			assertEquals(VARSLINGSTEKST, varsel.getVarslingstekst());
			assertEquals(KONTAKTINFO_SMS, varsel.getMobiltelefonNummer());
			assertEquals(VARSEL_SENDT_DATO, varsel.getVarslingstidspunkt());
			assertNull(varsel.getEpostAdresse());
		});

		assertThat(varsler).anySatisfy(varsel -> {
			assertEquals(EPOST, varsel.getVarslingKanal());
			assertEquals(VARSLINGSTITTEL, varsel.getVarslingstittel());
			assertEquals(VARSLINGSTEKST, varsel.getVarslingstekst());
			assertEquals(KONTAKTINFO_EPOST, varsel.getEpostAdresse());
			assertEquals(VARSEL_SENDT_DATO, varsel.getVarslingstidspunkt());
			assertNull(varsel.getMobiltelefonNummer());
		});
		assertThat(varsler).anySatisfy(varsel -> {
			assertEquals(EPOST, varsel.getVarslingKanal());
			assertEquals(VARSLINGSTITTEL, varsel.getVarslingstittel());
			assertEquals(VARSLINGSTEKST, varsel.getVarslingstekst());
			assertEquals(EPOSTADDRESS, varsel.getEpostAdresse());
			assertEquals(SECOND_VARSEL_SENDT_DATO, varsel.getVarslingstidspunkt());
			assertNull(varsel.getMobiltelefonNummer());
		});
	}

	@Test
	void skalReturnereBadRequestDersomForsendelseIkkeEksisterer() {

		var request = createOppdaterVarselInfoRequest(123L);

		var response = webTestClient.put()
				.uri(OPPDATERVARSELINFO_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertNotNull(response);

		assertThat(response).containsSequence("Forsendelse med forsendelseId=123 ikke funnet");
	}

	@Test
	void skalReturnereBadRequestDersomForsendelseHarFeilDistribusjonskanal() {

		var distribusjon = setupDatabase(createDistribusjonInfoWithoutDokumentInfo());
		distribusjon.setDistribusjonKanal(DISTRIBUSJON_KANAL_PRINT);
		dokumentDistribusjonRepository.save(distribusjon);
		commitAndBeginNewTransaction();

		var forsendelseId = distribusjon.getDokumentInfos().iterator().next().getDokumentInfoId();
		var request = createOppdaterVarselInfoRequest(forsendelseId);

		var response = webTestClient.put()
				.uri(OPPDATERVARSELINFO_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertNotNull(response);
		assertThat(response).containsSequence(
				format("Forsendelse med forsendelseId=%s har ikke forventet distribusjonskanal DITTNAV", forsendelseId));
	}

	@ParameterizedTest
	@CsvSource(value = {
			"-1, EPOST, tekst, 95123456, tittel, 2023-02-22T11:20:26.024492, forsendelseId må være et positivt tall",
			"1, , tekst, 95123456, tittel, 2023-02-22T11:20:26.024492, kanal kan ikke være null",
			"1, EPOST, , 95123456, tittel, 2023-02-22T11:20:26.024492, tekst må inneholde minst ett tegn",
			"1, EPOST, tekst, , tittel, 2023-02-22T11:20:26.024492, kontaktInfo må innholde en epostadresse eller et telefonnummer",
			"1, MOBILTELEFON, tekst, 95123456, tittel, , varslingstidspunkt kan ikke være null",
	})
	void skalReturnereBadRequestForUgyldigInput(Long forsendelseId, String kanal, String tekst, String kontaktInfo, String tittel, LocalDateTime sendtDato, String feilmelding) {

		var kanalKode = isNull(kanal) ? null : VarslingKanalCode.valueOf(kanal);
		var request = createOppdaterVarselInfoRequestWith(forsendelseId, kanalKode, tekst, kontaktInfo, tittel, sendtDato);

		var response = webTestClient.put()
				.uri(OPPDATERVARSELINFO_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertEquals(feilmelding, response);
	}

	@Test
	void skalReturnereBadRequestForRequestUtenNotifikasjoner() {

		var request = createOppdaterVarselInfoRequest(1L);
		request.setNotifikasjoner(emptyList());

		var response = webTestClient.put()
				.uri(OPPDATERVARSELINFO_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		assertEquals("notifikasjoner må innehold minst en notifikasjon", response);
	}

	private OppdaterVarselInfoRequest createOppdaterVarselInfoRequestWith(Long forsendelseId, VarslingKanalCode varslingKanalCode, String tekst, String kontaktinfo, String tittel, LocalDateTime varslingstidspunkt) {

		return OppdaterVarselInfoRequest.builder()
				.forsendelseId(forsendelseId)
				.notifikasjoner(List.of(
						Notifikasjon.builder()
								.kanal(varslingKanalCode)
								.tekst(tekst)
								.kontaktInfo(kontaktinfo)
								.tittel(tittel)
								.varslingstidspunkt(varslingstidspunkt)
								.build()))
				.build();
	}

	private OppdaterVarselInfoRequest createOppdaterVarselInfoRequest(Long forsendelseId) {
		List<Notifikasjon> notifikasjons = new ArrayList<>();
		notifikasjons.add(Notifikasjon.builder()
				.kanal(MOBILTELEFON)
				.tekst(VARSLINGSTEKST)
				.kontaktInfo(KONTAKTINFO_SMS)
				.varslingstidspunkt(VARSEL_SENDT_DATO)
				.build());
		notifikasjons.add(
				Notifikasjon.builder()
						.kanal(EPOST)
						.tekst(VARSLINGSTEKST)
						.kontaktInfo(KONTAKTINFO_EPOST)
						.tittel(VARSLINGSTITTEL)
						.varslingstidspunkt(VARSEL_SENDT_DATO)
						.build());
		return OppdaterVarselInfoRequest.builder()
				.forsendelseId(forsendelseId)
				.notifikasjoner(notifikasjons)
				.build();
	}

	private Notifikasjon createNotifikasjon(VarslingKanalCode kanal, String kontaktInfo, String tittel, LocalDateTime varslingstidspunkt) {
		return Notifikasjon.builder()
				.kanal(kanal)
				.tekst(VARSLINGSTEKST)
				.kontaktInfo(kontaktInfo)
				.tittel(tittel)
				.varslingstidspunkt(varslingstidspunkt)
				.build();
	}

	public void commitAndBeginNewTransaction() {
		TestTransaction.flagForCommit();
		TestTransaction.end();
		TestTransaction.start();
	}
}
