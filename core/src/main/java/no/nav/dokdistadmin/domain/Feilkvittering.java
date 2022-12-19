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
import java.time.LocalDateTime;

@Entity
@Table(name = "FEILKVITTERING")
public class Feilkvittering extends AbstractDomainObject {

	/**
	 * Serialization UID
	 */
	private static final long serialVersionUID = 8794614861864861348L;

	/**
	 * Sequence definition for this entity.
	 */
	private static final String FEILKVITTERING_SEQ = "FEILKVITTERING_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = FEILKVITTERING_SEQ)
	@SequenceGenerator(name = FEILKVITTERING_SEQ, sequenceName = FEILKVITTERING_SEQ, allocationSize = 1)
	@Column(name = "feilkvittering_id", nullable = false)
	private Long feilkvitteringId;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_feil_type")
	private FeilTypeCode feiltype;

	@Column(name = "feilpart")
	private String feilpart;

	@Column(name = "detaljer")
	private String detaljer;

	@Column(name = "feilet_tidspunkt")
	private LocalDateTime feiletTidspunkt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dokument_info_id", nullable = false)
	private DokumentInfo dokumentInfo;


	/**
	 * Constructs a new Feilkvittering
	 */
	public Feilkvittering() {
	}

	/**
	 * Constructs a new Feilkvittering.
	 *
	 * @param feilkvitteringId The ID.
	 * @param version The version.
	 */
	public Feilkvittering(Long feilkvitteringId, long version) {
		this.feilkvitteringId = feilkvitteringId;
		setVersion(version);
	}

	/**
	 * Getter for property 'feilkvitteringId'.
	 *
	 * @return Value for property 'feilkvitteringId'.
	 */
	public Long getFeilkvitteringId() {
		return feilkvitteringId;
	}

	/**
	 * Getter for property 'feiltype'.
	 *
	 * @return Value for property 'feiltype'.
	 */
	public FeilTypeCode getFeiltype() {
		return feiltype;
	}

	/**
	 * Setter for property 'feiltype'.
	 *
	 * @param feiltype Value to set for property 'feiltype'.
	 */
	public void setFeiltype(FeilTypeCode feiltype) {
		this.feiltype = feiltype;
	}

	/**
	 * Getter for property 'feilpart'.
	 *
	 * @return Value for property 'feilpart'.
	 */
	public String getFeilpart() {
		return feilpart;
	}

	/**
	 * Setter for property 'feilpart'.
	 *
	 * @param feilpart Value to set for property 'feilpart'.
	 */
	public void setFeilpart(String feilpart) {
		this.feilpart = feilpart;
	}

	/**
	 * Getter for property 'detaljer'.
	 *
	 * @return Value for property 'detaljer'.
	 */
	public String getDetaljer() {
		return detaljer;
	}

	/**
	 * Setter for property 'detaljer'.
	 *
	 * @param detaljer Value to set for property 'detaljer'.
	 */
	public void setDetaljer(String detaljer) {
		this.detaljer = detaljer;
	}

	/**
	 * Getter for property 'feiletTidspunkt'.
	 *
	 * @return Value for property 'feiletTidspunkt'.
	 */
	public LocalDateTime getFeiletTidspunkt() {
		return feiletTidspunkt;
	}

	/**
	 * Setter for property 'feiletTidspunkt'.
	 *
	 * @param feiletTidspunkt Value to set for property 'feiletTidspunkt'.
	 */
	public void setFeiletTidspunkt(LocalDateTime feiletTidspunkt) {
		this.feiletTidspunkt = feiletTidspunkt;
	}

	/**
	 * Getter for property 'dokumentInfo'.
	 *
	 * @return Value for property 'dokumentInfo'.
	 */
	public DokumentInfo getDokumentInfo() {
		return dokumentInfo;
	}

	/**
	 * Setter for property 'dokumentInfo'.
	 *
	 * @param dokumentInfo Value to set for property 'dokumentInfo'.
	 */
	public void setDokumentInfo(DokumentInfo dokumentInfo) {
		this.dokumentInfo = dokumentInfo;
	}
}
