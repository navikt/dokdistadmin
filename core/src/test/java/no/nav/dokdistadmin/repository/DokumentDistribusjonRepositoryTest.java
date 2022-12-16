package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.config.AbstractRepositoryTest;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.builder.DokumentInfoBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.FEILET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.RETURPOSTBEHANDLET;
import static no.nav.dokdistadmin.repository.TestUtils.DISTRIBUSJON_ID;
import static no.nav.dokdistadmin.repository.TestUtils.DOKUMENT_ID_2;
import static no.nav.dokdistadmin.repository.TestUtils.DOKUMENT_ID_3;
import static no.nav.dokdistadmin.repository.TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.repository.TestUtils.createDistribusjonInfoWithDokumentInfo;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DokumentDistribusjonRepositoryTest extends AbstractRepositoryTest {

	private static final int OPPRETTET_ANTALL_DAGER_SIDEN = 60;

	@Test
	public void shouldSaveDistribusjonInfo() {
		var distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfo().build());

		assertNotNull(distribusjonInfo.getDistribusjonInfoId());
	}

	@Test
	public void shouldUpdateDistribusjonInfo() {
		var distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfoWithDokumentInfo().build());
		distribusjonInfo.setDistribusjonStatus(DistribusjonStatusCode.OVERSENDT);
		commitTransaction();

		var updated = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjonInfo.getDistribusjonId());

		Assertions.assertEquals(DistribusjonStatusCode.OVERSENDT, updated.getDistribusjonStatus());

	}

	@Test
	public void shouldFindDistribusjonInfoByDistribusjonId() {
		var distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfo().build());
		commitTransaction();

		var result = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjonInfo.getDistribusjonId());

		assertNotNull(result);
		Assertions.assertEquals(distribusjonInfo.getDistribusjonInfoId(), result.getDistribusjonInfoId());
		Assertions.assertEquals(DISTRIBUSJON_ID, result.getDistribusjonId());
	}


	@Test
	void shouldUpdateDokumentInfosAvstemtArkivDato() {
		DistribusjonInfo distribusjoninfo = createDistribusjonInfo().dokumentInfos(
				DokumentInfoBuilder.with()
						.dokumentId(DOKUMENT_ID_2)
						.dokumentStatus(DokumentStatusCode.OVERSENDT)
						.build(),
				DokumentInfoBuilder.with()
						.dokumentId(DOKUMENT_ID_3)
						.dokumentStatus(DokumentStatusCode.OVERSENDT)
						.build()
		).build();
		dokumentDistribusjonRepository.save(distribusjoninfo);
		commitAndBeginNewTransaction();

		var dokumentInfoIdList = distribusjoninfo.getDokumentInfos().stream()
				.map(DokumentInfo::getDokumentInfoId)
				.toList();

		dokumentDistribusjonRepository.updateDokumentInfosAvstemtArkivDato(dokumentInfoIdList, USER_ID);
		commitAndBeginNewTransaction();

		var result = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjoninfo.getDistribusjonId());

		Assertions.assertTrue(result.getDokumentInfos().stream().
				allMatch(it -> it.getAvstemtArkivDato() != null && it.getChangeStamp().getEndretAv() != null));
	}

	@Test
	void shouldUpdateStatusForAllDokumentInfosRelatedTo() {
		DistribusjonInfo distribusjoninfo = createDistribusjonInfo().dokumentInfos(
				DokumentInfoBuilder.with()
						.dokumentId(DOKUMENT_ID_2)
						.dokumentStatus(DokumentStatusCode.OPPRETTET)
						.build(),
				DokumentInfoBuilder.with()
						.dokumentId(DOKUMENT_ID_3)
						.dokumentStatus(DokumentStatusCode.OPPRETTET)
						.build()
		).build();
		dokumentDistribusjonRepository.save(distribusjoninfo);
		commitAndBeginNewTransaction();

		dokumentDistribusjonRepository.updateStatusForAllDokumentInfosRelatedTo(distribusjoninfo, DokumentStatusCode.OVERSENDT, USER_ID);
		commitAndBeginNewTransaction();

		var result = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjoninfo.getDistribusjonId());

		Assertions.assertTrue(result.getDokumentInfos().stream().
				allMatch(it -> it.getDokumentStatus() == DokumentStatusCode.OVERSENDT && it.getChangeStamp().getEndretAv() != null));
	}

	@Test
	void shouldUpdateStatusToEkspedertForAllDokumentInfosRelatedTo() {
		DistribusjonInfo distribusjoninfo = createDistribusjonInfo().dokumentInfos(
				DokumentInfoBuilder.with()
						.dokumentId(DOKUMENT_ID_2)
						.dokumentStatus(DokumentStatusCode.OPPRETTET)
						.build(),
				DokumentInfoBuilder.with()
						.dokumentId(DOKUMENT_ID_3)
						.dokumentStatus(DokumentStatusCode.OPPRETTET)
						.build()
		).build();
		dokumentDistribusjonRepository.save(distribusjoninfo);
		commitAndBeginNewTransaction();

		dokumentDistribusjonRepository.updateStatusToEkspedertForAllDokumentInfosRelatedTo(distribusjoninfo, USER_ID);
		commitAndBeginNewTransaction();

		var result = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjoninfo.getDistribusjonId());

		Assertions.assertTrue(result.getDokumentInfos().stream().allMatch(
				it -> it.getDokumentStatus() == DokumentStatusCode.EKSPEDERT
					&& it.getChangeStamp().getEndretAv() != null
					&& it.getEkspedertDato() != null
				)
		);
	}

	@Test
	public void shouldFindDistribusjonInfoByDokumentStatusAndDistribusjonKanalWithRightAge() {
		//Opprett en distribusjonInfo med distribution_datetime = now
		DistribusjonInfo info = createDistribusjonInfoWithDokumentInfo().build();
		dokumentDistribusjonRepository.save(info);
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

