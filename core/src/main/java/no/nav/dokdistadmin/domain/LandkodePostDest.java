package no.nav.dokdistadmin.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Immutable
@Table(name = "K_LANDKODE_POST_DEST")
public class LandkodePostDest {

	@Id
	@Column(name = "k_landkode", nullable = false, updatable = false, length = 20)
	private String landkode;

	@Column(name = "post_dest", nullable = false, updatable = false, length = 20)
	private String postDest;

}

