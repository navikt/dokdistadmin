package no.nav.dokdistadmin.administrerforsendelse;


import no.nav.dokdistadmin.domain.ChangeStamp;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.FagomradeCode;
import no.nav.dokdistadmin.domain.FeilTypeCode;
import no.nav.dokdistadmin.domain.Feilkvittering;
import no.nav.dokdistadmin.domain.ModusCode;
import no.nav.dokdistadmin.domain.Postadresse;
import no.nav.dokdistadmin.domain.VarselInfo;
import no.nav.dokdistadmin.domain.VarslingKanalCode;
import org.apache.commons.lang3.StringUtils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.DITTNAV;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.SDP;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.FEILET;
import static no.nav.dokdistadmin.domain.ModusCode.P;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.EPOST;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.MOBILTELEFON;

public class TestUtils {

	public static final String RESENDINGDISTRIBUSJON_ID = UUID.randomUUID().toString();
	public static final Long DOKUMENTINFO_ID = 1110L;
	public static final Long DISTRIBUSJONINFO_ID = 1111L;
	public static final String KONVERSASJON_ID = "7ef3e7c7-cd4c-40bd-a5bf-99c5dbb26131";
	public static final String DISTRIBUSJON_ID = "7882d37e-34f7-11e9-b677-d663bd953d61";
	public static final String RESENDING_FORSENDELSE_ID = "b22cf04c-5526-4ed4-8738-99fd99aee8f3";
	public static final String ORIGINAL_FORSENDELSE_ID = "7882d37e-34f7-11e9-b677-d663bd953d61";
	public static final String BESTILLENDE_FAGSYSTEM = "ARENA";
	public static final FagomradeCode FAGOMRADE_CODE = FagomradeCode.DAG;
	public static final String BREVPRODUKSJON_APPLIKASJONCODE = "DOKPROD";
	public static final LocalDateTime OPPRETTET_DATO = LocalDateTime.now(Clock.systemDefaultZone()).minusDays(7).minusMinutes(23).minusMinutes(59);
	public static final LocalDateTime DISTRIBUSJON_DATO = LocalDateTime.now(Clock.systemDefaultZone()).minusDays(7).minusHours(23);
	public static final String MOTTAKER_ID = "26016826020";
	public static final String DIGITAL_DISTRIBUTOR_ID = "996460320";
	public static final String ARKIV_KODE = "389426100";
	public static final DokumentStatusCode DOKUMENT_STATUS = DokumentStatusCode.OPPRETTET;
	public static final DokumentStatusCode DOKUMENT_STATUS_EK = EKSPEDERT;
	public static final DistribusjonStatusCode DISTRIBUSJON_STATUS = DistribusjonStatusCode.OPPRETTET;
	public static final DistribusjonKanalCode DISTRIBUSJON_KANAL_PRINT = DistribusjonKanalCode.PRINT;
	public static final String EPOSTADDRESS = "epostaddress0@nav.no";
	public static final Long VARSELID = 2000L;
	public static final String DOKUMENT_ID = "6e2e21d7-eec4-4ce3-a31f-8e28b169b6f7";

	public static final String KONVERSASJON_ID_1 = "7ef3e7c7-cd4c-40bd-a5bf-99c5dbb26131";
	public static final String DISTRIBUSJON_ID_1 = "7882d37e-34f7-11e9-b677-d663bd953d61";
	public static final String BESTILLENDE_FAGSYSTEM_1 = "ARENA";

	public static final DokumentStatusCode DOKUMENT_STATUS_1 = DokumentStatusCode.OPPRETTET;

