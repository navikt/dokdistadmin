package no.nav.dokdistadmin.administrerforsendelse;


import no.nav.dokdistadmin.domain.ChangeStamp;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.FagomradeCode;
import no.nav.dokdistadmin.domain.Postadresse;
import no.nav.dokdistadmin.domain.VarselInfo;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.DITTNAV;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.ModusCode.P;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.EPOST;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.MOBILTELEFON;

public class TestUtils {

	public static final Long DOKUMENTINFO_ID = 1110L;
	public static final String KONVERSASJON_ID = "7ef3e7c7-cd4c-40bd-a5bf-99c5dbb26131";
	public static final String BESTILLENDE_FAGSYSTEM = "ARENA";
	public static final FagomradeCode FAGOMRADE_CODE = FagomradeCode.DAG;
	public static final String BREVPRODUKSJON_APPLIKASJONCODE = "DOKPROD";
	public static final String MOTTAKER_ID = "26016826020";
	public static final String DIGITAL_DISTRIBUTOR_ID = "996460320";
	public static final String ARKIV_KODE = "389426100";
	public static final DistribusjonKanalCode DISTRIBUSJON_KANAL_PRINT = DistribusjonKanalCode.PRINT;
	public static final String EPOSTADDRESS = "epostaddress0@nav.no";
	public static final Long VARSELID = 2000L;
	public static final String TELEFONNUMMER = "11111111";
	public static final String DOKUMENT_ID = "6e2e21d7-eec4-4ce3-a31f-8e28b169b6f7";

	//SDP forsinket
	public static final Long DOKUMENTINFO_ID_2 = 1234L;
	public static final Long DISTRIBUSJONINFO_ID_2 = 1222L;
	public static final String KONVERSASJON_ID_2 = "7ef3e7c7-cd4c-40bd-a5bf-99c5dbb26132";
	public static final String DISTRIBUSJON_ID_2 = "7882d37e-34f7-11e9-b677-d663bd953d62";
	public static final String BESTILLENDE_FAGSYSTEM_2 = "ARENA";
	public static final FagomradeCode FAGOMRADE_CODE_2 = FagomradeCode.DAG;
	public static final String BREVPRODUKSJON_APPLIKASJONCODE_2 = "DOKPROD";
	public static final LocalDateTime OPPRETTET_DATO_2 = LocalDateTime.now(Clock.systemDefaultZone()).minusHours(5).minusMinutes(23);
	public static final LocalDateTime DISTRIBUSJON_DATO_2 = LocalDateTime.now(Clock.systemDefaultZone()).minusHours(5);
	public static final String ARKIV_KODE_2 = "389426102";
	public static final String MOTTAKER_ID_2 = "26016826022";
	public static final DokumentStatusCode DOKUMENT_STATUS_2 = DokumentStatusCode.OPPRETTET;
	public static final DistribusjonStatusCode DISTRIBUSJON_STATUS_2 = DistribusjonStatusCode.OPPRETTET;
	public static final DistribusjonKanalCode DISTRIBUSJON_KANAL_2_SDP = DistribusjonKanalCode.SDP;
	public static final String SDP = DistribusjonKanalCode.SDP.name();

	public static final String VARSEL_TEKST = "Du har fått brev fra NAV";
	public static final String VARSEL_TITTEL = "Melding fra NAV";
	public static final String DOKDISTDPI = "dokdistdpi";
	public static final String ADRESSELINJE_1 = "adresselinje1";
	public static final String ADRESSELINJE_2 = "adresselinje2";
	public static final String ADRESSELINJE_3 = "adresselinje3";
	public static final String POSTNUMMER = "postnummer";
	public static final String POSTSTED = "poststed";
	public static final String LANDKODE = "landkode";
	public static String DIGITALPOSTKASSE_ADRESSE = "xyx#012@xyz";
	public static LocalDateTime VARSEL_SENDT_DATO = LocalDateTime.now().minusMinutes(3);

