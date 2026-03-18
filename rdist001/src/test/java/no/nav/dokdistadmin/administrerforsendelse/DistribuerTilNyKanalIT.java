package no.nav.dokdistadmin.administrerforsendelse;

import jakarta.jms.Queue;
import no.nav.dokdistadmin.administrerforsendelse.distribuertilnykanal.DistribuerTilNyKanalRequest;
import no.nav.dokdistadmin.config.AbstractITest;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentReferanse;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.utils.TestDatabaseCleanup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import static java.util.concurrent.TimeUnit.SECONDS;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentReferanseWithRefererTilAndRekkefoelge;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static no.nav.dokdistadmin.domain.DistribusjonStatusCode.FEILET;
import static no.nav.dokdistadmin.domain.DistribusjonStatusCode.KLAR_FOR_DIST;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OPPRETTET;
import static no.nav.dokdistadmin.domain.FeilTypeCode.VARSLINGSFEIL;
import static no.nav.dokdistadmin.domain.RefererTilCode.HOVEDDOKUMENT;
import static no.nav.dokdistadmin.domain.RefererTilCode.VEDLEGG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.awaitility.Awaitility.await;


public class DistribuerTilNyKanalIT extends AbstractITest {

	private static final String DISTRIBUER_TIL_NY_KANAL_URI = "/rest/v1/administrerforsendelse/distribuertilnykanal";
	private static final String TEST_ARSAK_BESKRIVELSE = "Test arsak beskrivelse";

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Queue printQueue;

	@Autowired
	private Queue dittnavQueue;

	@Autowired
	private Queue sdpQueue;

	@Autowired
	private Queue dpvtQueue;

	@Autowired
	private TestDatabaseCleanup testDatabaseCleanup;

	@AfterEach
	public void afterEach() {
		testDatabaseCleanup.execute();
	}

	@ParameterizedTest
	@EnumSource(value = DistribusjonKanalCode.class, names = {"PRINT", "DITTNAV", "SDP", "DPVT"})
	public void skalDistribuereTilKanal(DistribusjonKanalCode kanal) {
		var distribusjonInfo = setupDatabase();
		long forsendelseId = distribusjonInfo.getDokumentInfos().iterator().next().getDokumentInfoId();

		DistribuerTilNyKanalRequest request = DistribuerTilNyKanalRequest.builder()
				.forsendelseId(forsendelseId)
				.kanal(kanal.name())
				.arsak(VARSLINGSFEIL.name())
				.arsakBeskrivelse(TEST_ARSAK_BESKRIVELSE)
				.build();

		webTestClient.post()
				.uri(DISTRIBUER_TIL_NY_KANAL_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus()
				.isOk();

		var originalDistribusjon = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjonInfo.getDistribusjonId());
		var nyDistribusjon = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(originalDistribusjon.getResendingDistribusjonId());

		assertThat(originalDistribusjon)
				.extracting(
						DistribusjonInfo::getDistribusjonId,
						DistribusjonInfo::getDistribusjonStatus,
						DistribusjonInfo::getResendingDistribusjonId
				)
				.containsExactly(distribusjonInfo.getDistribusjonId(), FEILET, nyDistribusjon.getDistribusjonId());

		assertThat(nyDistribusjon)
				.extracting(
						DistribusjonInfo::getDistribusjonStatus,
						DistribusjonInfo::getDistribusjonKanal,
						DistribusjonInfo::getOriginalDistribusjonId
				)
				.containsExactly(KLAR_FOR_DIST, kanal, originalDistribusjon.getDistribusjonId());

		var originalDokument = originalDistribusjon.getDokumentInfos().iterator().next();
		var nyttDokument = nyDistribusjon.getDokumentInfos().iterator().next();

		assertThat(nyttDokument.getDokumentReferanses())
				.hasSize(2)
				.extracting(
						DokumentReferanse::getRefererTil,
						DokumentReferanse::getRekkefolge
				)
				.containsExactlyInAnyOrder(
						tuple(HOVEDDOKUMENT, 1),
						tuple(VEDLEGG, 2)
				);

		assertThat(dokumentInfoRepository.findDokumentInfoByDokumentInfoId(originalDokument.getDokumentInfoId()).getFeilkvitterings())
				.singleElement()
				.satisfies(f -> {
					assertThat(f.getFeiltype()).isEqualTo(VARSLINGSFEIL);
					assertThat(f.getDetaljer()).isEqualTo(TEST_ARSAK_BESKRIVELSE);
					assertThat(f.getDokumentInfo().getDokumentId()).isEqualTo(originalDokument.getDokumentId());
				});

		assertQueue(kanal, nyttDokument);
	}

