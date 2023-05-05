package no.nav.dokdistadmin.administrerforsendelse;

import no.nav.dokdistadmin.administrerforsendelse.varselinfo.Notifikasjon;
import no.nav.dokdistadmin.administrerforsendelse.varselinfo.OppdaterVarselInfoRequest;
import no.nav.dokdistadmin.config.AbstractOauth2Test;
import no.nav.dokdistadmin.config.ApplicationTestConfig;
import no.nav.dokdistadmin.config.DatabaseTest;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.EPOSTADDRESS;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.FIRST_VARSEL_SENDT_DATO;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.SECOND_VARSEL_SENDT_DATO;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.TELEFONNUMMER;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.VARSELTEKST;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.VARSELTITTEL;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfoWithDistribusjonKanal;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.DITTNAV;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.EPOST;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.MOBILTELEFON;
import static no.nav.dokdistadmin.repository.TestUtils.DOKUMENT_ID_1;
import static no.nav.dokdistadmin.repository.TestUtils.createDokumentInfo;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Transactional
@SpringBootTest(
		classes = {ApplicationTestConfig.class},
		webEnvironment = RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles({"itest"})
public class VarselinfoITest extends AbstractOauth2Test implements DatabaseTest {

	private static final String OPPDATERVARSELINFO_URI = "/rest/v1/administrerforsendelse/oppdatervarselinfo";

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

	private DistribusjonInfo setupDatabase() {
		var distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfoWithDistribusjonKanal(DITTNAV));
		distribusjonInfo.addDokumentInfo(createDokumentInfo());
		dokumentDistribusjonRepository.save(distribusjonInfo);

		commitAndBeginNewTransaction();

		return distribusjonInfo;
	}

	@Test
	void skalOppdatereVarselinfo() {
		setupDatabase();
		var dokument = dokumentInfoRepository.findDokumentInfoByDokumentId(DOKUMENT_ID_1);

		var request = createOppdaterVarselInfoRequest(dokument.getDokumentInfoId());

		webTestClient.put()
				.uri(OPPDATERVARSELINFO_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk();

		assertThat(StreamSupport.stream(varselInfoRepository.findAll().spliterator(), false)).hasSize(2);

		var varsler = dokumentInfoRepository.findDokumentInfoByDokumentId(DOKUMENT_ID_1).getVarselInfos();

		assertThat(varsler).anySatisfy(varsel -> {
			assertThat(varsel.getVarslingKanal()).isEqualTo(MOBILTELEFON);
			assertNull(varsel.getVarslingstittel());
			assertThat(varsel.getVarslingstekst()).isEqualTo(VARSELTEKST);
			assertThat(varsel.getMobiltelefonNummer()).isEqualTo(TELEFONNUMMER);
			assertThat(varsel.getVarslingstidspunkt()).isCloseTo(FIRST_VARSEL_SENDT_DATO, within(1, ChronoUnit.SECONDS));
			assertNull(varsel.getEpostAdresse());
		});

		assertThat(varsler).anySatisfy(varsel -> {
			assertThat(varsel.getVarslingKanal()).isEqualTo(EPOST);
			assertThat(varsel.getVarslingstittel()).isEqualTo(VARSELTITTEL);
			assertThat(varsel.getVarslingstekst()).isEqualTo(VARSELTEKST);
			assertThat(varsel.getEpostAdresse()).isEqualTo(EPOSTADDRESS);
			assertThat(varsel.getVarslingstidspunkt()).isCloseTo(FIRST_VARSEL_SENDT_DATO, within(1, ChronoUnit.SECONDS));
			assertNull(varsel.getMobiltelefonNummer());
		});
	}

	@Test
	void skalOppdatereListOfVarselinfo() {

		setupDatabase();
		var dokument = dokumentInfoRepository.findDokumentInfoByDokumentId(DOKUMENT_ID_1);

		var request = createOppdaterVarselInfoRequest(dokument.getDokumentInfoId());
		List<Notifikasjon> notifikasjoner = request.getNotifikasjoner();
		notifikasjoner.add(createNotifikasjon(EPOST, Rdist001TestUtils.EPOSTADDRESS, VARSELTITTEL, SECOND_VARSEL_SENDT_DATO));

		webTestClient.put()
				.uri(OPPDATERVARSELINFO_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk();

		assertThat(StreamSupport.stream(varselInfoRepository.findAll().spliterator(), false).count()).isEqualTo(3);

		var varsler = dokumentInfoRepository.findDokumentInfoByDokumentId(DOKUMENT_ID_1).getVarselInfos();

		assertThat(varsler).anySatisfy(varsel -> {
			assertThat(varsel.getVarslingKanal()).isEqualTo(MOBILTELEFON);
			assertNull(varsel.getVarslingstittel());
			assertThat(varsel.getVarslingstekst()).isEqualTo(VARSELTEKST);
			assertThat(varsel.getMobiltelefonNummer()).isEqualTo(TELEFONNUMMER);
			assertThat(varsel.getVarslingstidspunkt()).isCloseTo(FIRST_VARSEL_SENDT_DATO, within(1, ChronoUnit.SECONDS));
			assertNull(varsel.getEpostAdresse());
		});

		assertThat(varsler).anySatisfy(varsel -> {
			assertThat(varsel.getVarslingKanal()).isEqualTo(EPOST);
			assertThat(varsel.getVarslingstittel()).isEqualTo(VARSELTITTEL);
			assertThat(varsel.getVarslingstekst()).isEqualTo(VARSELTEKST);
			assertThat(varsel.getEpostAdresse()).isEqualTo(EPOSTADDRESS);
			assertThat(varsel.getVarslingstidspunkt()).isCloseTo(FIRST_VARSEL_SENDT_DATO, within(1, ChronoUnit.SECONDS));
			assertNull(varsel.getMobiltelefonNummer());
		});
		assertThat(varsler).anySatisfy(varsel -> {
			assertThat(varsel.getVarslingKanal()).isEqualTo(EPOST);
			assertThat(varsel.getVarslingstittel()).isEqualTo(VARSELTITTEL);
			assertThat(varsel.getVarslingstekst()).isEqualTo(VARSELTEKST);
			assertThat(varsel.getEpostAdresse()).isEqualTo(Rdist001TestUtils.EPOSTADDRESS);
			assertThat(varsel.getVarslingstidspunkt()).isCloseTo(SECOND_VARSEL_SENDT_DATO, within(1, ChronoUnit.SECONDS));
			assertNull(varsel.getMobiltelefonNummer());
		});
	}

	@Test
	void skalGodtaVarslingstidspunktErNull_inntilVidere() {

		setupDatabase();
		var dokument = dokumentInfoRepository.findDokumentInfoByDokumentId(DOKUMENT_ID_1);

		var request = createOppdaterVarselInfoRequest(dokument.getDokumentInfoId());
		List<Notifikasjon> notifikasjoner = request.getNotifikasjoner();
		notifikasjoner.add(createNotifikasjon(EPOST, Rdist001TestUtils.EPOSTADDRESS, VARSELTITTEL, null));

		webTestClient.put()
				.uri(OPPDATERVARSELINFO_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk();

		assertThat(StreamSupport.stream(varselInfoRepository.findAll().spliterator(), false).count()).isEqualTo(3);

		var varsler = dokumentInfoRepository.findDokumentInfoByDokumentId(DOKUMENT_ID_1).getVarselInfos();

		assertThat(varsler).anySatisfy(varsel -> {
			assertThat(varsel.getVarslingKanal()).isEqualTo(MOBILTELEFON);
			assertNull(varsel.getVarslingstittel());
			assertThat(varsel.getVarslingstekst()).isEqualTo(VARSELTEKST);
			assertThat(varsel.getMobiltelefonNummer()).isEqualTo(TELEFONNUMMER);
			assertNull(varsel.getEpostAdresse());
		});

		assertThat(varsler).anySatisfy(varsel -> {
			assertThat(varsel.getVarslingKanal()).isEqualTo(EPOST);
			assertThat(varsel.getVarslingstittel()).isEqualTo(VARSELTITTEL);
			assertThat(varsel.getVarslingstekst()).isEqualTo(VARSELTEKST);
			assertThat(varsel.getEpostAdresse()).isEqualTo(EPOSTADDRESS);
			assertNull(varsel.getMobiltelefonNummer());
		});
		assertThat(varsler).anySatisfy(varsel -> {
			assertThat(varsel.getVarslingKanal()).isEqualTo(EPOST);
			assertThat(varsel.getVarslingstittel()).isEqualTo(VARSELTITTEL);
			assertThat(varsel.getVarslingstekst()).isEqualTo(VARSELTEKST);
			assertThat(varsel.getEpostAdresse()).isEqualTo(Rdist001TestUtils.EPOSTADDRESS);
			assertNull(varsel.getVarslingstidspunkt());
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

	@ParameterizedTest
	@CsvSource(value = {
			"-1, EPOST, tekst, 95123456, tittel, 2023-02-22T11:20:26.024492, forsendelseId må være et positivt tall",
			"1, , tekst, 95123456, tittel, 2023-02-22T11:20:26.024492, kanal kan ikke være null",
			"1, EPOST, , 95123456, tittel, 2023-02-22T11:20:26.024492, tekst må inneholde minst ett tegn",
			"1, EPOST, tekst, , tittel, 2023-02-22T11:20:26.024492, kontaktInfo må innholde en epostadresse eller et telefonnummer",
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

		assertThat(response).contains(feilmelding);
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

		assertThat(response).contains("notifikasjoner må inneholde minst en notifikasjon");
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
				.tekst(VARSELTEKST)
				.kontaktInfo(TELEFONNUMMER)
				.varslingstidspunkt(FIRST_VARSEL_SENDT_DATO)
				.build());
		notifikasjons.add(
				Notifikasjon.builder()
						.kanal(EPOST)
						.tekst(VARSELTEKST)
						.kontaktInfo(EPOSTADDRESS)
						.tittel(VARSELTITTEL)
						.varslingstidspunkt(FIRST_VARSEL_SENDT_DATO)
						.build());
		return OppdaterVarselInfoRequest.builder()
				.forsendelseId(forsendelseId)
				.notifikasjoner(notifikasjons)
				.build();
	}

	private Notifikasjon createNotifikasjon(VarslingKanalCode kanal, String kontaktInfo, String tittel, LocalDateTime varslingstidspunkt) {
		return Notifikasjon.builder()
				.kanal(kanal)
				.tekst(VARSELTEKST)
				.kontaktInfo(kontaktInfo)
				.tittel(tittel)
				.varslingstidspunkt(varslingstidspunkt)
				.build();
	}

}
