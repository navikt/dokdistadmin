package no.nav.dokdistadmin.repository.support.itest;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

import no.nav.dokdistadmin.domain.FilInfo;
import no.nav.dokdistadmin.domain.FilStatusCode;
import no.nav.dokdistadmin.domain.FilTypeCode;
import no.nav.dokdistadmin.domain.KildeTypeCode;
import no.nav.dokdistadmin.domain.KommunikasjonRetningCode;
import no.nav.dokdistadmin.domain.builder.FilInfoBuilder;
import no.nav.dokdistadmin.repository.FilInfoRepository;
import no.nav.dokdistadmin.repository.RepositoryTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

/**
 * Tests for Jpa2FilInfoRepository.
 *
 * @author Thomas Eugen Bjørge, Visma Consulting
 */
public class Jpa2FilInfoRepositoryTest extends RepositoryTest {

	private static final String FILNAVN = "fil.pdf";

	@Autowired
	private FilInfoRepository filInfoRepository;

	@Test
	public void shouldPersistFilInfo() throws Exception {
		FilInfo filInfo = buildFilInfo();

		filInfoRepository.saveNewFilInfo(filInfo);

		assertThat(filInfo.getFilInfoId(), is(notNullValue()));
	}

	@Test
	public void shouldUpdateFilInfo() {
		FilInfo persisted = buildAndPersistFilInfo();
		entityManager.detach(persisted);
		persisted.setFilType(FilTypeCode.KVITTERING_PRINT);

		filInfoRepository.updateFilInfo(persisted);

		FilInfo foundFilInfo = filInfoRepository.findFilInfoById(persisted.getFilInfoId());
		assertThat(foundFilInfo.getFilType(), is(FilTypeCode.KVITTERING_PRINT));
	}

	@Test
	public void shouldFindFilInfo() {
		FilInfo persistedFilInfo = buildAndPersistFilInfo();

		FilInfo foundFilInfo = filInfoRepository.findFilInfoById(persistedFilInfo.getFilInfoId());

		assertThat(foundFilInfo, is(persistedFilInfo));
	}

	@Test
	public void shouldNotFindFilInfoAndReturnNull() {
		FilInfo foundFilInfo = filInfoRepository.findFilInfoById(100L);

		assertThat(foundFilInfo, is(nullValue()));
	}

	@Test
	public void shouldFindFilInfoByFilnavn() {
		FilInfo persistedFilInfo = buildAndPersistFilInfo();
		FilInfo foundFilInfo = filInfoRepository.findFilInfoByFilnavn(FILNAVN);

		assertThat(foundFilInfo, is(persistedFilInfo));
	}

	@Test
	public void shouldSetFilInfoStatus() {
		FilInfo persistedFilInfo = buildAndPersistFilInfo();
		filInfoRepository.updateFilInfoStatus(persistedFilInfo.getFilInfoId(), FilStatusCode.FEILET);
		FilInfo foundFilInfo = filInfoRepository.findFilInfoById(persistedFilInfo.getFilInfoId());

		assertThat(foundFilInfo.getFilStatus(), is(FilStatusCode.FEILET));
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
