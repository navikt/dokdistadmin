package no.nav.dokdistadmin.administrerforsendelse;

import no.nav.dokdistadmin.administrerforsendelse.post.HentPostdestinasjonResponse;
import no.nav.dokdistadmin.administrerforsendelse.post.OppdaterPostadresseRequest;
import no.nav.dokdistadmin.config.AbstractITest;
import no.nav.dokdistadmin.domain.LandkodePostDest;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import no.nav.dokdistadmin.repository.LandkodePostDestRepository;
import no.nav.dokdistadmin.repository.PostadresseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static java.lang.String.format;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfoWithEkspedertDato;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PostITest extends AbstractITest {

	private static final String HENTPOSTDESTINASJON_URI = "/rest/v1/administrerforsendelse/hentpostdestinasjon";
	private static final String OPPDATERPOSTADRESSE_URI = "/rest/v1/administrerforsendelse/oppdaterpostadresse";

	private static final String LANDKODE_NORGE = "NO";
	private static final String LANDKODE_SVERIGE = "SE";
	private static final String POSTDESTINASJON = "INNLAND";

	@Autowired
	LandkodePostDestRepository landkodePostDestRepository;

	@Autowired
	DokumentInfoRepository dokumentInfoRepository;

	@Autowired
	PostadresseRepository postadresseRepository;

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

	@Test
	void skalOppdaterePostadresse() {
		var distribusjonInfo = dokumentDistribusjonRepository.save(createDistribusjonInfo());

		no.nav.dokdistadmin.domain.Postadresse postadresse = no.nav.dokdistadmin.domain.Postadresse.builder()
				.adresselinje1("adr1")
				.adresselinje2("adr2")
				.adresselinje3("adr3")
				.postnummer("postnr")
				.poststed("poststed")
				.landkode("landkode")
				.build();
		var dokumentInfo = createDokumentInfoWithEkspedertDato(LocalDateTime.now().minusSeconds(1));
		dokumentInfo.setPostadresse(postadresse);
		distribusjonInfo.addDokumentInfo(dokumentInfo);
		dokumentDistribusjonRepository.save(distribusjonInfo);

		commitAndBeginNewTransaction();

		var forsendelseId = dokumentInfoRepository.findAll().iterator().next().getDokumentInfoId();

		OppdaterPostadresseRequest request = OppdaterPostadresseRequest.builder()
				.forsendelseId(forsendelseId)
				.adresselinje1("adresselinje1")
				.adresselinje2("adresselinje2")
				.adresselinje3("adresselinje3")
				.postnummer("6065")
				.poststed("Ulstein")
				.landkode("NO")
				.build();

		webTestClient.put()
				.uri(OPPDATERPOSTADRESSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk();
		System.out.println("hei");
	}
}
