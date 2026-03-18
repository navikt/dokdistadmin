package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.config.AbstractRepositoryTest;
import no.nav.dokdistadmin.domain.LandkodePostDest;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LandkodePostDestRepositoryTest extends AbstractRepositoryTest {

	private static final String LANDKODE_NO = "NO";
	private static final String LANDKODE_SE = "SE";
	private static final String POSTDESTINASJON_INNLAND = "INNLAND";

	@Test
	void shouldFindLandkodePostDestByLandkode() {
		landkodePostDestRepository.persist(createLandkodePostdestinasjonInnland());

		var result = landkodePostDestRepository.findLandkodePostDestByLandkode(LANDKODE_NO);

		assertEquals(POSTDESTINASJON_INNLAND, result.getPostDest());
		assertEquals(LANDKODE_NO, result.getLandkode());
	}

	@Test
	void shouldNotFindLandkodePostDestByLandkode() {
		var result = landkodePostDestRepository.findLandkodePostDestByLandkode(LANDKODE_SE);

		assertThat(result).isNull();
	}

	private LandkodePostDest createLandkodePostdestinasjonInnland() {
		return LandkodePostDest.builder()
				.landkode(LANDKODE_NO)
				.postDest(POSTDESTINASJON_INNLAND)
				.build();
	}

}