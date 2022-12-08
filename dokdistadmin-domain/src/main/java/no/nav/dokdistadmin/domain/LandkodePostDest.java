package no.nav.dokdistadmin.domain;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Mapping for LANDKODE_POST_DEST table
 *
 * @author Andreas Skomedal, Visma Consulting
 */
@Entity
@Immutable
@Table(name = "K_LANDKODE_POST_DEST")
public class LandkodePostDest {

	@Id
	@Column(name = "k_landkode", nullable = false, updatable = false)
	private String landkode;

	@Column(name = "post_dest", nullable = false, updatable = false)
	private String postDest;

	public String getLandkode() {
		return landkode;
	}

	public void setLandkode(String landkode) {
		this.landkode = landkode;
	}

	public String getPostDest() {
		return postDest;
	}

	public void setPostDest(String postDest) {
		this.postDest = postDest;
	}
}

