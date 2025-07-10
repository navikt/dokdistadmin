package no.nav.dokdistadmin.administrerforsendelse;

import no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse.FeilregistrerForsendelseRequest;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequest;
import no.nav.dokdistadmin.domain.ChangeStamp;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DistribusjonsTypeKode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentReferanse;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.Postadresse;
import no.nav.dokdistadmin.domain.RefererTilCode;
import no.nav.dokdistadmin.domain.VarselInfo;
import no.nav.dokdistadmin.domain.VarselStatusCode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static java.time.Clock.systemDefaultZone;
import static java.time.LocalDateTime.now;
import static no.nav.dokdistadmin.domain.ArkivSystemCode.JOARK;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.SDP;
import static no.nav.dokdistadmin.domain.DistribusjonstidspunktKode.KJERNETID;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OPPRETTET;
import static no.nav.dokdistadmin.domain.FeilTypeCode.MELDINGSFEIL;
import static no.nav.dokdistadmin.domain.ForsendelseMetadataTypeCode.DPO_ARKIVMELDING;
import static no.nav.dokdistadmin.domain.ModusCode.P;
import static no.nav.dokdistadmin.domain.MottakerIdTypeCode.PERSON;
import static no.nav.dokdistadmin.domain.RefererTilCode.HOVEDDOKUMENT;
import static no.nav.dokdistadmin.domain.RefererTilCode.VEDLEGG;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.EPOST;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.MOBILTELEFON;

public class Rdist001TestUtils {

	public static final Long DOKUMENTINFO_ID = 1110L;
	public static final Long DISTRIBUSJONINFO_ID = 1222L;
	public static final Long VARSELID = 2000L;
	public static final Long DOKUMENT_REFERANSE_ID = 1337L;
	public static final String KONVERSASJON_ID = "7ef3e7c7-cd4c-40bd-a5bf-99c5dbb26131";
	public static final String DISTRIBUSJON_ID = "7882d37e-34f7-11e9-b677-d663bd953d62";
	public static final String BESTILLINGS_ID = "723gd37e-34f7-11e9-b677-d663bd9as462";
	public static final String ORIGINAL_DISTRIBUSJON_ID = "623gd37e-34f7-123g-b677-d663bdaj8fe3";
	public static final String BESTILLENDE_FAGSYSTEM = "ARENA";
	public static final String BREVPRODUKSJON_APPLIKASJONCODE = "DOKPROD";
	public static final String MOTTAKER_ID = "26016826020";
	public static final String MOTTAKER_NAVN = "Navn Navnesen";
	public static final String DIGITAL_DISTRIBUTOR_ID = "996460320";
	public static final String ARKIV_KODE = "389426100";
	public static final String EPOSTADRESSE = "epostaddress0@nav.no";
	public static final String TELEFONNUMMER = "91234567";
	public static final String DOKDISTDPI = "dokdistdpi";
	public static final String ADRESSELINJE_1 = "adresselinje1";
	public static final String ADRESSELINJE_2 = "adresselinje2";
	public static final String ADRESSELINJE_3 = "adresselinje3";
	public static final String POSTNUMMER = "1234";
	public static final String POSTSTED = "Oslo";
	public static final String LANDKODE = "NO";
	public static final String DIGITALPOSTKASSE_ADRESSE = "xyx#012@xyz";
	public static final String MELDING = "Du har fått brev fra NAV";
	public static final String OPPRETTET_AV = "tdisk07";
	public static final String FORSENDELSE_TITTEL = "Forsendelse fra NAV";
	public static final byte[] FORSENDELSE_METADATA = lesInnFil("__files/forsendelsemetadata/forsendelsemetadata.xml");
	public static final String DOKUMENT_PRODUKSJON_APP = "Dokument Prod App";
	public static final String DOKUMENT_OBJEKT_REFERANSE = "4b79638e-e786-4065-8486-faf8bf4027c9";
	public static final String ARKIV_DOKUMENT_INFO_ID = "1234";
	public static final String DOKUMENT_TYPE_ID = "U000001";
	public static final String BATCH_ID = "batchId";
	public static final String DETALJER = "detaljer";
	public static final String PART = "part";
	public static final String RESENDING_DISTRIBUSJON_ID = "1";
	public static final String FAGOMRADE_DAG = "DAG";
	public static final String DOKDISTADMIN = "dokdistadmin";
	public static final LocalDateTime OPPRETTET_DATO = now(systemDefaultZone()).minusHours(5).minusMinutes(23);
	public static final LocalDateTime DISTRIBUSJON_DATO = now(systemDefaultZone()).minusHours(5);

	public static final String VARSELTEKST = "Du har fått brev fra NAV";

	public static final String VARSELTITTEL = "Melding fra NAV";
	public static final LocalDateTime FIRST_VARSEL_SENDT_DATO = LocalDateTime.now().minusHours(18);
	public static final LocalDateTime SECOND_VARSEL_SENDT_DATO = FIRST_VARSEL_SENDT_DATO.plusHours(9);

