package no.nav.dokdistadmin.administrerforsendelse.post;

import no.nav.dokdistadmin.domain.Postadresse;

public class PostadresseMapper {

	public static Postadresse map(OppdaterPostadresseRequest request) {

		return Postadresse.builder()
				.adresselinje1(request.getAdresselinje1())
				.adresselinje2(request.getAdresselinje2())
				.adresselinje3(request.getAdresselinje3())
				.postnummer(request.getPostnummer())
				.poststed(request.getPoststed())
				.landkode(request.getLandkode())
				.build();
	}

	public static Postadresse oppdaterPostadresse(OppdaterPostadresseRequest request, Postadresse postadresse) {

		postadresse.setAdresselinje1(request.getAdresselinje1());
		postadresse.setAdresselinje2(request.getAdresselinje2());
		postadresse.setAdresselinje3(request.getAdresselinje3());
		postadresse.setPostnummer(request.getPostnummer());
		postadresse.setPoststed(request.getPoststed());
		postadresse.setLandkode(request.getLandkode());

		return postadresse;
	}
}
