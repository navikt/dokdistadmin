package no.nav.dokdistadmin.domain.builder;

import no.nav.dokdistadmin.domain.ChangeStamp;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.Postadresse;

public class PostadresseBuilder extends Builder<Postadresse> {
	private Long postadresseId;
	private long version;
	private String adresselinje1;
	private String adresselinje2;
	private String adresselinje3;
	private String postnummer;
	private String poststed;
	private String landkode;
	private DokumentInfo dokumentInfo;
	private ChangeStamp changeStamp;

	private PostadresseBuilder() {
	}

	public static PostadresseBuilder with() {
		return new PostadresseBuilder();
	}


	public PostadresseBuilder postadresseId(Long postadresseId) {
		this.postadresseId = postadresseId;
		return this;
	}

	public PostadresseBuilder version(Long version) {
		this.version = version;
		return this;
	}

	public PostadresseBuilder adresselinje1(String adresselinje1) {
		this.adresselinje1 = adresselinje1;
		return this;
	}

	public PostadresseBuilder adresselinje2(String adresselinje2) {
		this.adresselinje2 = adresselinje2;
		return this;
	}

	public PostadresseBuilder adresselinje3(String adresselinje3) {
		this.adresselinje3 = adresselinje3;
		return this;
	}

	public PostadresseBuilder postnummer(String postnummer) {
		this.postnummer = postnummer;
		return this;
	}

	public PostadresseBuilder poststed(String poststed) {
		this.poststed = poststed;
		return this;
	}

	public PostadresseBuilder landkode(String landkode) {
		this.landkode = landkode;
		return this;
	}

	public PostadresseBuilder dokumentInfo(DokumentInfo dokumentInfo) {
		this.dokumentInfo = dokumentInfo;
		return this;
	}

	public PostadresseBuilder changeStamp(ChangeStamp changeStamp) {
		this.changeStamp = changeStamp;
		return this;
	}

	@Override
	public Postadresse build() {
		Postadresse postadresse = new Postadresse(postadresseId, version);
		postadresse.setAdresselinje1(adresselinje1);
		postadresse.setAdresselinje2(adresselinje2);
		postadresse.setAdresselinje3(adresselinje3);
		postadresse.setPostnummer(postnummer);
		postadresse.setPoststed(poststed);
		postadresse.setLandkode(landkode);
		postadresse.setDokumentInfo(dokumentInfo);
		postadresse.setChangeStamp(changeStamp);
		return postadresse;
	}
}
