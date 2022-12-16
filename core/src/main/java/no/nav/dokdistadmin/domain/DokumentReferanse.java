package no.nav.dokdistadmin.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Domain object for DokumentReferanse.
 *
 * @author Joakim Bjørnstad, Visma Consulting
 */
@Entity
@Table(name = "DOKUMENT_REFERANSE")
public class DokumentReferanse extends AbstractDomainObject {

	/**
	 * Serialization UID
	 */
	private static final long serialVersionUID = 7594297889066501383L;

	/**
	 * Sequence definition for this entity.
	 */
	private static final String DOKUMENT_REFERANSE_SEQ = "DOKUMENT_REFERANSE_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DOKUMENT_REFERANSE_SEQ)
	@SequenceGenerator(name = DOKUMENT_REFERANSE_SEQ, sequenceName = DOKUMENT_REFERANSE_SEQ, allocationSize = 1)
	@Column(name = "dokument_referanse_id", nullable = false)
	private Long dokumentReferanseId;

	@Column(name = "dokument_uri", nullable = false)
	private String dokumentUri;

	@Column(name = "fil_storrelse")
	private Long filStorrelse;

	@Column(name = "rekkefolge")
	private Integer rekkefolge;

	@Column(name = "arkiv_dokument_info_id")
	private String arkivDokumentInfoId;

	@Column(name = "dokumenttype_id")
	private String dokumenttypeId;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_refererer_til", nullable = false)
	private RefererTilCode refererTil;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dokument_info_id", nullable = false)
	private DokumentInfo dokumentInfo;

	/**
	 * Constructs a new DokumentReferanse.
	 */
	public DokumentReferanse() {
		//DokumentReferanse-objekter bygges med DokumentReferanseBuilder
	}

	/**
	 * Constructs a new DokumentReferanse.
	 *
	 * @param dokumentReferanseId The ID.
	 * @param version The version.
	 */
	public DokumentReferanse(Long dokumentReferanseId, long version) {
		this.dokumentReferanseId = dokumentReferanseId;
		setVersion(version);
	}

	/**
	 * Getter for the dokumentReferanseId property.
	 *
	 * @return the dokumentReferanseId
	 */
	public Long getDokumentReferanseId() {
		return dokumentReferanseId;
	}

	/**
	 * Getter for the dokumentUri property.
	 *
	 * @return the dokumentUri
	 */
	public String getDokumentUri() {
		return dokumentUri;
	}

	/**
	 * Setter for the dokumentUri property.
	 *
	 * @param dokumentUri the dokumentUri to set
	 */
	public void setDokumentUri(String dokumentUri) {
		this.dokumentUri = dokumentUri;
	}

	/**
	 * Getter for the filStorrelse property.
	 *
	 * @return the filStorrelse
	 */
	public Long getFilStorrelse() {
		return filStorrelse;
	}

	/**
	 * Setter for the filStorrelse property.
	 *
	 * @param filStorrelse the filStorrelse to set
	 */
	public void setFilStorrelse(Long filStorrelse) {
		this.filStorrelse = filStorrelse;
	}

	/**
	 * Getter for the refererTil property.
	 *
	 * @return the refererTil
	 */
	public RefererTilCode getRefererTil() {
		return refererTil;
	}

	/**
	 * Setter for the refererTil property.
	 *
	 * @param refererTil the refererTil to set
	 */
	public void setRefererTil(RefererTilCode refererTil) {
		this.refererTil = refererTil;
	}

	/**
	 * Getter for the rekkefolge property.
	 *
	 * @return the rekkefolge
	 */
	public Integer getRekkefolge() {
		return rekkefolge;
	}

	/**
	 * Setter for the rekkefolge property.
	 *
	 * @param rekkefolge the rekkefolge to set
	 */
	public void setRekkefolge(Integer rekkefolge) {
		this.rekkefolge = rekkefolge;
	}

	/**
	 * Getter for the arkivDokumentInfoId property.
	 *
	 * @return the arkivDokumentInfoId
	 */
	public String getArkivDokumentInfoId() {
		return arkivDokumentInfoId;
	}

	/**
	 * Setter for the arkivDokumentInfoId property.
	 *
	 * @param arkivDokumentInfoId the arkivDokumentInfoId to set
	 */
	public void setArkivDokumentInfoId(String arkivDokumentInfoId) {
		this.arkivDokumentInfoId = arkivDokumentInfoId;
	}

	/**
	 * Getter for the dokumenttypeId property.
	 *
	 * @return the dokumenttypeId
	 */
	public String getDokumenttypeId() {
		return dokumenttypeId;
	}

	/**
	 * Setter for the dokumenttypeId property.
	 *
	 * @param dokumenttypeId the dokumenttypeId to set
	 */
	public void setDokumenttypeId(String dokumenttypeId) {
		this.dokumenttypeId = dokumenttypeId;
	}

	/**
	 * Getter for the dokumentInfo property.
	 *
	 * @return the dokumentInfo
	 */
	public DokumentInfo getDokumentInfo() {
		return dokumentInfo;
	}

	/**
	 * Setter for the dokumentInfo property.
	 *
	 * @param dokumentInfo the dokumentInfo to set
	 */
	public void setDokumentInfo(DokumentInfo dokumentInfo) {
		this.dokumentInfo = dokumentInfo;
	}
}
