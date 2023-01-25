package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.config.AbstractRepositoryTest;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.FEILET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.RETURPOSTBEHANDLET;
import static no.nav.dokdistadmin.repository.TestUtils.DISTRIBUSJON_ID;
import static no.nav.dokdistadmin.repository.TestUtils.DOKUMENT_ID_1;
import static no.nav.dokdistadmin.repository.TestUtils.DOKUMENT_ID_2;
import static no.nav.dokdistadmin.repository.TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DokumentDistribusjonRepositoryTest extends AbstractRepositoryTest {

	private static final int OPPRETTET_ANTALL_DAGER_SIDEN = 60;

	@Test
	public void shouldSaveDistribusjonInfo() {
		var distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfo());

		assertNotNull(distribusjonInfo.getDistribusjonInfoId());
	}

	@Test
	public void shouldUpdateDistribusjonInfo() {
		var distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfo());
		distribusjonInfo.setDistribusjonStatus(DistribusjonStatusCode.OVERSENDT);
		dokumentDistribusjonRepository.save(distribusjonInfo);

		var updated = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjonInfo.getDistribusjonId());

		assertEquals(DistribusjonStatusCode.OVERSENDT, updated.getDistribusjonStatus());

	}

	@Test
	public void shouldFindDistribusjonInfoByDistribusjonId() {
		var distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfo());
		dokumentDistribusjonRepository.save(distribusjonInfo);

		var result = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjonInfo.getDistribusjonId());

		assertNotNull(result);
		assertEquals(distribusjonInfo.getDistribusjonInfoId(), result.getDistribusjonInfoId());
		assertEquals(DISTRIBUSJON_ID, result.getDistribusjonId());
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

		dokumentDistribusjonRepository.updateDokumentInfosAvstemtArkivDato(dokumentInfoIdList, USER_ID);
		commitAndBeginNewTransaction();

		var result = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjoninfo.getDistribusjonId());

		assertTrue(result.getDokumentInfos().stream().
				allMatch(it -> it.getAvstemtArkivDato() != null &&
						it.getChangeStamp().getEndretAv() != null &&
						it.getChangeStamp().getEndretDato() != null &&
						it.getVersion() == 1));
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

		dokumentDistribusjonRepository.updateStatusForAllDokumentInfosRelatedTo(distribusjoninfo, DokumentStatusCode.OVERSENDT, USER_ID);
		commitAndBeginNewTransaction();

		var result = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjoninfo.getDistribusjonId());

		assertTrue(result.getDokumentInfos().stream().
				allMatch(it -> it.getDokumentStatus() == DokumentStatusCode.OVERSENDT &&
						it.getChangeStamp().getEndretAv() != null &&
						it.getChangeStamp().getEndretDato() != null &&
						it.getVersion() == 1));
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

		dokumentDistribusjonRepository.updateStatusToEkspedertForAllDokumentInfosRelatedTo(distribusjoninfo, USER_ID);
		commitAndBeginNewTransaction();

		var result = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjoninfo.getDistribusjonId());

		assertTrue(result.getDokumentInfos().stream().allMatch(
				it -> it.getDokumentStatus() == DokumentStatusCode.EKSPEDERT &&
						it.getChangeStamp().getEndretAv() != null &&
						it.getChangeStamp().getEndretDato() != null &&
						it.getVersion() == 1));
	}

	@Test
	public void shouldFindDistribusjonInfoByDokumentStatusAndDistribusjonKanalWithRightAge() {
		//Opprett en distribusjonInfo med distribution_datetime = now
		DistribusjonInfo distribusjoninfo = dokumentDistribusjonRepository.save(createDistribusjonInfo());

		distribusjoninfo.addDokumentInfo(DokumentInfo.builder()
				.dokumentId(DOKUMENT_ID_1)
				.dokumentStatus(DokumentStatusCode.OPPRETTET)
				.build());
		distribusjoninfo.addDokumentInfo(DokumentInfo.builder()
				.dokumentId(DOKUMENT_ID_2)
				.dokumentStatus(DokumentStatusCode.OPPRETTET)
				.build());
		dokumentDistribusjonRepository.save(distribusjoninfo);

		commitAndBeginNewTransaction();

		//Hent alle distribusjonIder som er eldre enn en time
		LocalDateTime etterAntallDagerSiden = LocalDateTime.now().minusDays(OPPRETTET_ANTALL_DAGER_SIDEN);
		LocalDateTime foerAntallTimerSiden = LocalDateTime.now().minusHours(1L);
		List<DistribusjonInfo> result = dokumentDistribusjonRepository.findDistribusjonInfoByDokumentStatusAndDistribusjonKanal(
				EnumSet.of(EKSPEDERT, FEILET, RETURPOSTBEHANDLET), PRINT, etterAntallDagerSiden, foerAntallTimerSiden);

		assertTrue(result.isEmpty());

		//Hent alle distribusjonIder uten begrensning på opprettetDato
		foerAntallTimerSiden = LocalDateTime.now().minusHours(0L);
		result = dokumentDistribusjonRepository.findDistribusjonInfoByDokumentStatusAndDistribusjonKanal(
				EnumSet.of(EKSPEDERT, FEILET, RETURPOSTBEHANDLET), PRINT, etterAntallDagerSiden, foerAntallTimerSiden);

		assertEquals(1, result.size());
	}
}

