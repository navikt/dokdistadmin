package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.config.AbstractRepositoryTest;
import no.nav.dokdistadmin.domain.ArkivSystemCode;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.max;
import static java.util.List.of;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.DPO;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.TRYGDERETTEN;
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

class DokumentInfoRepositoryTest extends AbstractRepositoryTest {

	private static final String KONVERSASJON_ID = "879";
	private static final String JOURNALPOST_ID = "123456";
	private static final String AVSTEMT_REFERANSE = "MMA-1234";

	private static final LocalDateTime GYLDIG_EKSPEDERTDATO = LocalDateTime.of(2022, 10, 2, 0, 0);
	private static final LocalDateTime UGYLDIG_EKSPEDERTDATO = LocalDateTime.of(2022, 9, 30, 0, 0);

	@Test
	void shouldSaveDokumentInfo() {
		var distribusjon = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

		DokumentInfo dokumentInfo = createDokumentInfo();
		dokumentInfo.setDistribusjonInfo(distribusjon);

		var result = dokumentInfoRepository.persist(dokumentInfo);

		assertNotNull(result.getDokumentInfoId());
	}

	@Test
	void shouldSaveAndFindDokumentInfoWithForsendelseMetadata() throws URISyntaxException, IOException {
		var distribusjon = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

		DokumentInfo dokumentInfo = createDokumentInfo();
		var arkivmeldingXml = Files.readString(Paths.get(getClass().getClassLoader().getResource("forsendelsemetadata/arkivmelding.xml").toURI()));
		dokumentInfo.setForsendelseMetadata(arkivmeldingXml);

		dokumentInfo.setDistribusjonInfo(distribusjon);
		dokumentInfoRepository.persist(dokumentInfo);

		var result = dokumentInfoRepository.findDokumentInfoByDokumentId(dokumentInfo.getDokumentId());

		assertThat(result).isNotNull()
				.extracting(DokumentInfo::getForsendelseMetadata)
				.isEqualTo(arkivmeldingXml);
	}

	@Test
	void shouldFindDokumentInfo() {
		var distribusjon = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

		DokumentInfo dokumentInfo = createDokumentInfo();
		dokumentInfo.setDistribusjonInfo(distribusjon);
		dokumentInfoRepository.persist(dokumentInfo);

		var result = dokumentInfoRepository.findDokumentInfoByDokumentId(dokumentInfo.getDokumentId());

		assertNotNull(result);
	}

	@Test
	void shouldFindDokumentInfoByDokumentInfoId() {
		var distribusjon = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

		DokumentInfo dokumentInfo = createDokumentInfo();
		dokumentInfo.setDistribusjonInfo(distribusjon);
		dokumentInfoRepository.persist(dokumentInfo);

		var result = dokumentInfoRepository.findDokumentInfoByDokumentInfoId(dokumentInfo.getDokumentInfoId());

		assertNotNull(result);
	}

	@Test
	void shouldFindDokumentInfoByKonversasjonId() {
		var distribusjon = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

		DokumentInfo dokumentInfo = createDokumentInfo();
		dokumentInfo.setDistribusjonInfo(distribusjon);
		dokumentInfoRepository.persist(dokumentInfo);

		var result = dokumentInfoRepository.findDokumentInfoByKonversasjonId(KONVERSASJON_ID);

		assertNotNull(result);
	}

	@Test
	void shouldFindDokumentInfoByJournalpostId() {
		var distribusjon = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

		DokumentInfo dokumentInfo = createDokumentInfo();
		dokumentInfo.setDistribusjonInfo(distribusjon);
		dokumentInfo.setArkivkode(JOURNALPOST_ID);
		dokumentInfoRepository.persist(dokumentInfo);

		var result = dokumentInfoRepository.findDokumentInfoByArkivkode(JOURNALPOST_ID);

		assertNotNull(result);
	}

	@Test
	void shouldFindDokumentInfoByDokumentStatusAndDistribusjonKanal() {
		var distribusjon = TestUtils.createDistribusjonInfo();
		dokumentDistribusjonRepository.persist(distribusjon);

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

		dokumentInfoRepository.persistAll(dokumentInfos);

		var result = dokumentInfoRepository.findDokumentInfoByDokumentStatusAndDistribusjonKanalIn(
				EnumSet.of(OPPRETTET),
				EnumSet.of(PRINT));

		assertEquals(1, result.size());
		assertEquals(DOKUMENT_ID_1, result.getFirst().getDokumentId());
	}

