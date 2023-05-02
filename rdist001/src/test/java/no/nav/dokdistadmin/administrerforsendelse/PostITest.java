package no.nav.dokdistadmin.administrerforsendelse;

import no.nav.dokdistadmin.administrerforsendelse.post.HentPostdestinasjonResponse;
import no.nav.dokdistadmin.config.AbstractOauth2Test;
import no.nav.dokdistadmin.config.ApplicationTestConfig;
import no.nav.dokdistadmin.config.DatabaseTest;
import no.nav.dokdistadmin.domain.LandkodePostDest;
import no.nav.dokdistadmin.repository.LandkodePostDestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Transactional
@SpringBootTest(
		classes = {ApplicationTestConfig.class},
		webEnvironment = RANDOM_PORT
)
@AutoConfigureTestDatabase
@ActiveProfiles({"itest"})
public class PostITest extends AbstractOauth2Test implements DatabaseTest {

	private static final String HENTPOSTDESTINASJON_URI = "/rest/v1/administrerforsendelse/hentpostdestinasjon";

	@Autowired
	LandkodePostDestRepository landkodePostDestRepository;

	@Autowired
	WebTestClient webTestClient;

	@BeforeEach
	void setup() {
		var landkodePostDest = new LandkodePostDest("NO", "INNLAND");

		landkodePostDestRepository.save(landkodePostDest);

		commitAndBeginNewTransaction();
	}

	@Test
	void skalFinnePostdestinasjon() {
		var landkode = "NO";

		var response = webTestClient.get()
				.uri(format(HENTPOSTDESTINASJON_URI + "/%s", landkode))
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isOk()
				.expectBody(HentPostdestinasjonResponse.class)
				.returnResult()
				.getResponseBody();

		assertNotNull(response);
	}

	@Test
	void skalKasteNotFoundHvisPostdestinasjonIkkeFunnet() {
		var landkode = "SE";

		webTestClient.get()
				.uri(format(HENTPOSTDESTINASJON_URI + "/%s", landkode))
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	void skalKastBadRequestHvisLandkodenErBlankEllerNull() {

		webTestClient.get()
				.uri(HENTPOSTDESTINASJON_URI + "/ ")
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isBadRequest();
	}
}
