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

import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.BEKREFTET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.KLAR_FOR_DIST;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OPPRETTET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OVERSENDT;
import static no.nav.dokdistadmin.repository.TestUtils.DISTRIBUSJON_ID;
import static no.nav.dokdistadmin.repository.TestUtils.DOKUMENT_ID_1;
import static no.nav.dokdistadmin.repository.TestUtils.DOKUMENT_ID_2;
import static no.nav.dokdistadmin.repository.TestUtils.createDistribusjonInfo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DokumentDistribusjonRepositoryTest extends AbstractRepositoryTest {

	private static final int OPPRETTET_ANTALL_DAGER_SIDEN = 60;

	@Test
	public void shouldSaveDistribusjonInfo() {
		var distribusjonInfo = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

		assertNotNull(distribusjonInfo.getDistribusjonInfoId());
	}

	@Test
	public void shouldUpdateDistribusjonInfo() {
		var distribusjonInfo = dokumentDistribusjonRepository.persist(createDistribusjonInfo());
		distribusjonInfo.setDistribusjonStatus(DistribusjonStatusCode.OVERSENDT);
		dokumentDistribusjonRepository.persist(distribusjonInfo);

		var updated = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjonInfo.getDistribusjonId());

		assertEquals(DistribusjonStatusCode.OVERSENDT, updated.getDistribusjonStatus());

	}

	@Test
	public void shouldFindDistribusjonInfoByDistribusjonId() {
		var distribusjonInfo = dokumentDistribusjonRepository.persist(createDistribusjonInfo());
		dokumentDistribusjonRepository.persist(distribusjonInfo);

		var result = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjonInfo.getDistribusjonId());

		assertNotNull(result);
		assertEquals(distribusjonInfo.getDistribusjonInfoId(), result.getDistribusjonInfoId());
		assertEquals(DISTRIBUSJON_ID, result.getDistribusjonId());
	}

	@Test
	public void shouldFindDistribusjonInfoByDokumentStatusAndDistribusjonKanalWithRightAge() {

		var IKKE_EKSPEDERT = EnumSet.of(OPPRETTET, OVERSENDT, BEKREFTET, KLAR_FOR_DIST);

		//Opprett en distribusjonInfo med distribution_datetime = now
		DistribusjonInfo distribusjoninfo = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

		distribusjoninfo.addDokumentInfo(DokumentInfo.builder()
				.dokumentId(DOKUMENT_ID_1)
				.dokumentStatus(DokumentStatusCode.OPPRETTET)
				.build());
		distribusjoninfo.addDokumentInfo(DokumentInfo.builder()
				.dokumentId(DOKUMENT_ID_2)
				.dokumentStatus(DokumentStatusCode.OPPRETTET)
				.build());
		dokumentDistribusjonRepository.persist(distribusjoninfo);

		commitAndBeginNewTransaction();

		//Hent alle distribusjonIder som er eldre enn en time
		LocalDateTime etterAntallDagerSiden = LocalDateTime.now().minusDays(OPPRETTET_ANTALL_DAGER_SIDEN);
		LocalDateTime foerAntallTimerSiden = LocalDateTime.now().minusHours(1L);
		List<DistribusjonInfo> result = dokumentDistribusjonRepository.findDistribusjonInfoByDokumentStatusAndDistribusjonKanal(
				IKKE_EKSPEDERT, PRINT, etterAntallDagerSiden, foerAntallTimerSiden);

		assertTrue(result.isEmpty());

		//Hent alle distribusjonIder uten begrensning på opprettetDato
		foerAntallTimerSiden = LocalDateTime.now().minusHours(0L);
		result = dokumentDistribusjonRepository.findDistribusjonInfoByDokumentStatusAndDistribusjonKanal(
				IKKE_EKSPEDERT, PRINT, etterAntallDagerSiden, foerAntallTimerSiden);

		assertEquals(1, result.size());
	}
}
