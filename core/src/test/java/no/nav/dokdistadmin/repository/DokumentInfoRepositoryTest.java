package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.config.AbstractRepositoryTest;
import no.nav.dokdistadmin.domain.ArkivSystemCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
		var distribusjon = dokumentDistribusjonRepository.save(createDistribusjonInfo());

		DokumentInfo dokumentInfo = createDokumentInfo();
		dokumentInfo.setDistribusjonInfo(distribusjon);

		var result = dokumentInfoRepository.save(dokumentInfo);

		assertNotNull(result.getDokumentInfoId());
	}

	@Test
	void shouldFindDokumentInfo() {
		var distribusjon = dokumentDistribusjonRepository.save(createDistribusjonInfo());

		DokumentInfo dokumentInfo = createDokumentInfo();
		dokumentInfo.setDistribusjonInfo(distribusjon);
		dokumentInfoRepository.save(dokumentInfo);

		var result = dokumentInfoRepository.findDokumentInfoByDokumentId(dokumentInfo.getDokumentId());

		assertNotNull(result);
	}

	@Test
	void shouldFindDokumentInfoByDokumentInfoId() {
		var distribusjon = dokumentDistribusjonRepository.save(createDistribusjonInfo());

		DokumentInfo dokumentInfo = createDokumentInfo();
		dokumentInfo.setDistribusjonInfo(distribusjon);
		dokumentInfoRepository.save(dokumentInfo);

		var result = dokumentInfoRepository.findDokumentInfoByDokumentInfoId(dokumentInfo.getDokumentInfoId());

		assertNotNull(result);
	}

	@Test
	void shouldFindDokumentInfoByKonversasjonId() {
		var distribusjon = dokumentDistribusjonRepository.save(createDistribusjonInfo());

		DokumentInfo dokumentInfo = createDokumentInfo();
		dokumentInfo.setDistribusjonInfo(distribusjon);
		dokumentInfoRepository.save(dokumentInfo);

		var result = dokumentInfoRepository.findDokumentInfoByKonversasjonId(KONVERSASJON_ID);

		assertNotNull(result);
	}

	@Test
	void shouldFindDokumentInfoByJournalpostId() {
		var distribusjon = dokumentDistribusjonRepository.save(createDistribusjonInfo());

		DokumentInfo dokumentInfo = createDokumentInfo();
		dokumentInfo.setDistribusjonInfo(distribusjon);
		dokumentInfo.setArkivkode(JOURNALPOST_ID);
		dokumentInfoRepository.save(dokumentInfo);

		var result = dokumentInfoRepository.findDokumentInfoByArkivkode(JOURNALPOST_ID);

		assertNotNull(result);
	}

	@Test
	void shouldFindDokumentInfoByDokumentStatusAndDistribusjonKanal() {
		var distribusjon = TestUtils.createDistribusjonInfo();
		dokumentDistribusjonRepository.save(distribusjon);

		var dokumentInfos = Set.of(
				DokumentInfo.builder()
						.dokumentId(DOKUMENT_ID_1)
						.dokumentStatus(OPPRETTET)
						.distribusjonInfo(distribusjon)
						.build(),
				DokumentInfo.builder()
						.dokumentId(DOKUMENT_ID_2)
						.dokumentStatus(OVERSENDT)
						.distribusjonInfo(distribusjon)
						.build()
		);

		dokumentInfoRepository.saveAll(dokumentInfos);

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
		var distribusjonInfo = createDistribusjonInfo();
		dokumentDistribusjonRepository.save(distribusjonInfo);

		var dokumentInfoList = Set.of(
				DokumentInfo.builder()
						.dokumentId("1")
						.dokumentStatus(OVERSENDT)
						.arkivSystem(ArkivSystemCode.JOARK)
						.arkivkode(JOURNALPOST_ID)
						.ekspedertDato(GYLDIG_EKSPEDERTDATO)
						.distribusjonInfo(distribusjonInfo)
						.build(),
				DokumentInfo.builder()
						.dokumentId("2")
						.dokumentStatus(EKSPEDERT)
						.arkivSystem(ArkivSystemCode.JOARK)
						.arkivkode(JOURNALPOST_ID)
						.ekspedertDato(GYLDIG_EKSPEDERTDATO)
						.distribusjonInfo(distribusjonInfo)
						.build(),
				DokumentInfo.builder()
						.dokumentId("3")
						.dokumentStatus(EKSPEDERT)
						.arkivSystem(ArkivSystemCode.JOARK)
						.arkivkode(JOURNALPOST_ID)
						.ekspedertDato(UGYLDIG_EKSPEDERTDATO)
						.distribusjonInfo(distribusjonInfo)
						.build(),
				DokumentInfo.builder()
						.dokumentId("4")
						.dokumentStatus(EKSPEDERT)
						.arkivSystem(ArkivSystemCode.INGEN)
						.arkivkode(JOURNALPOST_ID)
						.ekspedertDato(GYLDIG_EKSPEDERTDATO)
						.distribusjonInfo(distribusjonInfo)
						.build(),
				DokumentInfo.builder()
						.dokumentId("5")
						.dokumentStatus(EKSPEDERT)
						.arkivSystem(ArkivSystemCode.JOARK)
						.arkivkode(JOURNALPOST_ID)
						.ekspedertDato(GYLDIG_EKSPEDERTDATO.plusDays(1))
						.distribusjonInfo(distribusjonInfo)
						.build()
		);

		dokumentInfoRepository.saveAll(dokumentInfoList);

		var result = dokumentInfoRepository.findEkspedertDokumentInfo(PageRequest.of(0, 1));

		assertEquals(1, result.getContent().size());
		assertEquals("2", result.getContent().get(0).getDokumentId());
	}
}