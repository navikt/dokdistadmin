package no.nav.dokdistadmin.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Domain object for FilInfo.
 *
 * @author Thomas Eugen Bjørge, Visma Consulting
 */
@Entity
@Table(name = "FIL_INFO")
public class FilInfo extends AbstractDomainObject {

	/**
	 * Serialization UID
	 */
	private static final long serialVersionUID = 5843997999899784645L;

	/**
	 * Sequence definition for this entity.
	 */
	private static final String FIL_INFO_SEQ = "FIL_INFO_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = FIL_INFO_SEQ)
	@SequenceGenerator(name = FIL_INFO_SEQ, sequenceName = FIL_INFO_SEQ, allocationSize = 1)
	@Column(name = "fil_info_id", nullable = false)
	private Long filInfoId;

	@Column(name = "filnavn", nullable = false)
	private String filnavn;

	@Column(name = "mottatt_dato")
	private LocalDateTime mottattDato;

	@Column(name = "sendt_dato")
	private LocalDateTime sendtDato;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_fil_type", nullable = false)
	private FilTypeCode filType;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_komm_retning", nullable = false)
	private KommunikasjonRetningCode kommunikasjonRetning;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_fil_status", nullable = false)
	private FilStatusCode filStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_kilde_type", nullable = false)
	private KildeTypeCode kildeType;

	@ManyToMany(mappedBy = "filInfos")
	private Set<DistribusjonInfo> distribusjonInfos = new HashSet<>();

	@ManyToMany(mappedBy = "filInfos")
	private Set<DokumentInfo> dokumentInfos = new HashSet<>();

	/**
	 * Constructs a new FilInfo.
	 */
	public FilInfo() {
	}

	/**
	 * Constructs a new FilInfo.
	 *
	 * @param filInfoId The ID.
	 * @param version The version.
	 */
	public FilInfo(Long filInfoId, long version) {
		this.filInfoId = filInfoId;
		setVersion(version);
	}

	/**
	 * Add distribjusjonsInfo
	 *
	 * @param distribusjonInfo The distribusjonInfo
	 */
	public void addDistribusjonInfo(DistribusjonInfo distribusjonInfo) {
		if (distribusjonInfo != null) {
			distribusjonInfos.add(distribusjonInfo);
		}
	}

	/**
	 * Getter for the distribusjonInfos property.
	 *
	 * @return the distribusjonInfos
	 */
	public Set<DistribusjonInfo> getDistribusjonInfos() {
		return Collections.unmodifiableSet(distribusjonInfos);
	}

	/**
	 * Add dokunmentInfo
	 *
	 * @param dokumentInfo The dokumentInfo
	 */
	public void addDokumentInfo(DokumentInfo dokumentInfo) {
		if (dokumentInfo != null) {
			dokumentInfos.add(dokumentInfo);
		}
	}

	/**
	 * Getter for the dokumentInfos property.
	 *
	 * @return the dokumentInfos
	 */
	public Set<DokumentInfo> getDokumentInfos() {
		return Collections.unmodifiableSet(dokumentInfos);
	}

	/**
	 * Getter for the filnavn property.
	 *
	 * @return the filnavn
	 */
	public String getFilnavn() {
		return filnavn;
	}

	/**
	 * Setter for the filnavn property.
	 *
	 * @param filnavn the filnavn to set
	 */
	public void setFilnavn(String filnavn) {
		this.filnavn = filnavn;
	}

	/**
	 * Getter for the mottattDato property.
	 *
	 * @return the mottattDato
	 */
	public LocalDateTime getMottattDato() {
		return mottattDato;
	}

	/**
	 * Setter for the mottattDato property.
	 *
	 * @param mottattDato the mottattDato to set
	 */
	public void setMottattDato(LocalDateTime mottattDato) {
		this.mottattDato = mottattDato;
	}

	/**
	 * Getter for the sendtDato property.
	 *
	 * @return the sendtDato
	 */
	public LocalDateTime getSendtDato() {
		return sendtDato;
	}

	/**
	 * Setter for the sendtDato property.
	 *
	 * @param sendtDato the sendtDato to set
	 */
	public void setSendtDato(LocalDateTime sendtDato) {
		this.sendtDato = sendtDato;
	}

	/**
	 * Getter for the filType property.
	 *
	 * @return the filType
	 */
	public FilTypeCode getFilType() {
		return filType;
	}

	/**
	 * Setter for the filType property.
	 *
	 * @param filType the filType to set
	 */
	public void setFilType(FilTypeCode filType) {
		this.filType = filType;
	}

	/**
	 * Getter for the kommunikasjonRetning property.
	 *
	 * @return the kommunikasjonRetning
	 */
	public KommunikasjonRetningCode getKommunikasjonRetning() {
		return kommunikasjonRetning;
	}

	/**
	 * Setter for the kommunikasjonRetning property.
	 *
	 * @param kommunikasjonRetning the kommunikasjonRetning to set
	 */
	public void setKommunikasjonRetning(KommunikasjonRetningCode kommunikasjonRetning) {
		this.kommunikasjonRetning = kommunikasjonRetning;
	}

	/**
	 * Getter for the filStatus property.
	 *
	 * @return the filStatus
	 */
	public FilStatusCode getFilStatus() {
		return filStatus;
	}

	/**
	 * Setter for the filStatus property.
	 *
	 * @param filStatus the filStatus to set
	 */
	public void setFilStatus(FilStatusCode filStatus) {
		this.filStatus = filStatus;
	}

	/**
	 * Getter for the kildeType property.
	 *
	 * @return the kildeType
	 */
	public KildeTypeCode getKildeType() {
		return kildeType;
	}

	/**
	 * Setter for the kildeType property.
	 *
	 * @param kildeType the kildeType to set
	 */
	public void setKildeType(KildeTypeCode kildeType) {
		this.kildeType = kildeType;
	}

	/**
	 * Getter for the filInfoId property.
	 *
	 * @return the filInfoId
	 */
	public Long getFilInfoId() {
		return filInfoId;
	}

}