	@Test
	void shouldFindDokumentInfoByDokumentStatusAndDistribusjonKanalIn() {
		var distribusjonPrint = TestUtils.createDistribusjonInfo();
		dokumentDistribusjonRepository.persist(distribusjonPrint);

		var distribusjonTrygderetten = TestUtils.createDistribusjonInfo();
		distribusjonTrygderetten.setDistribusjonKanal(TRYGDERETTEN);
		dokumentDistribusjonRepository.persist(distribusjonTrygderetten);

		var distribusjonDpo = TestUtils.createDistribusjonInfo();
		distribusjonDpo.setDistribusjonKanal(DPO);
		dokumentDistribusjonRepository.persist(distribusjonDpo);

		var dokumentInfos = Set.of(
				DokumentInfo.builder()
						.dokumentId(DOKUMENT_ID_1)
						.dokumentStatus(OVERSENDT)
						.distribusjonInfo(distribusjonPrint)
						.build(),
				DokumentInfo.builder()
						.dokumentId(DOKUMENT_ID_2)
						.dokumentStatus(OVERSENDT)
						.distribusjonInfo(distribusjonTrygderetten)
						.build(),
				DokumentInfo.builder()
						.dokumentId("3")
						.dokumentStatus(OPPRETTET)
						.distribusjonInfo(distribusjonTrygderetten)
						.build(),
				DokumentInfo.builder()
						.dokumentId("4")
						.dokumentStatus(OVERSENDT)
						.distribusjonInfo(distribusjonDpo)
						.build()
		);

		dokumentInfoRepository.persistAll(dokumentInfos);

		var result = dokumentInfoRepository.findDokumentInfoByDokumentStatusAndDistribusjonKanalIn(
				EnumSet.of(OVERSENDT),
				EnumSet.of(PRINT, TRYGDERETTEN));

		assertThat(result)
				.extracting(DokumentInfo::getDokumentId)
				.containsExactlyInAnyOrder(DOKUMENT_ID_1, DOKUMENT_ID_2);
	}

	@Test
	void shouldFindEkspedertDokumentInfo() {
		var distribusjonInfo = createDistribusjonInfo();
		dokumentDistribusjonRepository.persist(distribusjonInfo);

		var dokumentInfoList = getDokumentInfoSet(distribusjonInfo);

		dokumentInfoRepository.persistAll(dokumentInfoList);

		var result = dokumentInfoRepository.findEkspedertDokumentInfo(1, EnumSet.of(PRINT));

		assertThat(result).hasSize(1).doesNotContainNull();

		List<DokumentInfo> dokumentInfos = dokumentInfoRepository.fetchEkspedertDokumentInfo(result);
		assertThat(dokumentInfos)
				.extracting(DokumentInfo::getDokumentId)
				.containsExactly("2");
	}

	@Test
	void shouldUpdateAllDokumentInfosInList() {
		var distribusjonInfo = createDistribusjonInfo();
		dokumentDistribusjonRepository.persist(distribusjonInfo);

		var dokumentInfoList = getDokumentInfoSet(distribusjonInfo);
		dokumentInfoRepository.persistAll(dokumentInfoList);

		var idList = dokumentInfoList.stream()
				.map(DokumentInfo::getDokumentInfoId)
				.toList();

		dokumentInfoRepository.updateAvstemtReferanseAndAvstemtDatoForIdIn(AVSTEMT_REFERANSE, idList, USER_ID);

		commitAndBeginNewTransaction();

		var updatedDokumentInfoList = dokumentInfoRepository.findAllById(idList);

		assertThat(updatedDokumentInfoList)
				.isNotNull()
				.allSatisfy(it -> {
					assertThat(it.getAvstemtReferanse()).isEqualTo(AVSTEMT_REFERANSE);
					assertThat(it.getAvstemtDato()).isNotNull();
				});
	}

