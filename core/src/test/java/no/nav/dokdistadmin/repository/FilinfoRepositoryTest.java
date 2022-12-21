package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.config.AbstractRepositoryTest;
import no.nav.dokdistadmin.domain.FilInfo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static no.nav.dokdistadmin.domain.FilStatusCode.OK;
import static no.nav.dokdistadmin.domain.FilTypeCode.BEST_INFO_PRINT;
import static no.nav.dokdistadmin.domain.KildeTypeCode.HP_REXX;
import static no.nav.dokdistadmin.domain.KommunikasjonRetningCode.UTGAENDE;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FilinfoRepositoryTest extends AbstractRepositoryTest {

	private static final String FILNAVN = "filnavn";

	@Test
	void shouldSaveFilinfo() {
		var filinfo = filinfoRepository.save(createFilInfo());

		assertNotNull(filinfo.getFilInfoId());
	}

	@Test
	void shouldFindFilInfoByFilnavn() {
		filinfoRepository.save(createFilInfo());

		var result = filinfoRepository.findFilInfoByFilnavn(FILNAVN);

		assertNotNull(result);
	}

	private FilInfo createFilInfo() {
		return FilInfo.builder()
				.filnavn(FILNAVN)
				.filType(BEST_INFO_PRINT)
				.sendtDato(LocalDateTime.now())
				.kommunikasjonRetning(UTGAENDE)
				.filStatus(OK)
				.kildeType(HP_REXX)
				.build();
	}
}