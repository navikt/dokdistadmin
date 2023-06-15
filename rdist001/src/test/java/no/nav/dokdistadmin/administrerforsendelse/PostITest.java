package no.nav.dokdistadmin.administrerforsendelse;

import no.nav.dokdistadmin.administrerforsendelse.post.HentPostdestinasjonResponse;
import no.nav.dokdistadmin.config.AbstractITest;
import no.nav.dokdistadmin.domain.LandkodePostDest;
import no.nav.dokdistadmin.repository.LandkodePostDestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PostITest extends AbstractITest {

	private static final String HENTPOSTDESTINASJON_URI = "/rest/v1/administrerforsendelse/hentpostdestinasjon";
	private static final String LANDKODE_NORGE = "NO";
	private static final String LANDKODE_SVERIGE = "SE";
	private static final String POSTDESTINASJON = "INNLAND";

	@Autowired
	LandkodePostDestRepository landkodePostDestRepository;

	@BeforeEach
	void setup() {
		var landkodePostDest = new LandkodePostDest(LANDKODE_NORGE, POSTDESTINASJON);

		landkodePostDestRepository.save(landkodePostDest);

		commitAndBeginNewTransaction();
	}

	@Test
	void skalFinnePostdestinasjon() {
		var response = webTestClient.get()
				.uri(format(HENTPOSTDESTINASJON_URI + "/%s", LANDKODE_NORGE))
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isOk()
				.expectBody(HentPostdestinasjonResponse.class)
				.returnResult()
				.getResponseBody();

		assertNotNull(response);
		assertThat(response.postdestinasjon()).isEqualTo(POSTDESTINASJON);
	}

	@Test
	void skalKasteNotFoundHvisPostdestinasjonIkkeFunnet() {
		webTestClient.get()
				.uri(format(HENTPOSTDESTINASJON_URI + "/%s", LANDKODE_SVERIGE))
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
