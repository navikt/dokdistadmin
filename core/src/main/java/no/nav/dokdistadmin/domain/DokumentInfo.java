package no.nav.dokdistadmin.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REFRESH;
import static lombok.AccessLevel.NONE;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "DOKUMENT_INFO")
public class DokumentInfo extends AbstractDomainObject {

	private static final long serialVersionUID = 8076259094807605880L;
	private static final String DOK_INFO_SEQ = "DOKUMENT_INFO_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DOK_INFO_SEQ)
	@SequenceGenerator(name = DOK_INFO_SEQ, sequenceName = DOK_INFO_SEQ, allocationSize = 1)
	@Column(name = "dokument_info_id", nullable = false)
	@Setter(NONE)
	private Long dokumentInfoId;

	@Column(name = "dokument_id", nullable = false)
	private String dokumentId;

	@Column(name = "mottaker_id")
	private String mottakerId;

	@Column(name = "mottaker_navn")
	private String mottakerNavn;

	@Column(name = "arkivkode")
	private String arkivkode;

	@Column(name = "k_best_fagsystem")
	private String bestillendeFagsystem;

	@Column(name = "avstemt_arkiv_dato")
	private LocalDateTime avstemtArkivDato;

	@Column(name = "antall_sider")
	private Integer antallSider;

	@Column(name = "antall_ark")
	private Integer antallArk;

	@Column(name = "ekspedert_dato")
	private LocalDateTime ekspedertDato;

	@Column(name = "brevkode")
	private String brevkode;

	@Column(name = "konversasjon_id")
	private String konversasjonId;

	@Column(name = "avsender_id")
	private String avsenderId;

	@Column(name = "digital_distributor_id")
	private String digitalDistributorId;

	@Column(name = "digital_postkasse_adresse")
	private String digitalPostkasseAdresse;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_mottaker_id_type")
	private MottakerIdTypeCode mottakerIdType;

	@Column(name = "forsendelse_tittel")
	private String forsendelseTittel;

	@Column(name = "batch_id")
	private String batchId;

	@Column(name = "apningskvittering")
	@Type(type = "org.hibernate.type.TrueFalseType")
	private Boolean apningskvittering;

	@Column(name = "aapnetdato")
	private LocalDateTime aapnetDato;

	@Column(name = "virkningsdato")
	private LocalDateTime virkningsdato;

	@Column(name="avstemt_dato")
	private LocalDateTime avstemtDato;

	@Column(name="avstemt_referanse")
	private String avstemtReferanse;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_dokument_status", nullable = false)
	private DokumentStatusCode dokumentStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_fagomrade")
	private FagomradeCode fagomrade;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_arkiv_system")
	private ArkivSystemCode arkivSystem;

	@Column(name = "k_brev_prod_app")
	private String brevProduksjonApplikasjon;

	@Enumerated(EnumType.STRING)
	@Column(name = "k_sikkerhetsniva")
	private SikkerhetsnivaCode sikkerhetsniva;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "distribusjon_info_id", nullable = false)
	private DistribusjonInfo distribusjonInfo;

	@OneToOne(cascade = {PERSIST, MERGE, REFRESH})
	@JoinColumn(name = "postadresse_id")
	private Postadresse postadresse;

	@OneToMany(mappedBy = "dokumentInfo", cascade = {PERSIST, MERGE, REFRESH})
	private Set<DokumentReferanse> dokumentReferanses = new HashSet<>();

	@OneToMany(mappedBy = "dokumentInfo", cascade = {PERSIST, MERGE, REFRESH})
	private Set<VarselInfo> varselInfos = new HashSet<>();

	@ManyToMany(cascade = {PERSIST, MERGE, REFRESH})
	@JoinTable(
			name = "DOK_INFO_FIL_INFO",
			joinColumns = {@JoinColumn(name = "dokument_info_id")},
			inverseJoinColumns = {@JoinColumn(name = "fil_info_id")})
	private Set<FilInfo> filInfos = new HashSet<>();

	@OneToMany(mappedBy = "dokumentInfo", cascade = {PERSIST, MERGE, REFRESH})
	private Set<Feilkvittering> feilkvitterings = new HashSet<>();


	public DokumentInfo(Long dokumentInfoId, long version) {
		this.dokumentInfoId = dokumentInfoId;
		setVersion(version);
	}

	public void assignInitialValues() {
		this.dokumentStatus = DokumentStatusCode.OPPRETTET;
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

	public void addFilInfo(FilInfo filInfo) {
		if (filInfo != null) {
			this.filInfos.add(filInfo);
		}
	}

	public Set<FilInfo> getFilInfos() {
		return Collections.unmodifiableSet(filInfos);
	}

	public void addFeilkvittering(Feilkvittering feilkvittering) {
		if (feilkvittering != null) {
			this.feilkvitterings.add(feilkvittering);
			feilkvittering.setDokumentInfo(this);
		}
	}

	public Set<Feilkvittering> getFeilkvitterings() {
		return Collections.unmodifiableSet(feilkvitterings);
	}
}
