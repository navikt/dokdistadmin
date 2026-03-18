package no.nav.dokdistadmin.administrerforsendelse;

import no.nav.dokdistadmin.administrerforsendelse.post.HentPostdestinasjonResponse;
import no.nav.dokdistadmin.administrerforsendelse.post.OppdaterPostadresseRequest;
import no.nav.dokdistadmin.config.AbstractITest;
import no.nav.dokdistadmin.domain.LandkodePostDest;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import no.nav.dokdistadmin.repository.LandkodePostDestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static java.lang.String.format;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.ADRESSELINJE_1;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.ADRESSELINJE_2;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.ADRESSELINJE_3;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.POSTNUMMER;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.POSTSTED;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfoWithEkspedertDato;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PostdestinasjonIT extends AbstractITest {

	private static final String HENTPOSTDESTINASJON_URI = "/rest/v1/administrerforsendelse/hentpostdestinasjon";
	private static final String OPPDATERPOSTADRESSE_URI = "/rest/v1/administrerforsendelse/oppdaterpostadresse";

	private static final String LANDKODE_NORGE = "NO";
	private static final String LANDKODE_SVERIGE = "SE";
	private static final String POSTDESTINASJON = "INNLAND";

	@Autowired
	LandkodePostDestRepository landkodePostDestRepository;

	@Autowired
	DokumentInfoRepository dokumentInfoRepository;

	@BeforeEach
	void setup() {
		var landkodePostDest = new LandkodePostDest(LANDKODE_NORGE, POSTDESTINASJON);

		landkodePostDestRepository.persist(landkodePostDest);

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
	void skalReturnereNotFoundHvisPostdestinasjonIkkeFunnet() {
		webTestClient.get()
				.uri(format(HENTPOSTDESTINASJON_URI + "/%s", LANDKODE_SVERIGE))
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isNotFound();
	}

	@Test
	void skalReturnereBadRequestHvisLandkodenErBlankEllerNull() {
		webTestClient.get()
				.uri(HENTPOSTDESTINASJON_URI + "/ ")
				.headers(headers -> headers.setBearerAuth(jwt()))
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void skalOppdaterePostadresse() {
		var distribusjonInfo = dokumentDistribusjonRepository.persist(createDistribusjonInfo());

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
		dokumentDistribusjonRepository.persist(distribusjonInfo);

		commitAndBeginNewTransaction();

		var forsendelseId = distribusjonInfo.getDokumentInfos().iterator().next().getDokumentInfoId();
		OppdaterPostadresseRequest request = createOppdaterPostadresseRequest(forsendelseId, LANDKODE_NORGE);

		webTestClient.put()
				.uri(OPPDATERPOSTADRESSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk();

		commitAndBeginNewTransaction();

		var oppdatert = dokumentInfoRepository.findById(forsendelseId);

		assertThat(oppdatert.isPresent()).isTrue();
		assertThat(oppdatert.get().getPostadresse())
				.satisfies(actual -> {
					assertThat(actual.getAdresselinje1()).isEqualTo(ADRESSELINJE_1);
					assertThat(actual.getAdresselinje2()).isEqualTo(ADRESSELINJE_2);
					assertThat(actual.getAdresselinje3()).isEqualTo(ADRESSELINJE_3);
					assertThat(actual.getPostnummer()).isEqualTo(POSTNUMMER);
					assertThat(actual.getPoststed()).isEqualTo(POSTSTED);
					assertThat(actual.getLandkode()).isEqualTo(LANDKODE_NORGE);
				});
	}

	@Test
	void skalReturnereNotFoundForOppdaterPostadresseDersomForsendelseIkkeFinnes() {
		OppdaterPostadresseRequest request = createOppdaterPostadresseRequest(123L, "NO");

		webTestClient.put()
				.uri(OPPDATERPOSTADRESSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isNotFound();
	}

	@ParameterizedTest
	@ValueSource(longs = -1)
	@NullSource
	void skalReturnereBadRequestForOppdaterPostadresseHvisForsendelseIdErUgyldig(Long forsendelseId) {
		var request = createOppdaterPostadresseRequest(forsendelseId, "NO");

		webTestClient.put()
				.uri(OPPDATERPOSTADRESSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isBadRequest();
	}

	@ParameterizedTest
	@ValueSource(strings = {"NOR", "N", "  "})
	@NullSource
	void skalReturnereBadRequestForOppdaterPostadresseHvisLandkodeErUgyldig(String landkode) {
		var request = createOppdaterPostadresseRequest(123L, landkode);

		webTestClient.put()
				.uri(OPPDATERPOSTADRESSE_URI)
				.headers(headers -> headers.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isBadRequest();
	}

	private static OppdaterPostadresseRequest createOppdaterPostadresseRequest(Long forsendelseId, String landkode) {
		return OppdaterPostadresseRequest.builder()
				.forsendelseId(forsendelseId)
				.adresselinje1(ADRESSELINJE_1)
				.adresselinje2(ADRESSELINJE_2)
				.adresselinje3(ADRESSELINJE_3)
				.postnummer(POSTNUMMER)
				.poststed(POSTSTED)
				.landkode(landkode)
				.build();
	}
}
