package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.config.AbstractRepositoryTest;
import no.nav.dokdistadmin.domain.ArkivSystemCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.builder.DokumentInfoBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OPPRETTET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OVERSENDT;
import static no.nav.dokdistadmin.repository.TestUtils.DOKUMENT_ID_1;
import static no.nav.dokdistadmin.repository.TestUtils.DOKUMENT_ID_2;
import static no.nav.dokdistadmin.repository.TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.repository.TestUtils.createDokumentInfo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DokumentInfoRepositoryTest extends AbstractRepositoryTest {

	private static final String KONVERSASJON_ID = "879";
	private static final String JOURNALPOST_ID = "123456";

	private static final LocalDateTime GYLDIG_EKSPEDERTDATO = LocalDateTime.of(2022, 10, 2, 0, 0);
	private static final LocalDateTime UGYLDIG_EKSPEDERTDATO = LocalDateTime.of(2022, 9, 30, 0, 0);

	@Test
	public void shouldSaveDokumentInfo() {
		var distribusjon = dokumentDistribusjonRepository.save(createDistribusjonInfo("").build());

		DokumentInfo dokumentInfo = DokumentInfoBuilder.with()
				.dokumentId(DOKUMENT_ID_1)
				.dokumentStatus(OPPRETTET)
				.build();
		dokumentInfo.setDistribusjonInfo(distribusjon);

		var result = dokumentInfoRepository.save(dokumentInfo);

		assertNotNull(result.getDokumentInfoId());
	}

	@Test
	void shouldFindDokumentInfo() {
		var distribusjon = dokumentDistribusjonRepository.save(createDistribusjonInfo("").build());
		commitAndBeginNewTransaction();

		DokumentInfo dokumentInfo = DokumentInfoBuilder.with()
				.dokumentId(DOKUMENT_ID_1)
				.dokumentStatus(OPPRETTET)
				.build();
		dokumentInfo.setDistribusjonInfo(distribusjon);
		dokumentInfoRepository.save(dokumentInfo);

		var result = dokumentInfoRepository.findDokumentInfoByDokumentId(dokumentInfo.getDokumentId());

		assertNotNull(result);
	}

	@Test
	void shouldFindDokumentInfoByDokumentInfoId() {
		var distribusjon = dokumentDistribusjonRepository.save(createDistribusjonInfo("").build());
		commitAndBeginNewTransaction();

		DokumentInfo dokumentInfo = DokumentInfoBuilder.with()
				.dokumentId(DOKUMENT_ID_1)
				.dokumentStatus(OPPRETTET)
				.build();
		dokumentInfo.setDistribusjonInfo(distribusjon);
		var savedDokumentInfo = dokumentInfoRepository.save(dokumentInfo);

		var result = dokumentInfoRepository.findDokumentInfoByDokumentInfoId(savedDokumentInfo.getDokumentInfoId());

		assertNotNull(result);
	}

	@Test
	void shouldFindDokumentInfoByKonversasjonId() {
		var distribusjon = dokumentDistribusjonRepository.save(createDistribusjonInfo("").build());
		commitAndBeginNewTransaction();

		DokumentInfo dokumentInfo = DokumentInfoBuilder.with()
				.dokumentId(DOKUMENT_ID_1)
				.dokumentStatus(OPPRETTET)
				.konversasjonsId(KONVERSASJON_ID)
				.build();
		dokumentInfo.setDistribusjonInfo(distribusjon);
		dokumentInfoRepository.save(dokumentInfo);

		var result = dokumentInfoRepository.findDokumentInfoByKonversasjonId(KONVERSASJON_ID);

		assertNotNull(result);
	}

	@Test
	void shouldFindDokumentInfoByJournalpostId() {
		var distribusjon = dokumentDistribusjonRepository.save(createDistribusjonInfo("").build());
		commitAndBeginNewTransaction();

		DokumentInfo dokumentInfo = DokumentInfoBuilder.with()
				.dokumentId(DOKUMENT_ID_1)
				.dokumentStatus(OPPRETTET)
				.arkivkode(JOURNALPOST_ID)
				.build();
		dokumentInfo.setDistribusjonInfo(distribusjon);
		dokumentInfoRepository.save(dokumentInfo);

		var result = dokumentInfoRepository.findDokumentInfoByArkivkode(JOURNALPOST_ID);

		assertNotNull(result);
	}

	@Test
	void shouldFindDokumentInfoByDokumentStatusAndDistribusjonKanal() {
		var distribusjon = createDistribusjonInfo("").dokumentInfos(
				DokumentInfoBuilder.with()
						.dokumentId(DOKUMENT_ID_1)
						.dokumentStatus(OPPRETTET)
						.build(),
				DokumentInfoBuilder.with()
						.dokumentId(DOKUMENT_ID_2)
						.dokumentStatus(OVERSENDT)
						.build()
		).build();
		dokumentDistribusjonRepository.save(distribusjon);

		var result = dokumentInfoRepository.findDokumentInfoByDokumentStatusAndDistribusjonKanal(
				List.of(OPPRETTET),
				PRINT,
				LocalDateTime.now().minusHours(1L));

		assertEquals(1, result.size());
		assertEquals(DOKUMENT_ID_1, result.get(0).getDokumentId());

		assertNotNull(result);
	}

	@Test
	public void shouldFindEkspedertDokumentInfo() {
		dokumentDistribusjonRepository.save(createDistribusjonInfo("")
				.dokumentInfos(
						createDokumentInfo("1")
								.dokumentStatus(OVERSENDT)
								.arkivSystem(ArkivSystemCode.JOARK)
								.arkivkode(JOURNALPOST_ID)
								.ekspedertDato(GYLDIG_EKSPEDERTDATO)
								.build(),
						createDokumentInfo("2")
								.dokumentStatus(EKSPEDERT)
								.arkivSystem(ArkivSystemCode.JOARK)
								.arkivkode(JOURNALPOST_ID)
								.ekspedertDato(GYLDIG_EKSPEDERTDATO)
								.build(),
						createDokumentInfo("3")
								.dokumentStatus(EKSPEDERT)
								.arkivSystem(ArkivSystemCode.JOARK)
								.arkivkode(JOURNALPOST_ID)
								.ekspedertDato(UGYLDIG_EKSPEDERTDATO)
								.build(),
						createDokumentInfo("4")
								.dokumentStatus(EKSPEDERT)
								.arkivSystem(ArkivSystemCode.INGEN)
								.arkivkode(JOURNALPOST_ID)
								.ekspedertDato(GYLDIG_EKSPEDERTDATO)
								.build(),
						createDokumentInfo("5")
								.dokumentStatus(EKSPEDERT)
								.arkivSystem(ArkivSystemCode.JOARK)
								.arkivkode(JOURNALPOST_ID)
								.ekspedertDato(GYLDIG_EKSPEDERTDATO.plusDays(1))
								.build()
				).build());

		var result = dokumentInfoRepository.findEkspedertDokumentInfo(PageRequest.of(0, 1));

		assertEquals(1, result.getContent().size());
		assertEquals("2", result.getContent().get(0).getDokumentId());
	}
}