	public static DistribusjonInfo createDistribusjonInfoWithDistribusjonKanal(DistribusjonKanalCode distribusjonKanalCode) {
		DistribusjonInfo distribusjonInfo = createDistribusjonInfo();
		distribusjonInfo.setDistribusjonKanal(distribusjonKanalCode);

		return distribusjonInfo;
	}

	public static DistribusjonInfo createDistribusjonInfoWithVarselstatus(VarselStatusCode varselStatusCode) {
		DistribusjonInfo distribusjonInfo = createDistribusjonInfo();
		distribusjonInfo.setVarselStatus(varselStatusCode);

		return distribusjonInfo;
	}

	public static DistribusjonInfo createDistribusjonInfo() {
		DistribusjonInfo distribusjonInfo = DistribusjonInfo.builder()
				.originalDistribusjonId(DISTRIBUSJON_ID)
				.distribusjonId(DISTRIBUSJON_ID)
				.distribusjonKanal(SDP)
				.distribusjonStatus(DistribusjonStatusCode.OPPRETTET)
				.produksjonDato(OPPRETTET_DATO)
				.distribusjonDato(DISTRIBUSJON_DATO)
				.modus(P)
				.varselStatus(VarselStatusCode.OPPRETTET)
				.build();
		distribusjonInfo.setChangeStamp(ChangeStamp.builder()
				.opprettetAv(OPPRETTET_AV)
				.opprettetDato(OPPRETTET_DATO)
				.build());

		return distribusjonInfo;
	}

	public static DokumentInfo createDokumentInfoWithDistribusjonKanal(DistribusjonKanalCode kanal) {
		DokumentInfo dokumentInfo = createDokumentInfo();

		dokumentInfo.setDistribusjonInfo(DistribusjonInfo.builder().distribusjonKanal(kanal).build());
		dokumentInfo.setVarselInfos(Set.of(createSMSVarselInfo(), createEpostVarselInfo()));

		return dokumentInfo;
	}

	public static DokumentInfo createDokumentInfoWithEkspedertDato(LocalDateTime ekspedertDato) {
		DokumentInfo dokumentInfo = createDokumentInfoWithStatusCode(EKSPEDERT);
		dokumentInfo.setArkivSystem(JOARK);
		dokumentInfo.setEkspedertDato(ekspedertDato);
		dokumentInfo.addVarselInfo(createEpostVarselInfo());
		dokumentInfo.addVarselInfo(createSMSVarselInfo());

		return dokumentInfo;
	}

	public static DokumentInfo createDokumentInfoWithStatusCode(DokumentStatusCode dokumentStatusCode) {
		DokumentInfo dokumentInfo = createDokumentInfo();
		dokumentInfo.setDokumentStatus(dokumentStatusCode);

		return dokumentInfo;
	}

	public static DokumentInfo createDokumentInfoWithStatusCodeAndDokumentId(DokumentStatusCode dokumentStatusCode, String dokumentId) {
		DokumentInfo dokumentInfo = createDokumentInfoWithStatusCode(dokumentStatusCode);
		dokumentInfo.setDokumentId(dokumentId);
		return dokumentInfo;
	}

	public static DokumentInfo createDokumentInfoWithDokumentId(String dokumentId) {
		DokumentInfo dokumentInfo = createDokumentInfo();
		dokumentInfo.setDokumentId(dokumentId);
		return dokumentInfo;
	}

	public static DokumentInfo createDokumentInfo() {
		DokumentInfo dokumentInfo = DokumentInfo.builder()
				.dokumentId(DISTRIBUSJON_ID)
				.bestillendeFagsystem(BESTILLENDE_FAGSYSTEM)
				.dokumentStatus(OPPRETTET)
				.forsendelseMetadata(new String(FORSENDELSE_METADATA))
				.forsendelseMetadataType(DPO_ARKIVMELDING)
				.mottakerId(MOTTAKER_ID)
				.mottakerNavn(MOTTAKER_NAVN)
				.mottakerIdType(PERSON)
				.fagomrade(FAGOMRADE_DAG)
				.konversasjonId(KONVERSASJON_ID)
				.arkivkode(ARKIV_KODE)
				.brevProduksjonApplikasjon(BREVPRODUKSJON_APPLIKASJONCODE)
				.digitalDistributorId(DIGITAL_DISTRIBUTOR_ID)
				.digitalPostkasseAdresse(DIGITALPOSTKASSE_ADRESSE)
				.postadresse(createPostadresse())
				.build();
		dokumentInfo.setChangeStamp(ChangeStamp.builder()
				.opprettetAv(OPPRETTET_AV)
				.opprettetDato(OPPRETTET_DATO)
				.endretAv(DOKDISTDPI)
				.endretDato(LocalDateTime.now())
				.build());

		return dokumentInfo;
	}