	//SDP forsinket
	public static final Long DOKUMENTINFO_ID_2 = 1222L;
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
	public static final Long VARSELID_2 = 2222L;
	private static final ModusCode MODUS = P;
	public static final String EPOSTADDRESS_2 = "epostaddress2@nav.no";
	public static final Long DISTRIBUSJONINFO_ID_3 = 1333L;
	public static final String KONVERSASJON_ID_3 = "7ef3e7c7-cd4c-40bd-a5bf-99c5dbb26143";
	public static final String DISTRIBUSJON_ID_3 = "7882d37e-34f7-11e9-b677-d663bd953d63";
	public static final String BESTILLENDE_FAGSYSTEM_3 = "ARENA";
	public static final FagomradeCode FAGOMRADE_CODE_3 = FagomradeCode.AAP;
	public static final String BREVPRODUKSJON_APPLIKASJONCODE_3 = "DOKPROD";
	public static final LocalDateTime OPPRETTET_DATO_3 = LocalDateTime.now(Clock.systemDefaultZone()).minusHours(7).minusSeconds(3);
	public static final LocalDateTime DISTRIBUSJON_DATO_3 = LocalDateTime.now(Clock.systemDefaultZone()).minusHours(7).minusMinutes(1);
	public static final String MOTTAKER_ID_3 = "26016826023";
	public static final String ARKIV_KODE_3 = "389426113";
	public static final String DIGITAL_DISTRIBUTOR_ID_3 = "984661183";
	public static final DokumentStatusCode DOKUMENT_STATUS_3 = DokumentStatusCode.OVERSENDT;
	public static final DistribusjonStatusCode DISTRIBUSJON_STATUS_3 = DistribusjonStatusCode.OVERSENDT;
	public static final DistribusjonKanalCode DISTRIBUSJON_KANAL_3_SDP = DistribusjonKanalCode.SDP;
	public static final String EPOSTADDRESS_3 = "epostaddress3@nav.no";
	public static final String TELEFONNUMMER = "11111111";
	public static final String PRINT = DistribusjonKanalCode.PRINT.name();
	public static final String SDP = DistribusjonKanalCode.SDP.name();

	private static final String DATE_FORMATTER = "yyyy-MM-dd HH:mm:ss";
	private static final String MELDING = "Du har fått brev fra NAV";
	private static String DIGITAL_DISTRIBUTOR_ID_2 = "984661183";
	public static final String DETALJER = "detaljer";
	public static final LocalDateTime TIDSPUNKT = LocalDateTime.now().minusDays(2);
	public static final String DOKDISTDPI = "dokdistdpi";
	public static final String ADRESSELINJE_1 = "adresselinje1";
	public static final String ADRESSELINJE_2 = "adresselinje2";
	public static final String ADRESSELINJE_3 = "adresselinje3";
	public static final String POSTNUMMER = "postnummer";
	public static final String POSTSTED = "poststed";
	public static final String LANDKODE = "landkode";
	public static String DIGITALPOSTKASSE_ADRESSE = "xyx#012@xyz";

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

	public static List<DistribusjonInfo> createDistribusjonInfoListOverFiveHoursOld() {
		List<DistribusjonInfo> distribusjonInfos = Collections.singletonList(
				DistribusjonInfo.builder()
						.distribusjonInfoId(DISTRIBUSJONINFO_ID_3)
						.distribusjonId(DISTRIBUSJON_ID_3)
						.distribusjonKanal(DISTRIBUSJON_KANAL_3_SDP)
						.distribusjonStatus(DISTRIBUSJON_STATUS_3)
						.distribusjonDato(DISTRIBUSJON_DATO_3)
						.modus(P)
						.dokumentInfos(Set.of(
								DokumentInfo.builder()
										.dokumentId(DISTRIBUSJON_ID_3)
										.bestillendeFagsystem(BESTILLENDE_FAGSYSTEM_3)
										.dokumentStatus(DOKUMENT_STATUS_3)
										.fagomrade(FAGOMRADE_CODE_3)
										.ekspedertDato(null)
										.konversasjonId(KONVERSASJON_ID_3)
										.arkivkode(ARKIV_KODE_3)
										.brevProduksjonApplikasjon(BREVPRODUKSJON_APPLIKASJONCODE_3)
										.digitalDistributorId(DIGITAL_DISTRIBUTOR_ID_3)
										.varselInfos(Set.of(
												VarselInfo.builder()
														.varselInfoId(VARSELID_2)
														.epostAdresse(EPOSTADDRESS_3)
														.varslingKanal(EPOST)
														.build()))
										.build()))
						.build());
		distribusjonInfos.get(0).setChangeStamp(ChangeStamp.builder().opprettetAv("rdist001").opprettetDato(OPPRETTET_DATO_3).build());
		return distribusjonInfos;
	}

