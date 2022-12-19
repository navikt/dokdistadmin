package no.nav.dokdistadmin.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Immutable
@Table(name = "K_LANDKODE_POST_DEST")
public class LandkodePostDest {

	@Id
	@Column(name = "k_landkode", nullable = false, updatable = false)
	private String landkode;

	@Column(name = "post_dest", nullable = false, updatable = false)
	private String postDest;

}

