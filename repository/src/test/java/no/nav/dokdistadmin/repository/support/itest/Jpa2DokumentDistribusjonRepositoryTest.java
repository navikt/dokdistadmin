package no.nav.dokdistadmin.repository.support.itest;

import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.FilInfo;
import no.nav.dokdistadmin.domain.FilStatusCode;
import no.nav.dokdistadmin.domain.FilTypeCode;
import no.nav.dokdistadmin.domain.KildeTypeCode;
import no.nav.dokdistadmin.domain.KommunikasjonRetningCode;
import no.nav.dokdistadmin.domain.ModusCode;
import no.nav.dokdistadmin.domain.builder.DistribusjonInfoBuilder;
import no.nav.dokdistadmin.domain.builder.DokumentInfoBuilder;
import no.nav.dokdistadmin.domain.builder.FilInfoBuilder;
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
import static no.nav.dokdistadmin.domain.DokumentStatusCode.RETURPOSTBEHANDLET;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.fail;

public class Jpa2DokumentDistribusjonRepositoryTest extends RepositoryTest {

	private static final String DISTRIBUSJON_ID = "123";
	private static final String DOKUMENT_ID = "456";
	private static final String KONVERSASJONSID = "879";
	private static final String FILNAVN = "filnavn";

	@Autowired
	private DokumentDistribusjonRepository repository;

	@Test
	public void shouldSaveNewDistribusjonInfo() {
		DistribusjonInfo persisted = repository.saveNewDistribusjonInfo(createDistribusjonInfo().build());

		assertThat(persisted.getDistribusjonInfoId(), is(notNullValue()));
	}

	@Test
	public void shouldUpdateDistribusjonInfo() {
		DistribusjonInfo persisted = createDistribusjonInfoWithDokumentInfo().buildAndPersist(entityManager);

		persisted.addDokumentInfo(DokumentInfoBuilder.with()
						.dokumentId("123")
						.dokumentStatus(DokumentStatusCode.OVERSENDT)
						.build()
		);
		persisted.addDokumentInfo(DokumentInfoBuilder.with()
						.dokumentId("456")
						.dokumentStatus(DokumentStatusCode.OVERSENDT)
						.build()
		);

		repository.updateDistribusjonInfo(persisted);

		DistribusjonInfo found = repository.findDistribusjonInfoById(persisted.getDistribusjonInfoId());

		assertThat(found.getDokumentInfos().size(), is(3));
	}

	@Test
	public void shouldUpdateDokumentInfo() throws DuplicateResponseException {
		DistribusjonInfo persisted = createDistribusjonInfoWithDokumentInfo().buildAndPersist(entityManager);

		DokumentInfo dokumentInfo = persisted.getDokumentInfos().iterator().next();
		dokumentInfo.setDokumentStatus(DokumentStatusCode.EKSPEDERT);
		dokumentInfo.getDistribusjonInfo().setDistribusjonStatus(DistribusjonStatusCode.BEKREFTET);

		repository.updateDokumentInfo(dokumentInfo);

		DokumentInfo found = repository.findDokumentInfoByKonversasjonId(dokumentInfo.getKonversasjonId());

		assertThat(found.getDokumentStatus(), is(DokumentStatusCode.EKSPEDERT));
		assertThat(found.getDistribusjonInfo().getDistribusjonStatus(), is(DistribusjonStatusCode.BEKREFTET));
	}

	@Test
	public void shouldFindDistribusjonInfoById() {
		DistribusjonInfo persisted = createDistribusjonInfo().buildAndPersist(entityManager);

		DistribusjonInfo found = repository.findDistribusjonInfoById(persisted.getDistribusjonInfoId());

		assertThat(found, is(notNullValue()));
		assertThat(found.getDistribusjonInfoId(), is(persisted.getDistribusjonInfoId()));
		assertThat(found.getDistribusjonId(), is("123"));
	}

	@Test
	public void shouldReturnNullIfNoDistribusjonInfoWasFound() {
		DistribusjonInfo found = repository.findDistribusjonInfoById(100L);

		assertThat(found, is(nullValue()));
	}

	@Test
	public void shouldReturnNullIfDistribusjonsInfoIdWasNull() {
		DistribusjonInfo found = repository.findDistribusjonInfoById(null);

		assertThat(found, is(nullValue()));
	}

