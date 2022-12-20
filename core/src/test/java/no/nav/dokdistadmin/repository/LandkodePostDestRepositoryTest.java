package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.config.AbstractRepositoryTest;
import no.nav.dokdistadmin.domain.LandkodePostDest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LandkodePostDestRepositoryTest extends AbstractRepositoryTest {

	private static final String LANDKODE_NO = "NO";
	private static final String POST_DESTINATION = "INNLAND";

	@Test
	void findLandkodePostDestByLandkode() {
		landkodePostDestRepository.save(createLandKodePostDestination());

		var result = landkodePostDestRepository.findLandkodePostDestByLandkode(LANDKODE_NO);

		assertEquals(POST_DESTINATION, result.getPostDest());
	}

	private LandkodePostDest createLandKodePostDestination() {
		return LandkodePostDest.builder()
				.landkode(LANDKODE_NO)
				.postDest(POST_DESTINATION)
				.build();
	}

}