	public static DistribusjonInfo createDistribusjonInfoPrint(DokumentStatusCode dokumentStatus) {
		DistribusjonInfo distribusjonInfo = DistribusjonInfo.builder()
				.distribusjonInfoId(DISTRIBUSJONINFO_ID)
				.originalDistribusjonId(ORIGINAL_FORSENDELSE_ID)
				.distribusjonId(DISTRIBUSJON_ID)
				.distribusjonKanal(DISTRIBUSJON_KANAL_PRINT)
				.distribusjonStatus(DISTRIBUSJON_STATUS)
				.produksjonDato(OPPRETTET_DATO)
				.distribusjonDato(DISTRIBUSJON_DATO)
				.dokumentInfos(Set.of(createDokumentInfoPrint(dokumentStatus)))
				.modus(P)
				.build();
		distribusjonInfo.setChangeStamp(ChangeStamp.builder().opprettetAv("tdisk07").opprettetDato(OPPRETTET_DATO).build());
		return distribusjonInfo;
	}

	public static List<DistribusjonInfo> createDistribusjonInfos() {
		DistribusjonInfo distribusjonInfo = DistribusjonInfo.builder()
				.distribusjonInfoId(DISTRIBUSJONINFO_ID)
				.resendingDistribusjonId(RESENDING_FORSENDELSE_ID)
				.originalDistribusjonId(ORIGINAL_FORSENDELSE_ID)
				.distribusjonId(DISTRIBUSJON_ID)
				.distribusjonKanal(DISTRIBUSJON_KANAL_PRINT)
				.distribusjonStatus(DISTRIBUSJON_STATUS)
				.produksjonDato(OPPRETTET_DATO)
				.distribusjonDato(DISTRIBUSJON_DATO)
				.dokumentInfos(Set.of(
						DokumentInfo.builder()
						.dokumentInfoId(DOKUMENTINFO_ID)
						.dokumentId(DISTRIBUSJON_ID)
						.bestillendeFagsystem(BESTILLENDE_FAGSYSTEM)
						.dokumentStatus(DOKUMENT_STATUS_EK)
						.fagomrade(FAGOMRADE_CODE)
						.konversasjonId(KONVERSASJON_ID)
						.arkivkode(ARKIV_KODE)
						.brevProduksjonApplikasjon(BREVPRODUKSJON_APPLIKASJONCODE)
						.build()))
				.modus(P)
				.build();
		distribusjonInfo.setChangeStamp(ChangeStamp.builder().opprettetAv("tdisk07").opprettetDato(OPPRETTET_DATO).build());
		return Collections.singletonList(distribusjonInfo);
	}