	@Test
	public void shouldFindDistribusjonInfoByDistribusjonId() {
		DistribusjonInfo persisted = createDistribusjonInfo().buildAndPersist(entityManager);
		DistribusjonInfo found = repository.findDistribusjonInfoByDistribusjonId(DISTRIBUSJON_ID);

		assertThat(found, is(persisted));
	}

	@Test
	public void shouldReturnNullIfDistribusjonIdWasNull() {
		DistribusjonInfo found = repository.findDistribusjonInfoByDistribusjonId(null);

		assertThat(found, is(nullValue()));
	}

	@Test
	public void shouldReturnNullIfNoDistribusjonInfoWasFoundWithDistribusjonId() {
		DistribusjonInfo found = repository.findDistribusjonInfoByDistribusjonId("100");

		assertThat(found, is(nullValue()));
	}

	@Test
	public void shouldFindDokumentInfoByDokumentId() {
		DistribusjonInfo persisted = createDistribusjonInfoWithDokumentInfo().buildAndPersist(entityManager);
		DokumentInfo found = repository.findDokumentInfoByDokumentId(DOKUMENT_ID);

		assertThat(found, is(notNullValue()));
		assertThat(found, is(persisted.getDokumentInfos().iterator().next()));
	}

	@Test
	public void shouldReturnNullIfDokumentIdWasNull() {
		DokumentInfo found = repository.findDokumentInfoByDokumentId(null);

		assertThat(found, is(nullValue()));
	}

	@Test
	public void shouldReturnNullIfNoDokumentInfoWasFound() {
		DokumentInfo found = repository.findDokumentInfoByDokumentId("100");

		assertThat(found, is(nullValue()));
	}

