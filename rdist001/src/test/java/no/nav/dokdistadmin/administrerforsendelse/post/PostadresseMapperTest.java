package no.nav.dokdistadmin.administrerforsendelse.post;

import no.nav.dokdistadmin.domain.Postadresse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PostadresseMapperTest {

	private OppdaterPostadresseRequest request;
	private Postadresse eksisterendePostadresse;

	@BeforeEach
	public void setUp() {
		request = OppdaterPostadresseRequest.builder()
				.forsendelseId(1L)
				.adresselinje1("Ny Adresselinje 1")
				.adresselinje2("Ny Adresselinje 2")
				.adresselinje3("Ny Adresselinje 3")
				.postnummer("1000")
				.poststed("Nytt Poststed")
				.landkode("NO")
				.build();

		eksisterendePostadresse = Postadresse.builder()
				.adresselinje1("Adresselinje 1")
				.adresselinje2("Adresselinje 2")
				.adresselinje3("Adresselinje 3")
				.postnummer("9999")
				.poststed("Poststed")
				.landkode("SE")
				.build();
	}

	@Test
	public void skalMappePostadresse() {
		Postadresse mapped = PostadresseMapper.map(request);

		assertThat(mapped.getAdresselinje1()).isEqualTo(request.getAdresselinje1());
		assertThat(mapped.getAdresselinje2()).isEqualTo(request.getAdresselinje2());
		assertThat(mapped.getAdresselinje3()).isEqualTo(request.getAdresselinje3());
		assertThat(mapped.getPostnummer()).isEqualTo(request.getPostnummer());
		assertThat(mapped.getPoststed()).isEqualTo(request.getPoststed());
		assertThat(mapped.getLandkode()).isEqualTo(request.getLandkode());
	}

	@Test
	public void skalOppdaterePostadresse() {
		Postadresse updatedPostadresse = PostadresseMapper.oppdaterPostadresse(request, eksisterendePostadresse);

		assertThat(updatedPostadresse.getAdresselinje1()).isEqualTo(request.getAdresselinje1());
		assertThat(updatedPostadresse.getAdresselinje2()).isEqualTo(request.getAdresselinje2());
		assertThat(updatedPostadresse.getAdresselinje3()).isEqualTo(request.getAdresselinje3());
		assertThat(updatedPostadresse.getPostnummer()).isEqualTo(request.getPostnummer());
		assertThat(updatedPostadresse.getPoststed()).isEqualTo(request.getPoststed());
		assertThat(updatedPostadresse.getLandkode()).isEqualTo(request.getLandkode());
	}
}