	public static DokumentInfo createDokumentInfoPrint(DokumentStatusCode dokumentStatus) {
		DokumentInfo dokumentInfo = DokumentInfo.builder()
				.dokumentInfoId(DOKUMENTINFO_ID)
				.dokumentId(DISTRIBUSJON_ID)
				.bestillendeFagsystem(BESTILLENDE_FAGSYSTEM)
				.dokumentStatus(dokumentStatus)
				.mottakerId(MOTTAKER_ID)
				.fagomrade(FAGOMRADE_CODE)
				.konversasjonId(KONVERSASJON_ID)
				.arkivkode(ARKIV_KODE)
				.ekspedertDato(LocalDateTime.now())
				.postadresse(createPostadresse())
				.brevProduksjonApplikasjon(BREVPRODUKSJON_APPLIKASJONCODE)
				.varselInfos(Set.of(createEpostVarselInfo()))
				.build();
		dokumentInfo.setChangeStamp(ChangeStamp.builder().opprettetAv("tdisk07").opprettetDato(OPPRETTET_DATO).build());

		return dokumentInfo;
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
		DokumentInfo dokumentInfo = DokumentInfo.builder()
				.dokumentInfoId(DOKUMENTINFO_ID_2)
				.dokumentId(DISTRIBUSJON_ID_2)
				.bestillendeFagsystem(BESTILLENDE_FAGSYSTEM_2)
				.dokumentStatus(DOKUMENT_STATUS_2)
				.mottakerId(MOTTAKER_ID_2)
				.fagomrade(FAGOMRADE_CODE_2)
				.konversasjonId(KONVERSASJON_ID_2)
				.arkivkode(ARKIV_KODE_2)
				.brevProduksjonApplikasjon(BREVPRODUKSJON_APPLIKASJONCODE)
				.varselInfos(Set.of(
						VarselInfo.builder()
						.varselInfoId(VARSELID_2)
						.epostAdresse(EPOSTADDRESS_2)
						.varslingKanal(EPOST)
						.build()))
				.build();
		dokumentInfo.setChangeStamp(ChangeStamp.builder().opprettetAv("tdisk07").opprettetDato(OPPRETTET_DATO_2).build());
		dokumentInfo.getChangeStamp().updatedBy(DOKDISTDPI);
		return dokumentInfo;
	}

	public static List<DokumentInfo> createDokumentInfos() {
		return Arrays.asList(
				DokumentInfo.builder()
						.dokumentInfoId(DOKUMENTINFO_ID_2)
						.dokumentId(DISTRIBUSJON_ID_2)
						.bestillendeFagsystem(BESTILLENDE_FAGSYSTEM_2)
						.dokumentStatus(DOKUMENT_STATUS_2)
						.mottakerId(MOTTAKER_ID_2)
						.fagomrade(FAGOMRADE_CODE_2)
						.konversasjonId(KONVERSASJON_ID_2)
						.arkivkode(ARKIV_KODE_2)
						.brevProduksjonApplikasjon(BREVPRODUKSJON_APPLIKASJONCODE)
						.postadresse(createPostadresse())
						.varselInfos(Set.of(
								VarselInfo.builder()
								.varselInfoId(VARSELID_2)
								.epostAdresse(EPOSTADDRESS_2)
								.varslingKanal(EPOST)
								.build()))
						.build(),
				DokumentInfo.builder()
						.dokumentInfoId(DOKUMENTINFO_ID)
						.dokumentId(DISTRIBUSJON_ID)
						.bestillendeFagsystem(BESTILLENDE_FAGSYSTEM)
						.dokumentStatus(DOKUMENT_STATUS)
						.mottakerId(MOTTAKER_ID)
						.fagomrade(FAGOMRADE_CODE)
						.konversasjonId(KONVERSASJON_ID)
						.arkivkode(ARKIV_KODE)
						.brevProduksjonApplikasjon(BREVPRODUKSJON_APPLIKASJONCODE)
						.postadresse(createPostadresse())
						.varselInfos(Set.of(
								VarselInfo.builder()
								.varselInfoId(VARSELID)
								.epostAdresse(EPOSTADDRESS)
								.varslingKanal(EPOST)
								.build()))
						.build());
	}

