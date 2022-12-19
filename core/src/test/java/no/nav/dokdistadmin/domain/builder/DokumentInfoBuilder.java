package no.nav.dokdistadmin.domain.builder;

import no.nav.dokdistadmin.domain.ArkivSystemCode;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentReferanse;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.FagomradeCode;
import no.nav.dokdistadmin.domain.FilInfo;
import no.nav.dokdistadmin.domain.MottakerIdTypeCode;
import no.nav.dokdistadmin.domain.Postadresse;
import no.nav.dokdistadmin.domain.SikkerhetsnivaCode;
import no.nav.dokdistadmin.domain.VarselInfo;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DokumentInfoBuilder extends Builder<DokumentInfo> {

	private DokumentInfoBuilder() {
	}

	public static DokumentInfoBuilder with() {
		return new DokumentInfoBuilder();
	}

	private Long dokumentInfoId;
	private String dokumentId;
	private String mottakerId;
	private MottakerIdTypeCode mottakerIdTypeCode;
	private String mottakerNavn;
	private MottakerIdTypeCode mottakerIdType;
	private Postadresse postadresse;
	private String arkivkode;
	private LocalDateTime avstemtArkivDato;
	private Integer antallSider;
	private Integer antallArk;
	private LocalDateTime ekspedertDato;
	private String brevkode;
	private String konversasjonsId;
	private DokumentStatusCode dokumentStatus;
	private FagomradeCode fagomrade;
	private String forsendelseTittel;
	private String bestillendeFagsystem;
	private ArkivSystemCode arkivSystem;
	private String brevProduksjonApplikasjon;
	private String avsenderId;
	private String digitalPostkasseAdresse;
	private Set<FilInfo> filInfos = new HashSet<>();
	private Set<DokumentReferanse> dokumentReferanses = new HashSet<>();
	private Set<VarselInfo> varselInfos = new HashSet<>();
	private DistribusjonInfo distribusjonInfo;
	private SikkerhetsnivaCode sikkerhetsnivaCode;
	private String digitalDistributorId;

	@Override
	public DokumentInfo build() {
		DokumentInfo dokumentInfo = new DokumentInfo(dokumentInfoId, 1);
		dokumentInfo.setDokumentId(dokumentId);
		dokumentInfo.setMottakerId(mottakerId);
		dokumentInfo.setMottakerIdType(mottakerIdTypeCode);
		dokumentInfo.setDigitalDistributorId(mottakerId);
		dokumentInfo.setMottakerNavn(mottakerNavn);
		dokumentInfo.setMottakerIdType(mottakerIdType);
		dokumentInfo.setPostadresse(postadresse);
		dokumentInfo.setArkivkode(arkivkode);
		dokumentInfo.setAvstemtArkivDato(avstemtArkivDato);
		dokumentInfo.setAntallSider(antallSider);
		dokumentInfo.setAntallArk(antallArk);
		dokumentInfo.setEkspedertDato(ekspedertDato);
		dokumentInfo.setBrevkode(brevkode);
		dokumentInfo.setDokumentStatus(dokumentStatus);
		dokumentInfo.setFagomrade(fagomrade);
		dokumentInfo.setForsendelseTittel(forsendelseTittel);
		dokumentInfo.setBestillendeFagsystem(bestillendeFagsystem);
		dokumentInfo.setArkivSystem(arkivSystem);
		dokumentInfo.setBrevProduksjonApplikasjon(brevProduksjonApplikasjon);
		dokumentInfo.setKonversasjonId(konversasjonsId);
		dokumentInfo.setAvsenderId(avsenderId);
		dokumentInfo.setDigitalPostkasseAdresse(digitalPostkasseAdresse);
		dokumentInfo.setDistribusjonInfo(distribusjonInfo);
		dokumentInfo.setSikkerhetsniva(sikkerhetsnivaCode);
		dokumentInfo.setDigitalDistributorId(digitalDistributorId);
		for (FilInfo filInfo : filInfos) {
			dokumentInfo.addFilInfo(filInfo);
		}
		for (DokumentReferanse dokumentReferanse : dokumentReferanses) {
			dokumentInfo.addDokumentReferanse(dokumentReferanse);
		}
		for (VarselInfo varselInfo : varselInfos) {
			dokumentInfo.addVarselInfo(varselInfo);
		}
		return dokumentInfo;
	}

	public DokumentInfoBuilder dokumentInfoId(Long dokumentInfoId) {
		this.dokumentInfoId = dokumentInfoId;
		return this;
	}

	public DokumentInfoBuilder dokumentId(String dokumentId) {
		this.dokumentId = dokumentId;
		return this;
	}

	public DokumentInfoBuilder mottakerId(String mottakerId) {
		this.mottakerId = mottakerId;
		return this;
	}

	public DokumentInfoBuilder mottakerIdTypeKode(MottakerIdTypeCode mottakerIdTypeCode) {
		this.mottakerIdTypeCode = mottakerIdTypeCode;
		return this;
	}

	public DokumentInfoBuilder digitalDistributorId(String digitalDistributorId) {
		this.digitalDistributorId = digitalDistributorId;
		return this;
	}

	public DokumentInfoBuilder mottakerNavn(String mottakerNavn) {
		this.mottakerNavn = mottakerNavn;
		return this;
	}

	public DokumentInfoBuilder mottakerIdType(MottakerIdTypeCode mottakerIdType) {
		this.mottakerIdType = mottakerIdType;
		return this;
	}

	public DokumentInfoBuilder postadresse(Postadresse postadresse) {
		this.postadresse = postadresse;
		return this;
	}

	public DokumentInfoBuilder arkivkode(String arkivkode) {
		this.arkivkode = arkivkode;
		return this;
	}

	public DokumentInfoBuilder avstemtArkivDato(LocalDateTime avstemtArkivDato) {
		this.avstemtArkivDato = avstemtArkivDato;
		return this;
	}

	public DokumentInfoBuilder antallSider(Integer antallSider) {
		this.antallSider = antallSider;
		return this;
	}

	public DokumentInfoBuilder antallArk(Integer antallArk) {
		this.antallArk = antallArk;
		return this;
	}

	public DokumentInfoBuilder ekspedertDato(LocalDateTime ekspedertDato) {
		this.ekspedertDato = ekspedertDato;
		return this;
	}

	public DokumentInfoBuilder brevkode(String brevkode) {
		this.brevkode = brevkode;
		return this;
	}

	public DokumentInfoBuilder dokumentStatus(DokumentStatusCode dokumentStatus) {
		this.dokumentStatus = dokumentStatus;
		return this;
	}

	public DokumentInfoBuilder fagomrade(FagomradeCode fagomrade) {
		this.fagomrade = fagomrade;
		return this;
	}

	public DokumentInfoBuilder forsendelseTittel(String forsendelseTittel) {
		this.forsendelseTittel = forsendelseTittel;
		return this;
	}

	public DokumentInfoBuilder bestillendeFagsystem(String bestillendeFagsystem) {
		this.bestillendeFagsystem = bestillendeFagsystem;
		return this;
	}

	public DokumentInfoBuilder arkivSystem(ArkivSystemCode arkivSystem) {
		this.arkivSystem = arkivSystem;
		return this;
	}

	public DokumentInfoBuilder brevProduksjonApplikasjon(String brevProduksjonApplikasjon) {
		this.brevProduksjonApplikasjon = brevProduksjonApplikasjon;
		return this;
	}

	public DokumentInfoBuilder filInfos(FilInfo... filInfos) {
		this.filInfos.addAll(Arrays.asList(filInfos));
		return this;
	}

	public DokumentInfoBuilder dokumentReferanses(DokumentReferanse... dokumentReferanses) {
		this.dokumentReferanses.addAll(Arrays.asList(dokumentReferanses));
		return this;
	}

	public DokumentInfoBuilder varselInfos(VarselInfo... varselInfos) {
		this.varselInfos.addAll(Arrays.asList(varselInfos));
		return this;
	}

	public DokumentInfoBuilder konversasjonsId(String konversasjonsId) {
		this.konversasjonsId = konversasjonsId;
		return this;
	}

	public DokumentInfoBuilder avsenderId(String avsenderId) {
		this.avsenderId = avsenderId;
		return this;
	}

	public DokumentInfoBuilder digitalPostkasseAdresse(String digitalPostkasseAdresse) {
		this.digitalPostkasseAdresse = digitalPostkasseAdresse;
		return this;
	}

	public DokumentInfoBuilder distribusjonInfo(DistribusjonInfo distribusjonInfo) {
		this.distribusjonInfo = distribusjonInfo;
		return this;
	}

	public DokumentInfoBuilder sikkerhetsnivaa(SikkerhetsnivaCode sikkerhetsnivaCode) {
		this.sikkerhetsnivaCode = sikkerhetsnivaCode;
		return this;
	}

}
