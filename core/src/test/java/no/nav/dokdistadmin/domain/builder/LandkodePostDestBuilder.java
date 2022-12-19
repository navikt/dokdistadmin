package no.nav.dokdistadmin.domain.builder;

import no.nav.dokdistadmin.domain.LandkodePostDest;

public class LandkodePostDestBuilder extends Builder<LandkodePostDest> {

	private String landkode;
	private String postDest;

	private LandkodePostDestBuilder() {
	}

	public static LandkodePostDestBuilder with() {
		return new LandkodePostDestBuilder();
	}

	public LandkodePostDest build() {
		LandkodePostDest landkodePostDest = new LandkodePostDest();
		landkodePostDest.setLandkode(landkode);
		landkodePostDest.setPostDest(postDest);
		return landkodePostDest;
	}

	public LandkodePostDestBuilder landkode(String landkode) {
		this.landkode = landkode;
		return this;
	}

	public LandkodePostDestBuilder postDest(String postDest) {
		this.postDest = postDest;
		return this;
	}

}