	public static List<DokumentInfo> createDokumentInfosForForsendelse() {
		return Arrays.asList(
				DokumentInfo.builder()
						.dokumentInfoId(DOKUMENTINFO_ID_2)
						.dokumentId(DISTRIBUSJON_ID_2)
						.bestillendeFagsystem(BESTILLENDE_FAGSYSTEM_2)
						.dokumentStatus(DOKUMENT_STATUS_2)
						.mottakerId(MOTTAKER_ID_2)
						.fagomrade(FAGOMRADE_CODE_2)
						.ekspedertDato(LocalDateTime.now())
						.konversasjonId(KONVERSASJON_ID_2)
						.arkivkode(ARKIV_KODE_2)
						.brevProduksjonApplikasjon(BREVPRODUKSJON_APPLIKASJONCODE)
						.distribusjonInfo(
								DistribusjonInfo.builder()
										.distribusjonKanal(DITTNAV)
										.build())
						.varselInfos(Set.of(
								VarselInfo.builder()
								.varselInfoId(VARSELID_2)
								.epostAdresse(EPOSTADDRESS_2)
								.varslingKanal(EPOST)
								.build()))
						.build(),
				DokumentInfo.builder()
						.dokumentInfoId(DOKUMENTINFO_ID)
						.dokumentId(DOKUMENT_ID)
						.digitalDistributorId(DIGITAL_DISTRIBUTOR_ID)
						.digitalPostkasseAdresse(DIGITALPOSTKASSE_ADRESSE)
						.bestillendeFagsystem(BESTILLENDE_FAGSYSTEM)
						.ekspedertDato(LocalDateTime.now())
						.dokumentStatus(DOKUMENT_STATUS)
						.mottakerId(MOTTAKER_ID)
						.fagomrade(FAGOMRADE_CODE)
						.konversasjonId(KONVERSASJON_ID)
						.arkivkode(ARKIV_KODE)
						.brevProduksjonApplikasjon(BREVPRODUKSJON_APPLIKASJONCODE)
						.distribusjonInfo(
								DistribusjonInfo.builder().distribusjonKanal(DISTRIBUSJON_KANAL_2_SDP).build())
						.varselInfos(Set.of(createSMSVarselInfo(), createEpostVarselInfo()))
						.postadresse(createPostadresse())
						.build());
	}

