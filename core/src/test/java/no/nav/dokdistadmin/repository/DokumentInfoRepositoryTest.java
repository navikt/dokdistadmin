package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.config.AbstractRepositoryTest;
import no.nav.dokdistadmin.domain.ArkivSystemCode;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import org.junit.jupiter.api.Test;

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
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DokumentInfoRepositoryTest extends AbstractRepositoryTest {

	private static final String KONVERSASJON_ID = "879";
	private static final String JOURNALPOST_ID = "123456";
	private static final String AVSTEMT_REFERANSE = "MMA-1234";

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

		var dokumentInfoList = getDokumentInfoSet(distribusjonInfo);

		dokumentInfoRepository.saveAll(dokumentInfoList);

		var result = dokumentInfoRepository.findEkspedertDokumentInfo(1);

		assertThat(result).hasSize(1);
		assertThat(result).doesNotContainNull();

		List<DokumentInfo> dokumentInfos = dokumentInfoRepository.fetchEkspedertDokumentInfo(result);
		assertThat(dokumentInfos)
				.extracting(DokumentInfo::getDokumentId)
				.containsExactly("2");
	}

	@Test
	void shouldUpdateAllDokumentInfosInList() {
		var distribusjonInfo = createDistribusjonInfo();
		dokumentDistribusjonRepository.save(distribusjonInfo);

		var dokumentInfoList = getDokumentInfoSet(distribusjonInfo);
		dokumentInfoRepository.saveAll(dokumentInfoList);

		var idList = dokumentInfoList.stream()
				.map(DokumentInfo::getDokumentInfoId)
				.toList();

		dokumentInfoRepository.updateAvstemtReferanseAndAvstemtDatoForIdIn(AVSTEMT_REFERANSE, idList, USER_ID);

		commitAndBeginNewTransaction();

		var updatedDokumentInfoList = dokumentInfoRepository.findAllById(idList);

		assertThat(updatedDokumentInfoList)
				.allSatisfy(it -> {
					assertThat(it.getAvstemtReferanse()).isEqualTo(AVSTEMT_REFERANSE);
					assertThat(it.getAvstemtDato()).isNotNull();
				});
	}

	@Test
	void shouldOnlyUpdateDokumentInfoWithoutAvstemtReferanse() {
		var tidligereSattAvstemtReferanse = "MMA-0001";

		var distribusjonInfo = createDistribusjonInfo();
		dokumentDistribusjonRepository.save(distribusjonInfo);

		var dokumentInfoSkalOppdateres = createDokumentInfo();
		dokumentInfoSkalOppdateres.setDistribusjonInfo(distribusjonInfo);
		var dokumentInfoSkalIkkeOppdateres = createDokumentInfo();
		dokumentInfoSkalIkkeOppdateres.setDistribusjonInfo(distribusjonInfo);
		dokumentInfoSkalIkkeOppdateres.setAvstemtReferanse(tidligereSattAvstemtReferanse);

		dokumentInfoRepository.saveAll(List.of(dokumentInfoSkalOppdateres, dokumentInfoSkalIkkeOppdateres));

		var idList = List.of(dokumentInfoSkalOppdateres.getDokumentInfoId(), dokumentInfoSkalIkkeOppdateres.getDokumentInfoId());

		var result = dokumentInfoRepository.updateAvstemtReferanseAndAvstemtDatoForIdIn(AVSTEMT_REFERANSE, idList, USER_ID);

		assertEquals(1, result);

		commitAndBeginNewTransaction();

		var oppdatertDokumentInfo = dokumentInfoRepository.findDokumentInfoByDokumentInfoId(dokumentInfoSkalOppdateres.getDokumentInfoId());
		var ikkeOppdatertDokumentInfo = dokumentInfoRepository.findDokumentInfoByDokumentInfoId(dokumentInfoSkalIkkeOppdateres.getDokumentInfoId());

		assertEquals(AVSTEMT_REFERANSE, oppdatertDokumentInfo.getAvstemtReferanse());
		assertEquals(tidligereSattAvstemtReferanse, ikkeOppdatertDokumentInfo.getAvstemtReferanse());

		assertNotNull(oppdatertDokumentInfo.getAvstemtDato());
		assertNull(ikkeOppdatertDokumentInfo.getAvstemtDato());
	}

	@Test
	void shouldUpdateDokumentInfosAvstemtArkivDato() {
		DistribusjonInfo distribusjoninfo = dokumentDistribusjonRepository.save(createDistribusjonInfo());

		var dokumentInfoList = Set.of(
				DokumentInfo.builder()
						.dokumentId(DOKUMENT_ID_1)
						.dokumentStatus(DokumentStatusCode.OVERSENDT)
						.build(),
				DokumentInfo.builder()
						.dokumentId(DOKUMENT_ID_2)
						.dokumentStatus(DokumentStatusCode.OVERSENDT)
						.build()
		);

		dokumentInfoList.forEach(distribusjoninfo::addDokumentInfo);
		dokumentDistribusjonRepository.save(distribusjoninfo);

		var dokumentInfoIdList = distribusjoninfo.getDokumentInfos().stream()
				.map(DokumentInfo::getDokumentInfoId)
				.toList();

		dokumentInfoRepository.updateDokumentInfosAvstemtArkivDato(dokumentInfoIdList, USER_ID);
		commitAndBeginNewTransaction();

		var result = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjoninfo.getDistribusjonId());

		assertTrue(result.getDokumentInfos().stream().
				allMatch(it -> it.getAvstemtArkivDato() != null &&
						it.getChangeStamp().getEndretAv() != null &&
						it.getChangeStamp().getEndretDato() != null));
	}

	@Test
	void shouldUpdateStatusForAllDokumentInfosRelatedTo() {
		DistribusjonInfo distribusjoninfo = dokumentDistribusjonRepository.save(createDistribusjonInfo());

		var dokumentInfoList = Set.of(
				DokumentInfo.builder()
						.dokumentId(DOKUMENT_ID_1)
						.dokumentStatus(DokumentStatusCode.OPPRETTET)
						.build(),
				DokumentInfo.builder()
						.dokumentId(DOKUMENT_ID_2)
						.dokumentStatus(DokumentStatusCode.OPPRETTET)
						.build()
		);

		dokumentInfoList.forEach(distribusjoninfo::addDokumentInfo);
		dokumentDistribusjonRepository.save(distribusjoninfo);

		dokumentInfoRepository.updateStatusForAllDokumentInfosRelatedTo(distribusjoninfo, DokumentStatusCode.OVERSENDT, USER_ID);
		commitAndBeginNewTransaction();

		var result = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjoninfo.getDistribusjonId());

		assertTrue(result.getDokumentInfos().stream().
				allMatch(it -> it.getDokumentStatus() == DokumentStatusCode.OVERSENDT &&
						it.getChangeStamp().getEndretAv() != null &&
						it.getChangeStamp().getEndretDato() != null));
	}

	@Test
	void shouldUpdateStatusToEkspedertForAllDokumentInfosRelatedTo() {
		DistribusjonInfo distribusjoninfo = dokumentDistribusjonRepository.save(createDistribusjonInfo());

		var dokumentInfoList = Set.of(
				DokumentInfo.builder()
						.dokumentId(DOKUMENT_ID_1)
						.dokumentStatus(DokumentStatusCode.OPPRETTET)
						.build(),
				DokumentInfo.builder()
						.dokumentId(DOKUMENT_ID_2)
						.dokumentStatus(DokumentStatusCode.OPPRETTET)
						.build()
		);

		dokumentInfoList.forEach(distribusjoninfo::addDokumentInfo);
		dokumentDistribusjonRepository.save(distribusjoninfo);

		dokumentInfoRepository.updateStatusToEkspedertForAllDokumentInfosRelatedTo(distribusjoninfo, USER_ID);
		commitAndBeginNewTransaction();

		var result = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjoninfo.getDistribusjonId());

		assertTrue(result.getDokumentInfos().stream().allMatch(
				it -> it.getDokumentStatus() == DokumentStatusCode.EKSPEDERT &&
						it.getChangeStamp().getEndretAv() != null &&
						it.getChangeStamp().getEndretDato() != null));
	}

	private Set<DokumentInfo> getDokumentInfoSet(DistribusjonInfo distribusjonInfo) {
		return Set.of(
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

	}

}