	public static VarselInfo createEpostVarselInfo() {
		return VarselInfo.builder()
				.epostAdresse(EPOSTADRESSE)
				.varslingKanal(EPOST)
				.varslingstittel(VARSELTITTEL)
				.varslingstekst(MELDING)
				.varslingstidspunkt(FIRST_VARSEL_SENDT_DATO)
				.build();
	}

	public static VarselInfo createSMSVarselInfo() {
		return VarselInfo.builder()
				.mobiltelefonNummer(TELEFONNUMMER)
				.varslingKanal(MOBILTELEFON)
				.varslingstekst(MELDING)
				.varslingstidspunkt(FIRST_VARSEL_SENDT_DATO)
				.build();
	}

	public static Postadresse createPostadresse() {
		return Postadresse.builder()
				.adresselinje1(ADRESSELINJE_1)
				.adresselinje2(ADRESSELINJE_2)
				.adresselinje3(ADRESSELINJE_3)
				.postnummer(POSTNUMMER)
				.poststed(POSTSTED)
				.landkode(LANDKODE)
				.build();
	}

	public static OpprettForsendelseRequest.Dokument createDokumentReferanseWithReferererTilAndRekkefoelge(RefererTilCode refererTilCode, Integer rekkefoelge) {
		return OpprettForsendelseRequest.Dokument.builder()
				.tilknyttetSom(refererTilCode)
				.dokumentObjektReferanse(DOKUMENT_OBJEKT_REFERANSE)
				.rekkefolge(rekkefoelge)
				.arkivDokumentInfoId(ARKIV_DOKUMENT_INFO_ID)
				.dokumenttypeId(DOKUMENT_TYPE_ID)
				.build();
	}

	public static DokumentReferanse createDokumentReferanseWithRefererTilAndRekkefoelge(RefererTilCode refererTilCode, Integer rekkefoelge) {
		return DokumentReferanse.builder()
				.dokumentUri(DOKUMENT_OBJEKT_REFERANSE)
				.dokumenttypeId(DOKUMENT_TYPE_ID)
				.refererTil(refererTilCode)
				.arkivDokumentInfoId(ARKIV_DOKUMENT_INFO_ID)
				.rekkefolge(rekkefoelge)
				.build();
	}

	public static OpprettForsendelseRequest createOpprettForsendelseRequest() {
		return OpprettForsendelseRequest.builder()
				.bestillingsId(BESTILLINGS_ID)
				.distribusjonsKanal(SDP)
				.bestillendeFagsystem(BESTILLENDE_FAGSYSTEM)
				.tema(FAGOMRADE_DAG)
				.forsendelseTittel(FORSENDELSE_TITTEL)
				.forsendelseMetadata(FORSENDELSE_METADATA)
				.forsendelseMetadataType(DPO_ARKIVMELDING)
				.dokumentProdApp(DOKUMENT_PRODUKSJON_APP)
				.originalDistribusjonId(ORIGINAL_DISTRIBUSJON_ID)
				.mottaker(OpprettForsendelseRequest.Mottaker.builder()
						.mottakerId(MOTTAKER_ID)
						.mottakerNavn(MOTTAKER_NAVN)
						.mottakerType(PERSON)
						.build())
				.dokumenter(List.of(
						createDokumentReferanseWithReferererTilAndRekkefoelge(HOVEDDOKUMENT, 1),
						createDokumentReferanseWithReferererTilAndRekkefoelge(VEDLEGG, 2)))
				.batchId(BATCH_ID)
				.distribusjonstype(DistribusjonsTypeKode.VEDTAK)
				.distribusjonstidspunkt(KJERNETID)
				.arkivInformasjon(OpprettForsendelseRequest.ArkivInformasjon.builder()
						.arkivSystem(JOARK)
						.arkivId(ARKIV_KODE)
						.build())
				.postadresse(OpprettForsendelseRequest.Postadresse.builder()
						.adresselinje1(ADRESSELINJE_1)
						.adresselinje2(ADRESSELINJE_2)
						.adresselinje3(ADRESSELINJE_3)
						.postnummer(POSTNUMMER)
						.poststed(POSTSTED)
						.landkode(LANDKODE)
						.build())
				.build();
	}

	public static FeilregistrerForsendelseRequest createFeilregistrerForsendelseRequestWithForsendelseId(Long forsendelseId) {
		return FeilregistrerForsendelseRequest.builder()
				.forsendelseId(forsendelseId)
				.feilTypeCode(MELDINGSFEIL)
				.tidspunkt(LocalDateTime.now())
				.detaljer(DETALJER)
				.build();
	}

	private static byte[] lesInnFil(String filsti) {
		try {
			return Rdist001TestUtils.class.getClassLoader()
					.getResourceAsStream(filsti)
					.readAllBytes();
		} catch (IOException | NullPointerException e) {
			throw new RuntimeException("Kunne ikke lese inn fil fra filsti=%s".formatted(filsti), e);
		}
	}
}
