package no.nav.dokdistadmin.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "DISTRIBUSJON_INFO")
public class DistribusjonInfo extends AbstractDomainObject {

	/**
	 * Serialization UID
	 */
	private static final long serialVersionUID = -5070446598451545062L;

	/**
	 * Sequence definition for this entity.
	 */
	private static final String DIST_INFO_SEQ = "DISTRIBUSJON_INFO_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DIST_INFO_SEQ)
	@SequenceGenerator(name = DIST_INFO_SEQ, sequenceName = DIST_INFO_SEQ, allocationSize = 1)
	@Column(name = "distribusjon_info_id", nullable = false)
	private Long distribusjonInfoId;

	@Column(name = "distribusjon_id", nullable = false)
	private String distribusjonId;

	@Column(name = "avtale_referanse")
	private String avtaleReferanse;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_distribusjonstype")
	private DistribusjonsTypeKode distribusjonstype;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_distribusjonstidspunkt")
	private DistribusjonstidspunktKode distribusjonstidspunkt ;

	@Column(name = "produksjon_dato")
	private LocalDateTime produksjonDato;

	@Column(name = "distribusjon_dato", nullable = false)
	private LocalDateTime distribusjonDato;

	@Column(name = "eff_tidligst_dato")
	private LocalDate effektuerTidligstDato;

	@Column(name = "eff_senest_dato")
	private LocalDate effektuerSenestDato;

	@Column(name = "bekr_mottatt_dato")
	private LocalDateTime bekreftetMottattDato;

	@Column(name = "original_distribusjon_id")
	private String originalDistribusjonId;

	@Column(name = "resending_distribusjon_id")
	private String resendingDistribusjonId;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_dist_status", nullable = false)
	private DistribusjonStatusCode distribusjonStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_modus", nullable = false)
	private ModusCode modus;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_kanal_behandling")
	private KanalBehandlingCode kanalBehandling;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_post_dest")
	private PostDestinasjonCode postDestinasjon;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_dist_kanal", nullable = false)
	private DistribusjonKanalCode distribusjonKanal;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_varsel_status")
	private VarselStatusCode varselStatus;

	@OneToMany(mappedBy = "distribusjonInfo", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	private Set<DokumentInfo> dokumentInfos = new HashSet<>();

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	@JoinTable(
			name = "DIST_INFO_FIL_INFO",
			joinColumns = {@JoinColumn(name = "distribusjon_info_id")},
			inverseJoinColumns = {@JoinColumn(name = "fil_info_id")})
	private Set<FilInfo> filInfos = new HashSet<>();

	/**
	 * Constructs a new DistribusjonInfo.
	 */
	public DistribusjonInfo() {
	}

	/**
	 * Constructs a new DistribusjonInfo.
	 *
	 * @param distribusjonInfoId The ID.
	 * @param version The version.
	 */
	public DistribusjonInfo(Long distribusjonInfoId, long version) {
		this.distribusjonInfoId = distribusjonInfoId;
		setVersion(version);
	}

	/**
	 * Add a DokumentInfo to the dokumentInfo Set and set the bidirectional reference.
	 *
	 * @param dokumentInfo The DokumentInfo to add.
	 */
	public void addDokumentInfo(DokumentInfo dokumentInfo) {
		if (dokumentInfo != null) {
			this.dokumentInfos.add(dokumentInfo);
			dokumentInfo.setDistribusjonInfo(this);
		}
	}

	public Set<DokumentInfo> getDokumentInfos() {
		return Collections.unmodifiableSet(dokumentInfos);
	}

	/**
	 * Add a FilInfo to the FilInfo Set and set the bidirectional reference.
	 *
	 * @param filInfo The FilInfo to add.
	 */
	public void addFilInfo(FilInfo filInfo) {
		if (filInfo != null) {
			this.filInfos.add(filInfo);
		}
	}

	public void assignInitialValues() {
		this.distribusjonDato = LocalDateTime.now();
		this.distribusjonStatus = DistribusjonStatusCode.OPPRETTET;
		this.modus = ModusCode.P;
		assignInitialValuesForDokumentInfo();
	}

	private void assignInitialValuesForDokumentInfo() {
		Iterator<DokumentInfo> iterator = dokumentInfos.iterator();
		if (iterator.hasNext()) {
			DokumentInfo dokumentInfo = iterator.next();
			if (dokumentInfo != null) {
				dokumentInfo.assignInitialValues();
			}
		}
	}

}
