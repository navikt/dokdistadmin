package no.nav.dokdistadmin.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "POSTADRESSE")
public class Postadresse extends AbstractDomainObject {

	private static final long serialVersionUID = -874551566707739121L;
	private static final String POSTADRESSE_SEQ = "POSTADRESSE_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = POSTADRESSE_SEQ)
	@SequenceGenerator(name = POSTADRESSE_SEQ, sequenceName = POSTADRESSE_SEQ, allocationSize = 1)
	@Column(name = "postadresse_id", nullable = false)
	@Setter(NONE)
	private Long postadresseId;

	@Column(name = "adresselinje1")
	private String adresselinje1;

	@Column(name = "adresselinje2")
	private String adresselinje2;

	@Column(name = "adresselinje3")
	private String adresselinje3;

	@Column(name = "postnummer")
	private String postnummer;

	@Column(name = "poststed")
	private String poststed;

	@Column(name = "k_landkode", nullable = false)
	private String landkode;

	@OneToOne(mappedBy = "postadresse")
	private DokumentInfo dokumentInfo;

	public Postadresse(Long postadresseId, long version) {
		this.postadresseId = postadresseId;
		setVersion(version);
	}
}
