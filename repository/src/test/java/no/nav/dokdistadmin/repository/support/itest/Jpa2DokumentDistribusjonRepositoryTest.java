package no.nav.dokdistadmin.repository.support.itest;

import no.nav.dokdistadmin.domain.ArkivSystemCode;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.FeilTypeCode;
import no.nav.dokdistadmin.domain.Feilkvittering;
import no.nav.dokdistadmin.domain.FilInfo;
import no.nav.dokdistadmin.domain.FilStatusCode;
import no.nav.dokdistadmin.domain.FilTypeCode;
import no.nav.dokdistadmin.domain.KildeTypeCode;
import no.nav.dokdistadmin.domain.KommunikasjonRetningCode;
import no.nav.dokdistadmin.domain.builder.DistribusjonInfoBuilder;
import no.nav.dokdistadmin.domain.builder.DokumentInfoBuilder;
import no.nav.dokdistadmin.domain.builder.FeilkvitteringBuilder;
import no.nav.dokdistadmin.domain.builder.FilInfoBuilder;
import no.nav.dokdistadmin.domain.builder.LandkodePostDestBuilder;
import no.nav.dokdistadmin.domain.exception.DuplicateResponseException;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.RepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;

import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.FEILET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OPPRETTET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OVERSENDT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.RETURPOSTBEHANDLET;
import static no.nav.dokdistadmin.domain.ModusCode.P;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Jpa2DokumentDistribusjonRepositoryTest extends RepositoryTest {

	private static final String DISTRIBUSJON_ID = "123";
	private static final String DOKUMENT_ID_1 = "1111";
	private static final String DOKUMENT_ID_2 = "2222";
	private static final String DOKUMENT_ID_3 = "3333";
	private static final String KONVERSASJONSID = "879";
	private static final String FILNAVN = "filnavn";
	private static final String DISTRIBUSJON_ID_NOT_IN_DB = "100";
	private static final String DOKUMENT_ID_NOT_IN_DB = "100";
	private static final String ARKIVKODE = "123456789";
	private static final Long DISTRIBUSJONINFO_ID_NOT_IN_DB = 100L;

	private static final String LANDKODE_NO = "NO";
	private static final String POST_DESTINATION = "INNLAND";

	private static final LocalDateTime GYLDIG_EKSPEDERTDATO = LocalDateTime.of(2022, 10, 2, 0, 0);
	private static final LocalDateTime UGYLDIG_EKSPEDERTDATO = LocalDateTime.of(2022, 9, 30, 0, 0);

	@Autowired
	private DokumentDistribusjonRepository repository;

	@Test
	public void shouldSaveNewDistribusjonInfo() {
		DistribusjonInfo persisted = repository.saveNewDistribusjonInfo(createDistribusjonInfo().build());

		assertNotNull(persisted.getDistribusjonInfoId());
	}

	@Test
	public void shouldUpdateDistribusjonInfo() {
		DistribusjonInfo persisted = createDistribusjonInfoWithDokumentInfo().buildAndPersist(entityManager);

		persisted.addDokumentInfo(DokumentInfoBuilder.with()
				.dokumentId(DOKUMENT_ID_2)
				.dokumentStatus(DokumentStatusCode.OVERSENDT)
				.build());
		persisted.addDokumentInfo(DokumentInfoBuilder.with()
				.dokumentId(DOKUMENT_ID_3)
				.dokumentStatus(DokumentStatusCode.OVERSENDT)
				.build());

		repository.updateDistribusjonInfo(persisted);

		DistribusjonInfo found = repository.findDistribusjonInfoById(persisted.getDistribusjonInfoId());

		assertEquals(3, found.getDokumentInfos().size());
	}

	@Test
	public void shouldUpdateDokumentInfo() throws DuplicateResponseException {
		DistribusjonInfo persisted = createDistribusjonInfoWithDokumentInfo().buildAndPersist(entityManager);

		DokumentInfo dokumentInfo = persisted.getDokumentInfos().iterator().next();
		dokumentInfo.setDokumentStatus(EKSPEDERT);
		dokumentInfo.getDistribusjonInfo().setDistribusjonStatus(DistribusjonStatusCode.BEKREFTET);

		repository.updateDokumentInfo(dokumentInfo);

		DokumentInfo result = repository.findDokumentInfoByKonversasjonId(dokumentInfo.getKonversasjonId());

		assertEquals(EKSPEDERT, result.getDokumentStatus());
		assertEquals(DistribusjonStatusCode.BEKREFTET, result.getDistribusjonInfo().getDistribusjonStatus());
	}

	@Test
	public void shouldFindEkspedertDokumentInfo() {
		createDistribusjonInfo()
				.dokumentInfos(
						createDokumentInfo("1")
								.dokumentStatus(OVERSENDT)
								.arkivSystem(ArkivSystemCode.JOARK)
								.arkivkode(ARKIVKODE)
								.ekspedertDato(GYLDIG_EKSPEDERTDATO)
								.build(),
						createDokumentInfo("2")
								.dokumentStatus(EKSPEDERT)
								.arkivSystem(ArkivSystemCode.JOARK)
								.arkivkode(ARKIVKODE)
								.ekspedertDato(GYLDIG_EKSPEDERTDATO)
								.build(),
						createDokumentInfo("3")
								.dokumentStatus(EKSPEDERT)
								.arkivSystem(ArkivSystemCode.JOARK)
								.arkivkode(ARKIVKODE)
								.ekspedertDato(UGYLDIG_EKSPEDERTDATO)
								.build(),
						createDokumentInfo("4")
								.dokumentStatus(EKSPEDERT)
								.arkivSystem(ArkivSystemCode.INGEN)
								.arkivkode(ARKIVKODE)
								.ekspedertDato(GYLDIG_EKSPEDERTDATO)
								.build()
				).buildAndPersist(entityManager);

		var result = repository.findEkspedertDokumentInfo(10);

		assertEquals(1, result.size());
		assertEquals("2", result.get(0).getDokumentId());
	}

	@Test
	public void shouldUpdateDokumentInfosAvstemtArkivDato() {
		DistribusjonInfo persisted = createDistribusjonInfo().dokumentInfos(
				DokumentInfoBuilder.with()
						.dokumentId(DOKUMENT_ID_2)
						.dokumentStatus(DokumentStatusCode.OVERSENDT)
						.build(),
				DokumentInfoBuilder.with()
						.dokumentId(DOKUMENT_ID_3)
						.dokumentStatus(DokumentStatusCode.OVERSENDT)
						.build()
		).buildAndPersist(entityManager);

		var dokumentInfoIdList = persisted.getDokumentInfos().stream()
				.map(DokumentInfo::getDokumentInfoId)
				.toList();

		repository.updateDokumentInfosAvstemtArkivDato(dokumentInfoIdList);
		entityManager.refresh(persisted); //read DB updates back into session

		assertTrue(persisted.getDokumentInfos().stream().
				allMatch(it -> it.getAvstemtArkivDato() != null && it.getChangeStamp().getEndretAv() != null));
	}

	@Test
	public void shouldFindDistribusjonInfoById() {
		DistribusjonInfo persisted = createDistribusjonInfo().buildAndPersist(entityManager);

		DistribusjonInfo result = repository.findDistribusjonInfoById(persisted.getDistribusjonInfoId());

		assertNotNull(result);
		assertEquals(persisted.getDistribusjonInfoId(), result.getDistribusjonInfoId());
		assertEquals(DISTRIBUSJON_ID, result.getDistribusjonId());
	}

	@Test
	public void shouldReturnNullIfNoDistribusjonInfoWasFound() {
		DistribusjonInfo result = repository.findDistribusjonInfoById(DISTRIBUSJONINFO_ID_NOT_IN_DB);

		assertNull(result);
	}

	@Test
	public void shouldReturnNullIfDistribusjonsInfoIdWasNull() {
		DistribusjonInfo result = repository.findDistribusjonInfoById(null);

		assertNull(result);
	}

	@Test
	public void shouldFindDistribusjonInfoByDistribusjonId() {
		DistribusjonInfo persisted = createDistribusjonInfo().buildAndPersist(entityManager);
		DistribusjonInfo result = repository.findDistribusjonInfoByDistribusjonId(DISTRIBUSJON_ID);

		assertEquals(persisted, result);
	}

	@Test
	public void shouldReturnNullIfDistribusjonIdWasNull() {
		DistribusjonInfo result = repository.findDistribusjonInfoByDistribusjonId(null);

		assertNull(result);
	}

	@Test
	public void shouldReturnNullIfNoDistribusjonInfoWasFoundWithDistribusjonId() {
		DistribusjonInfo result = repository.findDistribusjonInfoByDistribusjonId(DISTRIBUSJON_ID_NOT_IN_DB);

		assertNull(result);
	}

	@Test
	public void shouldFindDokumentInfoByDokumentId() {
		DistribusjonInfo persisted = createDistribusjonInfoWithDokumentInfo().buildAndPersist(entityManager);
		DokumentInfo persistedDokumentInfo = persisted.getDokumentInfos().iterator().next();
		DokumentInfo result = repository.findDokumentInfoByDokumentId(DOKUMENT_ID_1);

		assertNotNull(result);
		assertEquals(persistedDokumentInfo, result);
	}

	@Test
	public void shouldReturnNullIfDokumentIdWasNull() {
		DokumentInfo result = repository.findDokumentInfoByDokumentId(null);

		assertNull(result);
	}

	@Test
	public void shouldReturnNullIfNoDokumentInfoWasFound() {
		DokumentInfo result = repository.findDokumentInfoByDokumentId(DOKUMENT_ID_NOT_IN_DB);

		assertNull(result);
	}

	@Test
	public void shouldUpdateDokumentInfosStatus() {
		DistribusjonInfo distribusjonInfo = createDistribusjonInfo()
				.dokumentInfos(DokumentInfoBuilder.with()
						.dokumentId(DOKUMENT_ID_1)
						.dokumentStatus(OPPRETTET)
						.build()).buildAndPersist(entityManager);

		DokumentStatusCode dokumentStatus = DokumentStatusCode.OVERSENDT;

		repository.updateStatusForAllDokumentInfosRelatedTo(distribusjonInfo, dokumentStatus);

		entityManager.refresh(distribusjonInfo); //read DB updates back into session

		for (DokumentInfo dokumentInfo : distribusjonInfo.getDokumentInfos()) {
			assertEquals(dokumentStatus, dokumentInfo.getDokumentStatus());
			assertNotNull(dokumentInfo.getChangeStamp().getEndretAv());
			assertNotNull(dokumentInfo.getChangeStamp().getEndretDato());
			assertEquals(2L, dokumentInfo.getVersion());
		}
	}

	@Test
	public void shouldUpdateDokumentInfosStatusToEkspedert() {
		DistribusjonInfo distribusjonInfo = createDistribusjonInfo()
				.dokumentInfos(createDokumentInfo().build())
				.dokumentInfos(createDokumentInfo().build())
				.buildAndPersist(entityManager);

		repository.updateStatusToEkspedertForAllDokumentInfosRelatedTo(distribusjonInfo);

		entityManager.refresh(distribusjonInfo); //read DB updates back into session

		assertEquals(2, distribusjonInfo.getDokumentInfos().size());

		for (DokumentInfo dokumentInfo : distribusjonInfo.getDokumentInfos()) {
			assertEquals(EKSPEDERT, dokumentInfo.getDokumentStatus());
			assertNotNull(dokumentInfo.getEkspedertDato());
			assertNotNull(dokumentInfo.getChangeStamp().getEndretAv());
			assertNotNull(dokumentInfo.getChangeStamp().getEndretDato());
			assertEquals(2L, dokumentInfo.getVersion());
		}
	}

	@Test
	public void shouldSaveNewDokumentInfo() {
		DokumentInfo dokumentInfo = DokumentInfoBuilder.with()
				.dokumentId(DOKUMENT_ID_1)
				.dokumentStatus(OPPRETTET)
				.build();
		dokumentInfo.setDistribusjonInfo(DistribusjonInfoBuilder.with()
				.distribusjonId(DISTRIBUSJON_ID)
				.distribusjonDato(LocalDateTime.now())
				.distribusjonKanal(PRINT)
				.distribusjonStatus(DistribusjonStatusCode.OVERSENDT)
				.modus(P)
				.buildAndPersist(entityManager));

		repository.saveNewDokumentInfo(dokumentInfo);

		assertNotNull(dokumentInfo.getDokumentInfoId());
	}

	@Test
	public void shouldFindDokumentInfoByDokumentInfoId() {
		DistribusjonInfo persisted = createDistribusjonInfoWithDokumentInfo()
				.buildAndPersist(entityManager);

		var dokumentInfoId = persisted.getDokumentInfos().iterator().next().getDokumentInfoId();

		var result = repository.findDokumentInfoByDokumentInfoId(dokumentInfoId);

		assertNotNull(result);
	}

	@Test
	public void shouldFindDokumentInfoByJournalpostId() {
		createDistribusjonInfo().dokumentInfos(DokumentInfoBuilder.with()
						.dokumentId(DOKUMENT_ID_1)
						.dokumentStatus(OPPRETTET)
						.arkivkode(ARKIVKODE)
						.build())
				.buildAndPersist(entityManager);

		var result = repository.findDokumentInfoByJournalpostId(ARKIVKODE);

		assertNotNull(result);
	}

	@Test
	public void shouldFindPostDestinasjon() {
		createLandKodePostDestination().buildAndPersist(entityManager);

		var result = repository.findPostDestinasjon(LANDKODE_NO);

		assertEquals(POST_DESTINATION, result);
	}

	@Test
	public void shouldFindDokumentInfoByDokumentStatusAndDistribusjonKanal() {

		createDistribusjonInfo().dokumentInfos(
				DokumentInfoBuilder.with()
						.dokumentId(DOKUMENT_ID_1)
						.dokumentStatus(OPPRETTET)
						.build(),
				DokumentInfoBuilder.with()
						.dokumentId(DOKUMENT_ID_2)
						.dokumentStatus(OVERSENDT)
						.build()
		).buildAndPersist(entityManager);


		var result = repository.findDokumentInfoByDokumentStatusAndDistribusjonKanal(
				List.of(OPPRETTET),
				PRINT,
				LocalDateTime.now().minusHours(1L));

		assertEquals(1, result.size());
		assertEquals(DOKUMENT_ID_1, result.get(0).getDokumentId());
	}

	@Test
	public void shouldFindDokumentInfoByKonversasjonsId() throws DuplicateResponseException {
		DistribusjonInfo persisted = createDistribusjonInfoWithDokumentInfo().buildAndPersist(entityManager);
		DokumentInfo persistedDokumentInfo = persisted.getDokumentInfos().iterator().next();

		DokumentInfo result = repository.findDokumentInfoByKonversasjonId(KONVERSASJONSID);

		assertNotNull(result);
		assertEquals(persistedDokumentInfo, result);
	}

	@Test
	public void shouldNotFindDokumentInfoByWrongKonversasjonsId() throws DuplicateResponseException {
		createDistribusjonInfo().buildAndPersist(entityManager);
		DokumentInfo result = repository.findDokumentInfoByKonversasjonId(KONVERSASJONSID + "garbage");

		assertNull(result);
	}

	@Test
	public void shouldFailFindDokumentInfoWithDuplicateKonversasjonsId() throws DuplicateResponseException {
		createDistribusjonInfo("1").dokumentInfos(createDokumentInfo().build()).buildAndPersist(entityManager);
		createDistribusjonInfo("2").dokumentInfos(createDokumentInfo().build()).buildAndPersist(entityManager);

		DuplicateResponseException result = assertThrows(DuplicateResponseException.class,
				() -> repository.findDokumentInfoByKonversasjonId(KONVERSASJONSID));

		assertTrue(result.getMessage().contains("NonUnique konversasjonsId"));
	}

	@Test
	public void shouldPersistFilInfoWithDistribusjonInfoRelation() {
		DistribusjonInfo distribusjonInfo = createDistribusjonInfo().filInfos(createFilInfo().build()).buildAndPersist(entityManager);
		entityManager.refresh(distribusjonInfo);
		FilInfo filInfo = distribusjonInfo.getFilInfos().iterator().next();

		assertNotNull(filInfo.getFilnavn());
		assertEquals(distribusjonInfo, filInfo.getDistribusjonInfos().iterator().next());
	}

	@Test
	public void shouldPersistFilInfoWithDokumentInfoRelation() {
		FilInfo filInfo1 = createFilInfo().build();
		FilInfo filInfo2 = createFilInfo().build();
		DokumentInfo dokumentInfo = createDokumentInfo().filInfos(filInfo1, filInfo2).build();
		createDistribusjonInfo().dokumentInfos(dokumentInfo).buildAndPersist(entityManager);
		entityManager.clear();
		DokumentInfo persistedDokumentInfo = repository.findDokumentInfoByDokumentId(DOKUMENT_ID_1);

		for (FilInfo filInfo : persistedDokumentInfo.getFilInfos()) {
			assertEquals(persistedDokumentInfo, filInfo.getDokumentInfos().iterator().next());
		}
	}

	@Test
	public void shouldFindDistribusjonInfoByDokumentInfoId() {
		var distribusjon = createDistribusjonInfo()
				.dokumentInfos(
						DokumentInfoBuilder.with()
								.dokumentId(DOKUMENT_ID_1)
								.dokumentStatus(OPPRETTET)
								.build()
				).buildAndPersist(entityManager);

		Long dokumentInfoId = distribusjon.getDokumentInfos().iterator().next().getDokumentInfoId();

		DistribusjonInfo result = repository.findDistribusjonInfoByDokumentInfoId(dokumentInfoId);

		assertNotNull(result);
		assertEquals(distribusjon, result);
	}

	@Test
	public void shouldFindDistribusjonInfoByDokumentStatusAndDistribusjonKanalWithRightAge() {
		//Opprett en distribusjonInfo med distribution_datetime = now
		DistribusjonInfo info = createDistribusjonInfoWithDokumentInfo().buildAndPersist(entityManager);

		//Hent alle distribusjonIder som er eldre enn en time gamle
		List<DistribusjonInfo> result = repository.findDistribusjonInfoByDokumentStatusAndDistribusjonKanal(
				EnumSet.of(EKSPEDERT, FEILET, RETURPOSTBEHANDLET), PRINT, 1L);

		assertTrue(result.isEmpty());

		//FIXME Se litt nærmere på denne
		//Hent alle distribusjonIder uten begrensning på opprettetDato
		result = repository.findDistribusjonInfoByDokumentStatusAndDistribusjonKanal(
				EnumSet.of(EKSPEDERT, FEILET, RETURPOSTBEHANDLET), PRINT, 0L);

		assertEquals(1, result.size());
		assertEquals(result.get(0), info);
	}

	@Test
	void shouldSaveFeilkvittering() {
		Feilkvittering feilkvittering = createFeilkvittering().build();

		repository.saveFeilkvittering(feilkvittering);

		assertNotNull(feilkvittering.getFeilkvitteringId());
	}

	private DistribusjonInfoBuilder createDistribusjonInfo() {
		return createDistribusjonInfo("");
	}

	private DistribusjonInfoBuilder createDistribusjonInfoWithDokumentInfo() {
		return createDistribusjonInfo("").dokumentInfos(createDokumentInfo().build());
	}

	private DistribusjonInfoBuilder createDistribusjonInfo(String idPadding) {
		return DistribusjonInfoBuilder.with()
				.distribusjonId(DISTRIBUSJON_ID + idPadding)
				.distribusjonDato(LocalDateTime.now())
				.distribusjonKanal(PRINT)
				.distribusjonStatus(DistribusjonStatusCode.OVERSENDT)
				.modus(P);
	}

	private DokumentInfoBuilder createDokumentInfo() {
		return createDokumentInfo(DOKUMENT_ID_1);
	}

	private DokumentInfoBuilder createDokumentInfo(String dokumentId) {
		return DokumentInfoBuilder.with()
				.dokumentId(dokumentId)
				.dokumentStatus(OPPRETTET)
				.konversasjonsId(KONVERSASJONSID);
	}

	private FilInfoBuilder createFilInfo() {
		return FilInfoBuilder.with()
				.filnavn(FILNAVN)
				.filStatus(FilStatusCode.OPPRETTET)
				.filType(FilTypeCode.PRINTFIL)
				.kildeType(KildeTypeCode.DOKDIST)
				.kommunikasjonRetning(KommunikasjonRetningCode.INNGAENDE);
	}

	private LandkodePostDestBuilder createLandKodePostDestination() {
		return LandkodePostDestBuilder.with()
				.landkode(LANDKODE_NO)
				.postDest(POST_DESTINATION);
	}

	private FeilkvitteringBuilder createFeilkvittering() {
		return FeilkvitteringBuilder.with()
				.feiltype(FeilTypeCode.MELDINGSFEIL)
				.detaljer("En feil har skjedd")
				.feiletTidspunkt(LocalDateTime.now());
	}

}
