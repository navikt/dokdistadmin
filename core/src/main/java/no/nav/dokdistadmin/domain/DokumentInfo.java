package no.nav.dokdistadmin.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;
import static lombok.AccessLevel.NONE;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "DOKUMENT_INFO")
public class DokumentInfo extends AbstractDomainObject {

	@Serial
	private static final long serialVersionUID = 8076259094807605880L;
	private static final String DOK_INFO_SEQ = "DOKUMENT_INFO_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DOK_INFO_SEQ)
	@SequenceGenerator(name = DOK_INFO_SEQ, sequenceName = DOK_INFO_SEQ, allocationSize = 1)
	@Column(name = "dokument_info_id", nullable = false)
	@Setter(NONE)
	private Long dokumentInfoId;

	@Column(name = "dokument_id", nullable = false, length = 40)
	private String dokumentId;

	@Column(name = "mottaker_id", length = 20)
	private String mottakerId;

	@Column(name = "mottaker_navn", length = 200)
	private String mottakerNavn;

	@Column(name = "arkivkode", length = 40)
	private String arkivkode;

	@Column(name = "k_best_fagsystem", length = 20)
	private String bestillendeFagsystem;

	@Column(name = "avstemt_arkiv_dato")
	private LocalDateTime avstemtArkivDato;

	@Column(name = "antall_sider")
	private Integer antallSider;

	@Column(name = "antall_ark")
	private Integer antallArk;

	@Column(name = "ekspedert_dato")
	private LocalDateTime ekspedertDato;

	@Column(name = "brevkode", length = 20)
	private String brevkode;

	@Column(name = "konversasjon_id", length = 50)
	private String konversasjonId;

	@Column(name = "avsender_id", length = 20)
	private String avsenderId;

	@Column(name = "digital_distributor_id", length = 20)
	private String digitalDistributorId;

	@Column(name = "digital_postkasse_adresse", length = 100)
	private String digitalPostkasseAdresse;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_mottaker_id_type", length = 20)
	private MottakerIdTypeCode mottakerIdType;

	@Column(name = "forsendelse_tittel", length = 1500)
	private String forsendelseTittel;

	@Column(name = "batch_id", length = 100)
	private String batchId;

	@Column(name = "virkningsdato")
	private LocalDateTime virkningsdato;

	@Column(name="avstemt_dato")
	private LocalDateTime avstemtDato;

	@Column(name="avstemt_referanse", length = 20)
	private String avstemtReferanse;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_dokument_status", nullable = false, length = 20)
	private DokumentStatusCode dokumentStatus;

	@Column(name = "k_fagomrade", length = 20)
	private String fagomrade;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_arkiv_system", length = 20)
	private ArkivSystemCode arkivSystem;

	@Column(name = "k_brev_prod_app", length = 256)
	private String brevProduksjonApplikasjon;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_sikkerhetsniva", length = 20)
	private SikkerhetsnivaCode sikkerhetsniva;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "distribusjon_info_id", nullable = false)
	private DistribusjonInfo distribusjonInfo;

	@OneToOne(cascade = {PERSIST, MERGE, REFRESH})
	@JoinColumn(name = "postadresse_id")
	private Postadresse postadresse;

	@Builder.Default
	@OneToMany(mappedBy = "dokumentInfo", cascade = {PERSIST, MERGE, REFRESH})
	private Set<DokumentReferanse> dokumentReferanses = new HashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "dokumentInfo", cascade = {PERSIST, MERGE, REFRESH})
	private Set<VarselInfo> varselInfos = new HashSet<>();

	@Builder.Default
	@ManyToMany(cascade = {PERSIST, MERGE, REFRESH})
	@JoinTable(
			name = "DOK_INFO_FIL_INFO",
			joinColumns = {@JoinColumn(name = "dokument_info_id")},
			inverseJoinColumns = {@JoinColumn(name = "fil_info_id")})
	private Set<FilInfo> filInfos = new HashSet<>();

	@Builder.Default
	@OneToMany(mappedBy = "dokumentInfo", cascade = {PERSIST, MERGE, REFRESH})
	private Set<Feilkvittering> feilkvitterings = new HashSet<>();


	public DokumentInfo(Long dokumentInfoId, long version) {
		this.dokumentInfoId = dokumentInfoId;
		setVersion(version);
	}

	public void addDokumentReferanse(DokumentReferanse dokumentReferanse) {
		if (dokumentReferanse != null) {
			this.dokumentReferanses.add(dokumentReferanse);
			dokumentReferanse.setDokumentInfo(this);
		}
	}

	public Set<DokumentReferanse> getDokumentReferanses() {
		return Collections.unmodifiableSet(dokumentReferanses);
	}

	public void addVarselInfo(VarselInfo varselInfo) {
		if (varselInfo != null) {
			this.varselInfos.add(varselInfo);
			varselInfo.setDokumentInfo(this);
		}
	}

	public Set<VarselInfo> getVarselInfos() {
		return Collections.unmodifiableSet(varselInfos);
	}

	public void addFeilkvittering(Feilkvittering feilkvittering) {
		if (feilkvittering != null) {
			this.feilkvitterings.add(feilkvittering);
			feilkvittering.setDokumentInfo(this);
		}
	}

}