	@Test
	public void shouldUpdateDokumentInfosStatus() {
		DistribusjonInfo distribusjonInfo = createDistribusjonInfo()
				.dokumentInfos(DokumentInfoBuilder.with()
						.dokumentId("234")
						.dokumentStatus(DokumentStatusCode.OPPRETTET)
						.build()).buildAndPersist(entityManager);

		DokumentStatusCode dokumentStatus = DokumentStatusCode.OVERSENDT;

		repository.updateStatusForAllDokumentInfosRelatedTo(distribusjonInfo, dokumentStatus);

		entityManager.refresh(distribusjonInfo); //read DB updates back into session
		for (DokumentInfo dokumentInfo : distribusjonInfo.getDokumentInfos()) {
			assertThat(dokumentInfo.getDokumentStatus(), is(dokumentStatus));
			assertThat(dokumentInfo.getChangeStamp().getEndretAv(), notNullValue());
			assertThat(dokumentInfo.getChangeStamp().getEndretDato(), notNullValue());
			assertThat(dokumentInfo.getVersion(), is(2L));
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

		assertThat(distribusjonInfo.getDokumentInfos(), hasSize(2));
		for (DokumentInfo dokumentInfo : distribusjonInfo.getDokumentInfos()) {
			assertThat(dokumentInfo.getDokumentStatus(), is(DokumentStatusCode.EKSPEDERT));
			assertThat(dokumentInfo.getEkspedertDato(), is(notNullValue()));
			assertThat(dokumentInfo.getChangeStamp().getEndretAv(), notNullValue());
			assertThat(dokumentInfo.getChangeStamp().getEndretDato(), notNullValue());
			assertThat(dokumentInfo.getVersion(), is(2L));
		}
	}

	@Test
	public void shouldSaveNewDokumentInfo() {
		DokumentInfo dokumentInfo = DokumentInfoBuilder.with()
				.dokumentId(DOKUMENT_ID)
				.dokumentStatus(DokumentStatusCode.OPPRETTET)
				.build();
		dokumentInfo.setDistribusjonInfo(DistribusjonInfoBuilder.with()
				.distribusjonId(DISTRIBUSJON_ID)
				.distribusjonDato(LocalDateTime.now())
				.distribusjonKanal(PRINT)
				.distribusjonStatus(DistribusjonStatusCode.OVERSENDT)
				.modus(ModusCode.P)
				.buildAndPersist(entityManager));

		repository.saveNewDokumentInfo(dokumentInfo);

		assertThat(dokumentInfo.getDokumentId(), is(notNullValue()));
	}

	@Test
	public void shouldFindDokumentInfoByKonversasjonsId() throws DuplicateResponseException {
		DistribusjonInfo persisted = createDistribusjonInfoWithDokumentInfo().buildAndPersist(entityManager);
		DokumentInfo found = repository.findDokumentInfoByKonversasjonId(KONVERSASJONSID);

		assertThat(found, is(notNullValue()));
		assertThat(found, is(persisted.getDokumentInfos().iterator().next()));
	}

	@Test
	public void shouldNotFindDokumentInfoByWrongKonversasjonsId() throws DuplicateResponseException {
		createDistribusjonInfo().buildAndPersist(entityManager);
		DokumentInfo found = repository.findDokumentInfoByKonversasjonId(KONVERSASJONSID + "garbage");

		assertThat(found, is(nullValue()));
	}

	@Test
	public void shouldFailFindDokumentInfoWithDuplicateKonversasjonsId() throws DuplicateResponseException {
		createDistribusjonInfo("1").dokumentInfos(createDokumentInfo().build()).buildAndPersist(entityManager);
		createDistribusjonInfo("2").dokumentInfos(createDokumentInfo().build()).buildAndPersist(entityManager);
		try {
			repository.findDokumentInfoByKonversasjonId(KONVERSASJONSID);
			fail("Should throw exception");
		} catch (DuplicateResponseException ignored)
		{
		}
	}

	@Test
	public void shouldPersistFilInfoWithDistribusjonInfoRelation() {
		DistribusjonInfo distribusjonInfo = createDistribusjonInfo().filInfos(createFilInfo().build()).buildAndPersist(entityManager);
		entityManager.refresh(distribusjonInfo);
		FilInfo filInfo = distribusjonInfo.getFilInfos().iterator().next();

		assertThat(filInfo.getFilnavn(), is(notNullValue()));
		assertThat(filInfo.getDistribusjonInfos().iterator().next(), is(distribusjonInfo));
	}

	@Test
	public void shouldPersistFilInfoWithDokumentInfoRelation() {
		FilInfo filInfo1 = createFilInfo().build();
		FilInfo filInfo2 = createFilInfo().build();
		DokumentInfo dokumentInfo = createDokumentInfo().filInfos(filInfo1, filInfo2).build();
		createDistribusjonInfo().dokumentInfos(dokumentInfo).buildAndPersist(entityManager);
		entityManager.clear();
		DokumentInfo persistedDokumentInfo = repository.findDokumentInfoByDokumentId(DOKUMENT_ID);

		for (FilInfo filInfo : persistedDokumentInfo.getFilInfos()) {
			assertThat(filInfo.getDokumentInfos().iterator().next(), is(persistedDokumentInfo));
		}
	}

	@Test
	public void shouldFindDistribusjonInfoByDokumentStatusAndDistribusjonKanalWithRightAge() {
		//creates a distribution with distribution_datetime = now
		DistribusjonInfo info = createDistribusjonInfoWithDokumentInfo().buildAndPersist(entityManager);

		//Looks for anything that's (now-1hrs) old
		List<DistribusjonInfo> found = repository.findDistribusjonInfoByDokumentStatusAndDistribusjonKanal(EnumSet.of(EKSPEDERT, FEILET, RETURPOSTBEHANDLET), PRINT, 1L);
		assertThat(found.size(), is(0));

		//Should find something as it looks for anything that's made before (now +1hr)
		found = repository.findDistribusjonInfoByDokumentStatusAndDistribusjonKanal(EnumSet.of(EKSPEDERT, FEILET, RETURPOSTBEHANDLET), PRINT, -1L);
		assertThat(found.size(), is(1));
		assertThat(info, is(found.get(0)));
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
				.modus(ModusCode.P);
	}

	private DokumentInfoBuilder createDokumentInfo() {
		return createDokumentInfo(DOKUMENT_ID);
	}

	private DokumentInfoBuilder createDokumentInfo(String dokumentId) {
		return DokumentInfoBuilder.with()
				.dokumentId(dokumentId)
				.dokumentStatus(DokumentStatusCode.OPPRETTET)
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

}