	private void assertQueue(DistribusjonKanalCode kanal, DokumentInfo nyttDokument) {
		Queue queue = switch (kanal) {
			case PRINT -> printQueue;
			case DITTNAV -> dittnavQueue;
			case SDP -> sdpQueue;
			case DPVT -> dpvtQueue;
			default -> throw new IllegalArgumentException("Ukjent kanal: " + kanal);
		};

		await().atMost(5, SECONDS).untilAsserted(() -> {
			String message = (String) jmsTemplate.receiveAndConvert(queue);
			assertThat(message)
					.isNotNull()
					.contains("<forsendelseId>%s</forsendelseId>".formatted(nyttDokument.getDokumentInfoId()));
		});
	}

	@ParameterizedTest
	@CsvSource(value = {
			"UGYLDIG_KANAL, VARSLINGSFEIL, Ugyldig kanal 'UGYLDIG_KANAL'",
			"PRINT, UGYLDIG_FEILTYPE, Ugyldig årsak 'UGYLDIG_FEILTYPE'"
	})
	public void skalReturnereBadRequestVedUgyldigInput(String kanal, String feilType, String feilmelding) {
		var distribusjonInfo = setupDatabase();
		long forsendelseId = distribusjonInfo.getDokumentInfos().iterator().next().getDokumentInfoId();

		DistribuerTilNyKanalRequest request = DistribuerTilNyKanalRequest.builder()
				.forsendelseId(forsendelseId)
				.kanal(kanal)
				.arsak(feilType)
				.arsakBeskrivelse(TEST_ARSAK_BESKRIVELSE)
				.build();

		webTestClient.post()
				.uri(DISTRIBUER_TIL_NY_KANAL_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus()
				.isBadRequest()
				.expectBody(String.class)
				.value(body ->
					assertThat(body).contains(feilmelding)
				);
	}

	@Test
	public void skalReturnereNotFoundNaarForsendelseIkkeFinnes() {
		DistribuerTilNyKanalRequest request = DistribuerTilNyKanalRequest.builder()
				.forsendelseId(999999L)
				.kanal(PRINT.name())
				.arsak(VARSLINGSFEIL.name())
				.arsakBeskrivelse(DistribuerTilNyKanalIT.TEST_ARSAK_BESKRIVELSE)
				.build();

		webTestClient.post()
				.uri(DISTRIBUER_TIL_NY_KANAL_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus()
				.isNotFound()
				.expectBody(String.class)
				.isEqualTo("\"Forsendelse med forsendelseId=%s ikke funnet i dokdistDb\"".formatted(request.getForsendelseId()));
	}

	@Test
	public void skalReturnereBadRequestForsendelseHarStatusEkspedert() {
		var distribusjonInfo = setupDatabase(EKSPEDERT);
		long forsendelseId = distribusjonInfo.getDokumentInfos().iterator().next().getDokumentInfoId();

		DistribuerTilNyKanalRequest request = DistribuerTilNyKanalRequest.builder()
				.forsendelseId(forsendelseId)
				.kanal(PRINT.name())
				.arsak(VARSLINGSFEIL.name())
				.arsakBeskrivelse(TEST_ARSAK_BESKRIVELSE)
				.build();

		webTestClient.post()
				.uri(DISTRIBUER_TIL_NY_KANAL_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus()
				.isBadRequest()
				.expectBody(String.class)
				.value(body ->
					assertThat(body).contains("Forsendelsen har status '%s' og kan ikke distribueres til ny kanal.".formatted(EKSPEDERT.name()))
				);
	}

	private DistribusjonInfo setupDatabase() {
		return setupDatabase(OPPRETTET);
	}

	private DistribusjonInfo setupDatabase(DokumentStatusCode dokumentStatus) {
		var distribusjonInfo = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

		var dokumentInfo = createDokumentInfo();
		dokumentInfo.setDokumentStatus(dokumentStatus);
		dokumentInfo.addDokumentReferanse(createDokumentReferanseWithRefererTilAndRekkefoelge(HOVEDDOKUMENT, 1));
		dokumentInfo.addDokumentReferanse(createDokumentReferanseWithRefererTilAndRekkefoelge(VEDLEGG, 2));
		distribusjonInfo.addDokumentInfo(dokumentInfo);
		dokumentDistribusjonRepository.persist(distribusjonInfo);

		commitAndBeginNewTransaction();

		return distribusjonInfo;
	}
}