	public static List<DistribusjonInfo> createDistribusjonInfoListException() {
		return Arrays.asList(DistribusjonInfo.builder()
						.distribusjonInfoId(DISTRIBUSJONINFO_ID_2)
						.originalDistribusjonId(DISTRIBUSJON_ID_2)
						.distribusjonId(DISTRIBUSJON_ID_2)
						.distribusjonKanal(DISTRIBUSJON_KANAL_2_SDP)
						.distribusjonStatus(DISTRIBUSJON_STATUS_2)
						.distribusjonDato(DISTRIBUSJON_DATO_2)
						.bekreftetMottattDato(null)
						.modus(P)
						.dokumentInfos(Set.of(
								DokumentInfo.builder()
										.dokumentInfoId(DOKUMENTINFO_ID_2)
										.dokumentId(DISTRIBUSJON_ID_3)
										.bestillendeFagsystem(BESTILLENDE_FAGSYSTEM)
										.dokumentStatus(FEILET)
										.mottakerId(MOTTAKER_ID)
										.fagomrade(FAGOMRADE_CODE_2)
										.ekspedertDato(null)
										.konversasjonId(KONVERSASJON_ID)
										.arkivkode(ARKIV_KODE)
										.brevProduksjonApplikasjon(BREVPRODUKSJON_APPLIKASJONCODE)
										.build(),
								DokumentInfo.builder()
										.dokumentInfoId(DOKUMENTINFO_ID)
										.dokumentId(DISTRIBUSJON_ID)
										.bestillendeFagsystem(BESTILLENDE_FAGSYSTEM)
										.dokumentStatus(DOKUMENT_STATUS_1)
										.mottakerId(MOTTAKER_ID_2)
										.fagomrade(FAGOMRADE_CODE)
										.ekspedertDato(null)
										.konversasjonId(KONVERSASJON_ID)
										.arkivkode(ARKIV_KODE)
										.brevProduksjonApplikasjon(BREVPRODUKSJON_APPLIKASJONCODE)
										.build()))
						.build(),
				DistribusjonInfo.builder()
						.distribusjonInfoId(DISTRIBUSJONINFO_ID_3)
						.resendingDistribusjonId(RESENDING_FORSENDELSE_ID)
						.originalDistribusjonId(DISTRIBUSJON_ID_3)
						.distribusjonId(DISTRIBUSJON_ID_3)
						.distribusjonKanal(DISTRIBUSJON_KANAL_3_SDP)
						.distribusjonStatus(DISTRIBUSJON_STATUS_3)
						.distribusjonDato(DISTRIBUSJON_DATO_3)
						.bekreftetMottattDato(null)
						.modus(P)
						.dokumentInfos(Set.of(
								DokumentInfo.builder()
								.dokumentInfoId(DOKUMENTINFO_ID_2)
								.dokumentId(DISTRIBUSJON_ID)
								.bestillendeFagsystem(BESTILLENDE_FAGSYSTEM_3)
								.dokumentStatus(DOKUMENT_STATUS_1)
								.mottakerId(MOTTAKER_ID_3)
								.fagomrade(FAGOMRADE_CODE_3)
								.ekspedertDato(null)
								.konversasjonId(KONVERSASJON_ID_3)
								.arkivkode(ARKIV_KODE_3)
								.brevProduksjonApplikasjon(BREVPRODUKSJON_APPLIKASJONCODE_3)
								.build()))
						.build());
	}


//	public static OppdaterForsendelserAvstemtInfo createOppdaterForsendelserAvstemtInfo() {
//		return OppdaterForsendelserAvstemtInfo.builder()
//				.avstemtReferanse("MMA-4213")
//				.forsendelser(Arrays.asList(
//						OppdaterForsendelserAvstemtInfo.Forsendelse.builder()
//								.forsendelseId(String.valueOf(DOKUMENTINFO_ID))
//								.build(),
//						OppdaterForsendelserAvstemtInfo.Forsendelse.builder()
//								.forsendelseId(String.valueOf(DOKUMENTINFO_ID_2))
//								.build())
//				)
//				.build();
//	}
//
//	public static FeilRegistrerForsendelseRequest getFeilRegistrerForsendelseRequest(Long forsendelseId) {
//		return FeilRegistrerForsendelseRequest.builder()
//				.forsendelseId(String.valueOf(forsendelseId))
//				.type(FeilTypeCode.MELDINGSFEIL.name())
//				.part("filPart")
//				.detaljer(DETALJER)
//				.tidspunkt(TIDSPUNKT)
//				.resendingDistribusjonId(RESENDINGDISTRIBUSJON_ID)
//				.build();
//	}
//
//	public static FeilRegistrerForsendelseRequest getFeilRegistrerForsendelseRequest(String type, LocalDateTime tidspunkt, String detaljer) {
//		return FeilRegistrerForsendelseRequest.builder()
//				.forsendelseId(String.valueOf(DOKUMENTINFO_ID_2))
//				.type(type)
//				.part("filPart")
//				.detaljer(detaljer)
//				.tidspunkt(tidspunkt)
//				.resendingDistribusjonId(RESENDINGDISTRIBUSJON_ID)
//				.build();
//	}

	public static Feilkvittering getFeilKvittering() {
		return Feilkvittering.builder()
				.feilkvitteringId(1111L)
				.feiltype(FeilTypeCode.MELDINGSFEIL)
				.feiletTidspunkt(TIDSPUNKT)
				.detaljer(DETALJER)
				.dokumentInfo(createDokumentInfo())
				.build();
	}

	private static VarselInfo createEpostVarselInfo() {
		return VarselInfo.builder()
				.varselInfoId(VARSELID)
				.epostAdresse(EPOSTADDRESS)
				.varslingKanal(EPOST)
				.varslingstekst(MELDING)
				.build();
	}

	private static VarselInfo createSMSVarselInfo() {
		return VarselInfo.builder()
				.varselInfoId(VARSELID)
				.mobiltelefonNummer(TELEFONNUMMER)
				.varslingKanal(MOBILTELEFON)
				.varslingstekst(MELDING)
				.build();
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

	public static String convertDateTimeToString(LocalDateTime localDateTime) {

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
		return localDateTime == null ? StringUtils.EMPTY : localDateTime.format(dateTimeFormatter);
	}

}