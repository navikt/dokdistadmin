package no.nav.dokdistadmin.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "POSTADRESSE")
public class Postadresse extends AbstractDomainObject {

	/** Serialization UID */
	private static final long serialVersionUID = -874551566707739121L;

	/** Sequence definition for this entity. */
	private static final String POSTADRESSE_SEQ = "POSTADRESSE_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = POSTADRESSE_SEQ)
	@SequenceGenerator(name = POSTADRESSE_SEQ, sequenceName = POSTADRESSE_SEQ, allocationSize = 1)
	@Column(name = "postadresse_id", nullable = false)
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

	@OneToOne(mappedBy = "postadresse", fetch = FetchType.LAZY)
	private DokumentInfo dokumentInfo;

	public Postadresse() {
	}

	public Postadresse(Long postadresseId, long version) {
		this.postadresseId = postadresseId;
		setVersion(version);
	}

	public Long getPostadresseId() {
		return postadresseId;
	}

	public void setPostadresseId(Long postadresseId) {
		this.postadresseId = postadresseId;
	}

	public String getAdresselinje1() {
		return adresselinje1;
	}

	public void setAdresselinje1(String adresselinje1) {
		this.adresselinje1 = adresselinje1;
	}

	public String getAdresselinje2() {
		return adresselinje2;
	}

	public void setAdresselinje2(String adresselinje2) {
		this.adresselinje2 = adresselinje2;
	}

	public String getAdresselinje3() {
		return adresselinje3;
	}

	public void setAdresselinje3(String adresselinje3) {
		this.adresselinje3 = adresselinje3;
	}

	public String getPostnummer() {
		return postnummer;
	}

	public void setPostnummer(String postnummer) {
		this.postnummer = postnummer;
	}

	public String getPoststed() {
		return poststed;
	}

	public void setPoststed(String poststed) {
		this.poststed = poststed;
	}

	public String getLandkode() {
		return landkode;
	}

	public void setLandkode(String landkode) {
		this.landkode = landkode;
	}

	public DokumentInfo getDokumentInfo() {
		return dokumentInfo;
	}

	public void setDokumentInfo(DokumentInfo dokumentInfo) {
		this.dokumentInfo = dokumentInfo;
	}
}
