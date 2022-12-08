package no.nav.dokdistadmin.repository.support.itest;

import no.nav.dokdistadmin.domain.FilInfo;
import no.nav.dokdistadmin.domain.FilStatusCode;
import no.nav.dokdistadmin.domain.FilTypeCode;
import no.nav.dokdistadmin.domain.KildeTypeCode;
import no.nav.dokdistadmin.domain.KommunikasjonRetningCode;
import no.nav.dokdistadmin.domain.builder.FilInfoBuilder;
import no.nav.dokdistadmin.repository.FilInfoRepository;
import no.nav.dokdistadmin.repository.RepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static no.nav.dokdistadmin.domain.FilStatusCode.FEILET;
import static no.nav.dokdistadmin.domain.FilTypeCode.KVITTERING_PRINT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class Jpa2FilInfoRepositoryTest extends RepositoryTest {

	private static final String FILNAVN = "fil.pdf";

	@Autowired
	private FilInfoRepository filInfoRepository;

	@Test
	public void shouldPersistFilInfo() {
		FilInfo filInfo = buildFilInfo();

		filInfoRepository.saveNewFilInfo(filInfo);

		assertNotNull(filInfo.getFilInfoId());
	}

	@Test
	public void shouldUpdateFilInfo() {
		FilInfo persisted = buildAndPersistFilInfo();
		entityManager.detach(persisted);
		persisted.setFilType(KVITTERING_PRINT);

		filInfoRepository.updateFilInfo(persisted);

		FilInfo foundFilInfo = filInfoRepository.findFilInfoById(persisted.getFilInfoId());
		assertEquals(KVITTERING_PRINT, foundFilInfo.getFilType());
	}

	@Test
	public void shouldFindFilInfo() {
		FilInfo persistedFilInfo = buildAndPersistFilInfo();

		FilInfo foundFilInfo = filInfoRepository.findFilInfoById(persistedFilInfo.getFilInfoId());

		assertEquals(persistedFilInfo, foundFilInfo);
	}

	@Test
	public void shouldNotFindFilInfoAndReturnNull() {
		FilInfo foundFilInfo = filInfoRepository.findFilInfoById(100L);

		assertNull(foundFilInfo);
	}

	@Test
	public void shouldFindFilInfoByFilnavn() {
		FilInfo persistedFilInfo = buildAndPersistFilInfo();
		FilInfo foundFilInfo = filInfoRepository.findFilInfoByFilnavn(FILNAVN);

		assertEquals(persistedFilInfo, foundFilInfo);
	}

	@Test
	public void shouldSetFilInfoStatus() {
		FilInfo persistedFilInfo = buildAndPersistFilInfo();
		filInfoRepository.updateFilInfoStatus(persistedFilInfo.getFilInfoId(), FEILET);
		FilInfo foundFilInfo = filInfoRepository.findFilInfoById(persistedFilInfo.getFilInfoId());

		assertEquals(FEILET, foundFilInfo.getFilStatus());
	}

	private FilInfo buildFilInfo() {
		return FilInfoBuilder.with()
				.filnavn(FILNAVN)
				.filType(FilTypeCode.BEST_INFO_PRINT)
				.sendtDato(LocalDateTime.now())
				.kommunikasjonRetning(KommunikasjonRetningCode.UTGAENDE)
				.filStatus(FilStatusCode.OK)
				.kildeType(KildeTypeCode.HP_REXX)
				.build();
	}

	private FilInfo buildAndPersistFilInfo() {
		return FilInfoBuilder.with()
				.filnavn(FILNAVN)
				.filType(FilTypeCode.BEST_INFO_PRINT)
				.sendtDato(LocalDateTime.now())
				.kommunikasjonRetning(KommunikasjonRetningCode.UTGAENDE)
				.filStatus(FilStatusCode.OK)
				.kildeType(KildeTypeCode.HP_REXX)
				.buildAndPersist(entityManager);
	}

}