	public static List<DokumentInfo> createEkspederteForsendelser() {
		return Arrays.asList(
				DokumentInfo.builder()
						.dokumentInfoId(DOKUMENTINFO_ID_2)
						.dokumentId(DISTRIBUSJON_ID_2)
						.bestillendeFagsystem(BESTILLENDE_FAGSYSTEM_2)
						.dokumentStatus(EKSPEDERT)
						.mottakerId(MOTTAKER_ID_2)
						.fagomrade(FAGOMRADE_CODE_2)
						.ekspedertDato(LocalDateTime.now())
						.konversasjonId(KONVERSASJON_ID_2)
						.arkivkode(ARKIV_KODE_2)
						.brevProduksjonApplikasjon(BREVPRODUKSJON_APPLIKASJONCODE)
						.distribusjonInfo(DistribusjonInfo.builder().distribusjonKanal(DITTNAV).build())
						.varselInfos(Set.of(createSMSVarselInfo(), createEpostVarselInfo()))
						.postadresse(createPostadresse())
						.build(),
				DokumentInfo.builder()
						.dokumentInfoId(DOKUMENTINFO_ID)
						.dokumentId(DOKUMENT_ID)
						.digitalDistributorId(DIGITAL_DISTRIBUTOR_ID)
						.digitalPostkasseAdresse(DIGITALPOSTKASSE_ADRESSE)
						.bestillendeFagsystem(BESTILLENDE_FAGSYSTEM)
						.ekspedertDato(LocalDateTime.now())
						.dokumentStatus(EKSPEDERT)
						.mottakerId(MOTTAKER_ID)
						.fagomrade(FAGOMRADE_CODE)
						.konversasjonId(KONVERSASJON_ID)
						.arkivkode(ARKIV_KODE)
						.brevProduksjonApplikasjon(BREVPRODUKSJON_APPLIKASJONCODE)
						.distribusjonInfo(DistribusjonInfo.builder().distribusjonKanal(DistribusjonKanalCode.SDP).build())
						.varselInfos(Set.of(createSMSVarselInfo(), createEpostVarselInfo()))
						.postadresse(createPostadresse())
						.build(),
				DokumentInfo.builder()
						.dokumentInfoId(DOKUMENTINFO_ID)
						.dokumentId(DOKUMENT_ID)
						.bestillendeFagsystem(BESTILLENDE_FAGSYSTEM)
						.ekspedertDato(LocalDateTime.now())
						.dokumentStatus(EKSPEDERT)
						.mottakerId(MOTTAKER_ID)
						.fagomrade(FAGOMRADE_CODE)
						.konversasjonId(KONVERSASJON_ID)
						.arkivkode(ARKIV_KODE)
						.brevProduksjonApplikasjon(BREVPRODUKSJON_APPLIKASJONCODE)
						.distribusjonInfo(DistribusjonInfo.builder().distribusjonKanal(DistribusjonKanalCode.PRINT).build())
						.varselInfos(Set.of(createSMSVarselInfo()))
						.postadresse(createPostadresse())
						.build());
	}

	public static DistribusjonInfo createDistribusjonInfoWithoutDokumentInfo() {
		return createDistribusjonInfoWithDistribusjonKanalWithoutDokumentInfo(DISTRIBUSJON_KANAL_2_SDP);
	}

	public static DistribusjonInfo createDistribusjonInfoWithDistribusjonKanalWithoutDokumentInfo(DistribusjonKanalCode distribusjonKanalCode) {
		DistribusjonInfo distribusjonInfo = DistribusjonInfo.builder()
				.distribusjonInfoId(DISTRIBUSJONINFO_ID_2)
				.originalDistribusjonId(DISTRIBUSJON_ID_2)
				.distribusjonId(DISTRIBUSJON_ID_2)
				.distribusjonKanal(distribusjonKanalCode)
				.distribusjonStatus(DISTRIBUSJON_STATUS_2)
				.produksjonDato(OPPRETTET_DATO_2)
				.distribusjonDato(DISTRIBUSJON_DATO_2)
				.modus(P)
				.build();
		distribusjonInfo.setChangeStamp(ChangeStamp.builder().opprettetAv("tdisk07").opprettetDato(OPPRETTET_DATO_2).build());
		return distribusjonInfo;
	}


