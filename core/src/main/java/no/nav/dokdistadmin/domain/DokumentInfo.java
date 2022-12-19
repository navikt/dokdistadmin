package no.nav.dokdistadmin.domain;

import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
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

@Entity
@Table(name = "DOKUMENT_INFO")
public class DokumentInfo extends AbstractDomainObject {

	/**
	 * Serialization UID
	 */
	private static final long serialVersionUID = 8076259094807605880L;

	/**
	 * Sequence definition for this entity.
	 */
	private static final String DOK_INFO_SEQ = "DOKUMENT_INFO_SEQ";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DOK_INFO_SEQ)
	@SequenceGenerator(name = DOK_INFO_SEQ, sequenceName = DOK_INFO_SEQ, allocationSize = 1)
	@Column(name = "dokument_info_id", nullable = false)
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

	@Column(name="avstemt_dato",nullable = true)
	private LocalDateTime avstemtDato;

	@Column(name="avstemt_referanse",nullable = true)
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

	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	@JoinColumn(name = "postadresse_id")
	private Postadresse postadresse;

	@OneToMany(mappedBy = "dokumentInfo", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	private Set<DokumentReferanse> dokumentReferanses = new HashSet<>();

	@OneToMany(mappedBy = "dokumentInfo", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	private Set<VarselInfo> varselInfos = new HashSet<>();

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	@JoinTable(
			name = "DOK_INFO_FIL_INFO",
			joinColumns = {@JoinColumn(name = "dokument_info_id")},
			inverseJoinColumns = {@JoinColumn(name = "fil_info_id")})
	private Set<FilInfo> filInfos = new HashSet<>();

	@OneToMany(mappedBy = "dokumentInfo", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	private Set<Feilkvittering> feilkvitterings = new HashSet<>();

	/**
	 * Constructs a new DokumentInfo.
	 */
	public DokumentInfo() {
		//DokumentInfo-objekter bygges med DokumentInfoBuilder
	}

	/**
	 * Constructs a new DokumentInfo.
	 *
	 * @param dokumentInfoId The ID.
	 * @param version The version.
	 */
	public DokumentInfo(Long dokumentInfoId, long version) {
		this.dokumentInfoId = dokumentInfoId;
		setVersion(version);
	}

	public void assignInitialValues() {
		this.dokumentStatus = DokumentStatusCode.OPPRETTET;
	}

	/**
	 * Add a DokumentReferanse to the dokumentReferanses Set and set the bidirectional reference.
	 *
	 * @param dokumentReferanse The DokumentReferanse to add.
	 */
	public void addDokumentReferanse(DokumentReferanse dokumentReferanse) {
		if (dokumentReferanse != null) {
			this.dokumentReferanses.add(dokumentReferanse);
			dokumentReferanse.setDokumentInfo(this);
		}
	}

	/**
	 * Getter for the dokumentReferanses property.
	 *
	 * @return the dokumentReferanses
	 */
	public Set<DokumentReferanse> getDokumentReferanses() {
		return Collections.unmodifiableSet(dokumentReferanses);
	}

	/**
	 * Add a VarselInfo to the varselInfos Set and set the bidirectional reference.
	 *
	 * @param varselInfo The VarselInfo to add.
	 */
	public void addVarselInfo(VarselInfo varselInfo) {
		if (varselInfo != null) {
			this.varselInfos.add(varselInfo);
			varselInfo.setDokumentInfo(this);
		}
	}

	/**
	 * Getter for the varselInfos property.
	 *
	 * @return the varselInfos
	 */
	public Set<VarselInfo> getVarselInfos() {
		return Collections.unmodifiableSet(varselInfos);
	}

	/**
	 * Add a FilInfo to the FilInfo Set
	 *
	 * @param filInfo The FilInfo to add.
	 */
	public void addFilInfo(FilInfo filInfo) {
		if (filInfo != null) {
			this.filInfos.add(filInfo);
		}
	}

	/**
	 * Getter for the filInfos property.
	 *
	 * @return the filInfos
	 */
	public Set<FilInfo> getFilInfos() {
		return Collections.unmodifiableSet(filInfos);
	}

	/**
	 * Getter for the dokumentId property.
	 *
	 * @return the dokumentId
	 */
	public String getDokumentId() {
		return dokumentId;
	}

	/**
	 * Setter for the dokumentId property.
	 *
	 * @param dokumentId the dokumentId to set
	 */
	public void setDokumentId(String dokumentId) {
		this.dokumentId = dokumentId;
	}

	/**
	 * Getter for the mottakerId property.
	 *
	 * @return the mottakerId
	 */
	public String getMottakerId() {
		return mottakerId;
	}

	/**
	 * Setter for the mottakerId property.
	 *
	 * @param mottakerId the mottakerId to set
	 */
	public void setMottakerId(String mottakerId) {
		this.mottakerId = mottakerId;
	}

	/**
	 * Getter for the arkivkode property.
	 *
	 * @return the arkivkode
	 */
	public String getArkivkode() {
		return arkivkode;
	}

	/**
	 * Setter for the arkivkode property.
	 *
	 * @param arkivkode the arkivkode to set
	 */
	public void setArkivkode(String arkivkode) {
		this.arkivkode = arkivkode;
	}

	/**
	 * Getter for the avstemtArkivDato property.
	 *
	 * @return the avstemtArkivDato
	 */
	public LocalDateTime getAvstemtArkivDato() {
		return avstemtArkivDato;
	}

	/**
	 * Setter for the avstemtArkivDato property.
	 *
	 * @param avstemtArkivDato the avstemtArkivDato to set
	 */
	public void setAvstemtArkivDato(LocalDateTime avstemtArkivDato) {
		this.avstemtArkivDato = avstemtArkivDato;
	}

	/**
	 * Getter for the antallSider property.
	 *
	 * @return the antallSider
	 */
	public Integer getAntallSider() {
		return antallSider;
	}

	/**
	 * Setter for the antallSider property.
	 *
	 * @param antallSider the antallSider to set
	 */
	public void setAntallSider(Integer antallSider) {
		this.antallSider = antallSider;
	}

	/**
	 * Getter for the antallArk property.
	 *
	 * @return the antallArk
	 */
	public Integer getAntallArk() {
		return antallArk;
	}

	/**
	 * Setter for the antallArk property.
	 *
	 * @param antallArk the antallArk to set
	 */
	public void setAntallArk(Integer antallArk) {
		this.antallArk = antallArk;
	}

	/**
	 * Getter for the dokumentStatus property.
	 *
	 * @return the dokumentStatus
	 */
	public DokumentStatusCode getDokumentStatus() {
		return dokumentStatus;
	}

	/**
	 * Setter for the dokumentStatus property.
	 *
	 * @param dokumentStatus the dokumentStatus to set
	 */
	public void setDokumentStatus(DokumentStatusCode dokumentStatus) {
		this.dokumentStatus = dokumentStatus;
	}

	/**
	 * Getter for the fagomrade property.
	 *
	 * @return the fagomrade
	 */
	public FagomradeCode getFagomrade() {
		return fagomrade;
	}

	/**
	 * Setter for the fagomrade property.
	 *
	 * @param fagomrade the fagomrade to set
	 */
	public void setFagomrade(FagomradeCode fagomrade) {
		this.fagomrade = fagomrade;
	}

	/**
	 * Getter for the bestillendeFagsystem property.
	 *
	 * @return the bestillendeFagsystem
	 */
	public String getBestillendeFagsystem() {
		return bestillendeFagsystem;
	}

	/**
	 * Setter for the bestillendeFagsystem property.
	 *
	 * @param bestillendeFagsystem the bestillendeFagsystem to set
	 */
	public void setBestillendeFagsystem(String bestillendeFagsystem) {
		this.bestillendeFagsystem = bestillendeFagsystem;
	}

	/**
	 * Getter for the arkivSystem property.
	 *
	 * @return the arkivSystem
	 */
	public ArkivSystemCode getArkivSystem() {
		return arkivSystem;
	}

	/**
	 * Setter for the arkivSystem property.
	 *
	 * @param arkivSystem the arkivSystem to set
	 */
	public void setArkivSystem(ArkivSystemCode arkivSystem) {
		this.arkivSystem = arkivSystem;
	}

	/**
	 * Getter for the distribusjonInfo property.
	 *
	 * @return the distribusjonInfo
	 */
	public DistribusjonInfo getDistribusjonInfo() {
		return distribusjonInfo;
	}

	/**
	 * This method should not be used directly, instead use
	 * {@link DistribusjonInfo#addDokumentInfo(DokumentInfo)}
	 *
	 * @param distribusjonInfo The distribusjonInfo to set.
	 */
	public void setDistribusjonInfo(DistribusjonInfo distribusjonInfo) {
		this.distribusjonInfo = distribusjonInfo;
	}


	/**
	 * Getter for the distribusjonInfo property.
	 *
	 * @return the postadresse
	 */
	public Postadresse getPostadresse() {
		return postadresse;
	}

	/**
	 * Setter for the postadresse property.
	 *
	 * @param postadresse the postadresse to set
	 */
	public void setPostadresse(Postadresse postadresse) {
		this.postadresse = postadresse;
	}

	/**
	 * Getter for the dokumentInfoId property.
	 *
	 * @return the dokumentInfoId
	 */
	public Long getDokumentInfoId() {
		return dokumentInfoId;
	}

	/**
	 * Getter for the mottakerNavn property.
	 *
	 * @return the mottakerNavn
	 */
	public String getMottakerNavn() {
		return mottakerNavn;
	}

	/**
	 * Setter for the mottakerNavn property.
	 *
	 * @param mottakerNavn the mottakerNavn to set
	 */
	public void setMottakerNavn(String mottakerNavn) {
		this.mottakerNavn = mottakerNavn;
	}

	/**
	 * Getter for the mottakerIdType property.
	 *
	 * @return the mottakerIdType
	 */
	public MottakerIdTypeCode getMottakerIdType() {
		return mottakerIdType;
	}

	/**
	 * Setter for the mottakerIdType property.
	 *
	 * @param mottakerIdType the mottakerIdType to set
	 */
	public void setMottakerIdType(MottakerIdTypeCode mottakerIdType) {
		this.mottakerIdType = mottakerIdType;
	}

	/**
	 * Getter for the ekspedertDato property.
	 *
	 * @return the ekspedertDato
	 */
	public LocalDateTime getEkspedertDato() {
		return ekspedertDato;
	}

	/**
	 * Setter for the ekspedertDato property.
	 *
	 * @param ekspedertDato the ekspedertDato to set
	 */
	public void setEkspedertDato(LocalDateTime ekspedertDato) {
		this.ekspedertDato = ekspedertDato;
	}

	/**
	 * Getter for the brevkode property.
	 *
	 * @return the brevkode
	 */
	public String getBrevkode() {
		return brevkode;
	}

	/**
	 * Setter for the brevkode property.
	 *
	 * @param brevkode the brevkode to set
	 */
	public void setBrevkode(String brevkode) {
		this.brevkode = brevkode;
	}

	/**
	 * Getter for the konverasjonId property.
	 *
	 * @return the konversasjonId
	 */
	public String getKonversasjonId() {
		return konversasjonId;
	}

	/**
	 * Setter for the konverasjonId property.
	 *
	 * @param konversasjonId the konversasjonId to set
	 */
	public void setKonversasjonId(String konversasjonId) {
		this.konversasjonId = konversasjonId;
	}

	/**
	 * Getter for the forsendelseTittel property.
	 *
	 * @return the forsendelseTittel
	 */
	public String getForsendelseTittel() {
		return forsendelseTittel;
	}

	/**
	 * Setter for the forsendelseTittel property.
	 *
	 * @param forsendelseTittel the avsenderId to set
	 **/
	public void setForsendelseTittel(String forsendelseTittel) {
		this.forsendelseTittel = forsendelseTittel;
	}

	/**
	 * Getter for the batchId property.
	 *
	 * @return the batchId
	 */
	public String getBatchId() {
		return batchId;
	}

	/**
	 * Setter for the batchId property.
	 *
	 * @param batchId the avsenderId to set
	 */
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	/**
	 * Getter for the avsenderId property.
	 *
	 * @return the avsenderId
	 */
	public String getAvsenderId() {
		return avsenderId;
	}

	/**
	 * Setter for the avsenderId property.
	 *
	 * @param avsenderId the avsenderId to set
	 */
	public void setAvsenderId(String avsenderId) {
		this.avsenderId = avsenderId;
	}

	/**
	 * Getter for the digitalDistributorId property.
	 *
	 * @return the digitalDistributorId
	 */
	public String getDigitalDistributorId() {
		return digitalDistributorId;
	}

	/**
	 * Setter for the digitalDistributorId property.
	 *
	 * @param digitalDistributorId the digitalDistributorId to set
	 */
	public void setDigitalDistributorId(String digitalDistributorId) {
		this.digitalDistributorId = digitalDistributorId;
	}

	/**
	 * Getter for the digitalPostkasseAdresse property.
	 *
	 * @return the digitalPostkasseAdresse
	 */
	public String getDigitalPostkasseAdresse() {
		return digitalPostkasseAdresse;
	}

	/**
	 * Setter for the digitalPostkasseAdresse property.
	 *
	 * @param digitalPostkasseAdresse the digitalPostkasseAdresse to set
	 */
	public void setDigitalPostkasseAdresse(String digitalPostkasseAdresse) {
		this.digitalPostkasseAdresse = digitalPostkasseAdresse;
	}

	/**
	 * Getter for the apningskvittering property.
	 *
	 * @return the apningskvittering
	 */
	public Boolean getApningskvittering() {
		return apningskvittering;
	}

	/**
	 * Setter for the apningskvittering property.
	 *
	 * @param apningskvittering the apningskvittering to set
	 */
	public void setApningskvittering(Boolean apningskvittering) {
		this.apningskvittering = apningskvittering;
	}

	/**
	 * Getter for the virkningsdato property.
	 *
	 * @return the virkningsdato
	 */
	public LocalDateTime getVirkningsdato() {
		return virkningsdato;
	}

	/**
	 * Setter for the virkningsdato property.
	 *
	 * @param virkningsdato the virkningsdato to set
	 */
	public void setVirkningsdato(LocalDateTime virkningsdato) {
		this.virkningsdato = virkningsdato;
	}

	/**
	 * Getter for the brevProduksjonApplikasjon property.
	 *
	 * @return the brevProduksjonApplikasjon
	 */
	public String getBrevProduksjonApplikasjon() {
		return brevProduksjonApplikasjon;
	}

	/**
	 * Setter for the brevProduksjonApplikasjon property.
	 *
	 * @param brevProduksjonApplikasjon the brevProduksjonApplikasjon to set
	 */
	public void setBrevProduksjonApplikasjon(String brevProduksjonApplikasjon) {
		this.brevProduksjonApplikasjon = brevProduksjonApplikasjon;
	}

	public LocalDateTime getAvstemtDato() {
		return avstemtDato;
	}

	public void setAvstemtDato(LocalDateTime avstemtDato) {
		this.avstemtDato = avstemtDato;
	}

	public String getAvstemtReferanse() {
		return avstemtReferanse;
	}

	public void setAvstemtReferanse(String avstemtReferanse) {
		this.avstemtReferanse = avstemtReferanse;
	}

	/**
	 * Getter for the sikkerhetsniva property.
	 *
	 * @return the sikkerhetsniva
	 */
	public SikkerhetsnivaCode getSikkerhetsniva() {
		return sikkerhetsniva;
	}

	/**
	 * Setter for the sikkerhetsniva property.
	 *
	 * @param sikkerhetsniva the sikkerhetsniva to set
	 */
	public void setSikkerhetsniva(SikkerhetsnivaCode sikkerhetsniva) {
		this.sikkerhetsniva = sikkerhetsniva;
	}

	/**
	 * Getter for the aapnetDato property.
	 *
	 * @return the aapnetDato
	 */
	public LocalDateTime getAapnetDato() {
		return aapnetDato;
	}

	/**
	 * Setter for the aapnetDato property.
	 *
	 * @param aapnetDato the aapnetDato to set
	 */
	public void setAapnetDato(LocalDateTime aapnetDato) {
		this.aapnetDato = aapnetDato;
	}

	/**
	 * Add a Feilkvittering to the Feilkvittering Set
	 *
	 * @param feilkvittering The FilInfo to add.
	 */
	public void addFeilkvittering(Feilkvittering feilkvittering) {
		if (feilkvittering != null) {
			this.feilkvitterings.add(feilkvittering);
			feilkvittering.setDokumentInfo(this);
		}
	}

	/**
	 * Getter for the feilkvitterings property.
	 *
	 * @return the feilkvitterings
	 */
	public Set<Feilkvittering> getFeilkvitterings() {
		return Collections.unmodifiableSet(feilkvitterings);
	}
}
