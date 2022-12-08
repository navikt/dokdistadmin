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
 * Domain object for VarselInfo.
 *
 * @author Joakim Bjørnstad, Visma Consulting
 */
@Entity
@Table(name = "VARSEL_INFO")
public class VarselInfo extends AbstractDomainObject {

	/**
	 * Serialization UID
	 */
	private static final long serialVersionUID = -3793260172915981033L;

	/**
	 * Sequence definition for this entity.
	 */
	private static final String VARSEL_INFO_SEQ = "VARSEL_INFO_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VARSEL_INFO_SEQ)
	@SequenceGenerator(name = VARSEL_INFO_SEQ, sequenceName = VARSEL_INFO_SEQ, allocationSize = 1)
	@Column(name = "varsel_info_id", nullable = false)
	private Long varselInfoId;

	@Column(name = "varslingstekst")
	private String varslingstekst;

	@Column(name = "epost_adresse")
	private String epostAdresse;

	@Column(name = "mobiltelefon_nummer")
	private String mobiltelefonNummer;

	@Column(name = "antall_repetisjoner")
	private Integer antallRepetisjoner;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_varsling_kanal", nullable = false)
	private VarslingKanalCode varslingKanal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dokument_info_id", nullable = false)
	private DokumentInfo dokumentInfo;

	/**
	 * Constructs a new VarselInfo.
	 */
	public VarselInfo() {
		//VarselInfo-objekter bygges med VarselInfoBuilder
	}

	/**
	 * Constructs a new VarselInfo.
	 *
	 * @param varselInfoId The ID.
	 * @param version      The version.
	 */
	public VarselInfo(Long varselInfoId, long version) {
		this.varselInfoId = varselInfoId;
		setVersion(version);
	}

	/**
	 * Getter for the varselInfoId property.
	 *
	 * @return the varselInfoId
	 */
	public Long getVarselInfoId() {
		return varselInfoId;
	}

	/**
	 * Getter for the varslingstekst property.
	 *
	 * @return the varslingstekst
	 */
	public String getVarslingstekst() {
		return varslingstekst;
	}

	/**
	 * Setter for the varslingstekst property.
	 *
	 * @param varslingstekst the varslingstekst to set
	 */
	public void setVarslingstekst(String varslingstekst) {
		this.varslingstekst = varslingstekst;
	}

	/**
	 * Getter for the epostAdresse property.
	 *
	 * @return the epostAdresse
	 */
	public String getEpostAdresse() {
		return epostAdresse;
	}

	/**
	 * Setter for the epostAdresse property.
	 *
	 * @param epostAdresse the epostAdresse to set
	 */
	public void setEpostAdresse(String epostAdresse) {
		this.epostAdresse = epostAdresse;
	}

	/**
	 * Getter for the mobiltelefonNummer property.
	 *
	 * @return the mobiltelefonNummer
	 */
	public String getMobiltelefonNummer() {
		return mobiltelefonNummer;
	}

	/**
	 * Setter for the mobiltelefonNummer property.
	 *
	 * @param mobiltelefonNummer the mobiltelefonNummer to set
	 */
	public void setMobiltelefonNummer(String mobiltelefonNummer) {
		this.mobiltelefonNummer = mobiltelefonNummer;
	}

	/**
	 * Getter for the antallRepetisjoner property.
	 *
	 * @return the antallRepetisjoner
	 */
	public Integer getAntallRepetisjoner() {
		return antallRepetisjoner;
	}

	/**
	 * Setter for the antallRepetisjoner property.
	 *
	 * @param antallRepetisjoner the antallRepetisjoner to set
	 */
	public void setAntallRepetisjoner(Integer antallRepetisjoner) {
		this.antallRepetisjoner = antallRepetisjoner;
	}

	/**
	 * Getter for the varslingKanal property.
	 *
	 * @return the varslingKanal
	 */
	public VarslingKanalCode getVarslingKanal() {
		return varslingKanal;
	}

	/**
	 * Setter for the varslingKanal property.
	 *
	 * @param varslingKanal the varslingKanal to set
	 */
	public void setVarslingKanal(VarslingKanalCode varslingKanal) {
		this.varslingKanal = varslingKanal;
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