	public static DistribusjonInfo createDistribusjonInfo() {
		DistribusjonInfo distribusjonInfo = DistribusjonInfo.builder()
				.distribusjonInfoId(DISTRIBUSJONINFO_ID_2)
				.originalDistribusjonId(DISTRIBUSJON_ID_2)
				.distribusjonId(DISTRIBUSJON_ID_2)
				.distribusjonKanal(DISTRIBUSJON_KANAL_2_SDP)
				.distribusjonStatus(DISTRIBUSJON_STATUS_2)
				.produksjonDato(OPPRETTET_DATO_2)
				.distribusjonDato(DISTRIBUSJON_DATO_2)
				.dokumentInfos(Set.of(createDokumentInfo()))
				.modus(P)
				.build();
		distribusjonInfo.setChangeStamp(ChangeStamp.builder().opprettetAv("tdisk07").opprettetDato(OPPRETTET_DATO_2).build());
		return distribusjonInfo;
	}

	public static DokumentInfo createDokumentInfo() {
		return createDokumentInfoWithStatusCode(DOKUMENT_STATUS_2);
	}

	public static DokumentInfo createDokumentInfoWithStatusCode(DokumentStatusCode dokumentStatusCode) {
		DokumentInfo dokumentInfo = DokumentInfo.builder()
				.dokumentInfoId(DOKUMENTINFO_ID_2)
				.dokumentId(DISTRIBUSJON_ID_2)
				.bestillendeFagsystem(BESTILLENDE_FAGSYSTEM_2)
				.dokumentStatus(dokumentStatusCode)
				.mottakerId(MOTTAKER_ID_2)
				.fagomrade(FAGOMRADE_CODE_2)
				.konversasjonId(KONVERSASJON_ID_2)
				.arkivkode(ARKIV_KODE_2)
				.brevProduksjonApplikasjon(BREVPRODUKSJON_APPLIKASJONCODE)
				.build();
		dokumentInfo.setChangeStamp(ChangeStamp.builder().opprettetAv("tdisk07").opprettetDato(OPPRETTET_DATO_2).build());
		dokumentInfo.getChangeStamp().updatedBy(DOKDISTDPI);
		return dokumentInfo;
	}

	private static VarselInfo createEpostVarselInfo() {
		VarselInfo varselInfo = VarselInfo.builder()
				.varslingstittel(VARSEL_TITTEL)
				.varselInfoId(VARSELID)
				.epostAdresse(EPOSTADDRESS)
				.varslingKanal(EPOST)
				.varslingstekst(VARSEL_TEKST)
				.varslingstidspunkt(VARSEL_SENDT_DATO)
				.build();
		varselInfo.setChangeStamp(ChangeStamp.builder()
				.opprettetDato(LocalDateTime.now().minusMinutes(2))
				.opprettetAv("srv")
				.build());
		return varselInfo;
	}

	private static VarselInfo createSMSVarselInfo() {
		VarselInfo varselInfo = VarselInfo.builder()
				.varselInfoId(VARSELID)
				.mobiltelefonNummer(TELEFONNUMMER)
				.varslingKanal(MOBILTELEFON)
				.varslingstekst(VARSEL_TEKST)
				.varslingstidspunkt(VARSEL_SENDT_DATO)
				.build();
		varselInfo.setChangeStamp(ChangeStamp.builder()
				.opprettetDato(LocalDateTime.now().minusMinutes(2))
				.opprettetAv("srv")
				.build());
		return varselInfo;
	}

	private static Postadresse createPostadresse() {
		return Postadresse.builder()
				.adresselinje1(ADRESSELINJE_1)
				.adresselinje2(ADRESSELINJE_2)
				.adresselinje3(ADRESSELINJE_3)
				.postnummer(POSTNUMMER)
				.poststed(POSTSTED)
				.landkode(LANDKODE)
				.build();
	}
}