	@Test
	void shouldOnlyUpdateDokumentInfoWithoutAvstemtReferanse() {
		var tidligereSattAvstemtReferanse = "MMA-0001";

		var distribusjonInfo = createDistribusjonInfo();
		dokumentDistribusjonRepository.persist(distribusjonInfo);

		var dokumentInfoSkalOppdateres = createDokumentInfo();
		dokumentInfoSkalOppdateres.setDistribusjonInfo(distribusjonInfo);
		var dokumentInfoSkalIkkeOppdateres = createDokumentInfo();
		dokumentInfoSkalIkkeOppdateres.setDistribusjonInfo(distribusjonInfo);
		dokumentInfoSkalIkkeOppdateres.setAvstemtReferanse(tidligereSattAvstemtReferanse);

		dokumentInfoRepository.persistAll(of(dokumentInfoSkalOppdateres, dokumentInfoSkalIkkeOppdateres));

		var idList = of(dokumentInfoSkalOppdateres.getDokumentInfoId(), dokumentInfoSkalIkkeOppdateres.getDokumentInfoId());

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
		DistribusjonInfo distribusjoninfo = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

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
		dokumentDistribusjonRepository.persist(distribusjoninfo);

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
		DistribusjonInfo distribusjoninfo = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

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
		dokumentDistribusjonRepository.persist(distribusjoninfo);

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
		DistribusjonInfo distribusjoninfo = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

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
		dokumentDistribusjonRepository.persist(distribusjoninfo);

		dokumentInfoRepository.updateStatusToEkspedertForAllDokumentInfosRelatedTo(distribusjoninfo, USER_ID);
		commitAndBeginNewTransaction();

		var result = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjoninfo.getDistribusjonId());

		assertTrue(result.getDokumentInfos().stream().allMatch(
				it -> it.getDokumentStatus() == DokumentStatusCode.EKSPEDERT &&
						it.getChangeStamp().getEndretAv() != null &&
						it.getChangeStamp().getEndretDato() != null));
	}

	@Test
	void shouldFindByDokumentId() {
		var distribusjon = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

		DokumentInfo dokumentInfo = createDokumentInfo();
		dokumentInfo.setDistribusjonInfo(distribusjon);
		dokumentInfoRepository.persist(dokumentInfo);

		var result = dokumentInfoRepository.findIdsByDokumentId(dokumentInfo.getDokumentId());

		Assertions.assertThat(result)
				.singleElement()
				.satisfies(it -> assertThat(it.getDokumentInfoId()).isEqualTo(dokumentInfo.getDokumentInfoId()));
	}

	@Test
	void shouldFindByKonversasjonId() {
		var distribusjon = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

		DokumentInfo dokumentInfo = createDokumentInfo();
		dokumentInfo.setDistribusjonInfo(distribusjon);
		dokumentInfoRepository.persist(dokumentInfo);

		var result = dokumentInfoRepository.findIdsByKonversasjonId(KONVERSASJON_ID);

		Assertions.assertThat(result)
				.singleElement()
				.satisfies(it -> assertThat(it.getDokumentInfoId()).isEqualTo(dokumentInfo.getDokumentInfoId()));
	}

	@Test
	void shouldFindByJournalpostId() {
		var distribusjon = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

		DokumentInfo dokumentInfo = createDokumentInfo();
		dokumentInfo.setDistribusjonInfo(distribusjon);
		dokumentInfo.setArkivkode(JOURNALPOST_ID);
		dokumentInfoRepository.persist(dokumentInfo);

		var result = dokumentInfoRepository.findTopByArkivkodeOrderByDokumentInfoIdDesc(JOURNALPOST_ID);

		Assertions.assertThat(result)
				.isNotNull()
				.satisfies(it -> assertThat(it.getDokumentInfoId()).isEqualTo(dokumentInfo.getDokumentInfoId()));
	}

	@Test
	void shouldFindTopByJournalpostId() {
		var distribusjon = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

		DokumentInfo dokumentInfo1 = createDokumentInfo();
		dokumentInfo1.setDistribusjonInfo(distribusjon);
		dokumentInfo1.setArkivkode(JOURNALPOST_ID);
		dokumentInfoRepository.persist(dokumentInfo1);

		DokumentInfo dokumentInfo2 = createDokumentInfo();
		dokumentInfo2.setDistribusjonInfo(distribusjon);
		dokumentInfo2.setArkivkode(JOURNALPOST_ID);
		dokumentInfoRepository.persist(dokumentInfo2);

		var expected = max(of(dokumentInfo1.getDokumentInfoId(), dokumentInfo2.getDokumentInfoId()));

		var result = dokumentInfoRepository.findTopByArkivkodeOrderByDokumentInfoIdDesc(JOURNALPOST_ID);

		Assertions.assertThat(result)
				.isNotNull()
				.satisfies(it -> assertThat(it.getDokumentInfoId()).isEqualTo(expected));
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