package no.nav.dokdistadmin.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static lombok.AccessLevel.NONE;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "DISTRIBUSJON_INFO")
public class DistribusjonInfo extends AbstractDomainObject {

	@Serial
	private static final long serialVersionUID = -5070446598451545062L;
	private static final String DIST_INFO_SEQ = "DISTRIBUSJON_INFO_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DIST_INFO_SEQ)
	@SequenceGenerator(name = DIST_INFO_SEQ, sequenceName = DIST_INFO_SEQ, allocationSize = 1)
	@Column(name = "distribusjon_info_id", nullable = false)
	@Setter(NONE)
	private Long distribusjonInfoId;

	@Column(name = "distribusjon_id", nullable = false)
	private String distribusjonId;

	@Column(name = "avtale_referanse", length = 20)
	private String avtaleReferanse;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_distribusjonstype", length = 20)
	private DistribusjonsTypeKode distribusjonstype;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_distribusjonstidspunkt", length = 20)
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
	@Column(name = "k_dist_status", nullable = false, length = 20)
	private DistribusjonStatusCode distribusjonStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_modus", nullable = false, length = 20)
	private ModusCode modus;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_kanal_behandling", length = 20)
	private KanalBehandlingCode kanalBehandling;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_post_dest", length = 20)
	private PostDestinasjonCode postDestinasjon;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_dist_kanal", nullable = false, length = 20)
	private DistribusjonKanalCode distribusjonKanal;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_varsel_status", length = 20)
	private VarselStatusCode varselStatus;

	@Builder.Default
	@OneToMany(mappedBy = "distribusjonInfo", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	private Set<DokumentInfo> dokumentInfos = new HashSet<>();

	@Builder.Default
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	@JoinTable(
			name = "DIST_INFO_FIL_INFO",
			joinColumns = {@JoinColumn(name = "distribusjon_info_id")},
			inverseJoinColumns = {@JoinColumn(name = "fil_info_id")})
	private Set<FilInfo> filInfos = new HashSet<>();

	public DistribusjonInfo(Long distribusjonInfoId, long version) {
		this.distribusjonInfoId = distribusjonInfoId;
		setVersion(version);
	}

	public void addDokumentInfo(DokumentInfo dokumentInfo) {
		if (dokumentInfo != null) {
			this.dokumentInfos.add(dokumentInfo);
			dokumentInfo.setDistribusjonInfo(this);
		}
	}

	public Set<DokumentInfo> getDokumentInfos() {
		return Collections.unmodifiableSet(dokumentInfos);
	}

	public void addFilInfo(FilInfo filInfo) {
		if (filInfo != null) {
			this.filInfos.add(filInfo);
		}
	}

}
