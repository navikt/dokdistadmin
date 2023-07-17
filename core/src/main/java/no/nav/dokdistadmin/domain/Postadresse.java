package no.nav.dokdistadmin.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serial;

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "POSTADRESSE")
public class Postadresse extends AbstractDomainObject {

	@Serial
	private static final long serialVersionUID = -874551566707739121L;
	private static final String POSTADRESSE_SEQ = "POSTADRESSE_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = POSTADRESSE_SEQ)
	@SequenceGenerator(name = POSTADRESSE_SEQ, sequenceName = POSTADRESSE_SEQ, allocationSize = 1)
	@Column(name = "postadresse_id", nullable = false)
	@Setter(NONE)
	private Long postadresseId;

	@Column(name = "adresselinje1", length = 200)
	private String adresselinje1;

	@Column(name = "adresselinje2", length = 200)
	private String adresselinje2;

	@Column(name = "adresselinje3", length = 200)
	private String adresselinje3;

	@Column(name = "postnummer", length = 20)
	private String postnummer;

	@Column(name = "poststed", length = 200)
	private String poststed;

	@Column(name = "k_landkode", nullable = false, length = 20)
	private String landkode;

	@OneToOne(mappedBy = "postadresse")
	private DokumentInfo dokumentInfo;

	public Postadresse(Long postadresseId, long version) {
		this.postadresseId = postadresseId;
		setVersion(version);
	}
}
