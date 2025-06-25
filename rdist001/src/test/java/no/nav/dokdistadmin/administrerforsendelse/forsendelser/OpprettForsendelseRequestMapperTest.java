package no.nav.dokdistadmin.administrerforsendelse.forsendelser;

import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequest.ArkivInformasjon;
import no.nav.dokdistadmin.domain.ArkivSystemCode;
import no.nav.dokdistadmin.domain.ForsendelseMetadataTypeCode;
import no.nav.dokdistadmin.domain.ModusCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.ADRESSELINJE_1;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.ADRESSELINJE_2;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.ADRESSELINJE_3;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.ARKIV_DOKUMENT_INFO_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.ARKIV_KODE;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.BATCH_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.BESTILLENDE_FAGSYSTEM;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.BESTILLINGS_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.DOKUMENT_OBJEKT_REFERANSE;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.DOKUMENT_PRODUKSJON_APP;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.DOKUMENT_TYPE_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.FAGOMRADE_DAG;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.FORSENDELSE_TITTEL;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.LANDKODE;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.MOTTAKER_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.MOTTAKER_NAVN;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.ORIGINAL_DISTRIBUSJON_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.POSTNUMMER;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.POSTSTED;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createOpprettForsendelseRequest;
import static no.nav.dokdistadmin.domain.ArkivSystemCode.INGEN;
import static no.nav.dokdistadmin.domain.ArkivSystemCode.JOARK;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.SDP;
import static no.nav.dokdistadmin.domain.DistribusjonsTypeKode.VEDTAK;
import static no.nav.dokdistadmin.domain.DistribusjonstidspunktKode.KJERNETID;
import static no.nav.dokdistadmin.domain.ForsendelseMetadataTypeCode.DPO_ARKIVMELDING;
import static no.nav.dokdistadmin.domain.ModusCode.T;
import static no.nav.dokdistadmin.domain.MottakerIdTypeCode.PERSON;
import static no.nav.dokdistadmin.domain.RefererTilCode.HOVEDDOKUMENT;
import static no.nav.dokdistadmin.domain.RefererTilCode.VEDLEGG;
import static org.assertj.core.api.Assertions.assertThat;

class OpprettForsendelseRequestMapperTest {

	@Test
	void shouldMapToDistribusjonInfo() {

		var request = createOpprettForsendelseRequest();

		var distribusjonInfo = OpprettForsendelseRequestMapper.mapToDistribusjonInfo(request);
		var dokumentInfo = distribusjonInfo.getDokumentInfos().iterator().next();
		var postadresse = dokumentInfo.getPostadresse();

		assertThat(distribusjonInfo.getDistribusjonId()).isEqualTo(BESTILLINGS_ID);
		assertThat(distribusjonInfo.getDistribusjonKanal()).isEqualTo(SDP);
		assertThat(distribusjonInfo.getOriginalDistribusjonId()).isEqualTo(ORIGINAL_DISTRIBUSJON_ID);
		assertThat(distribusjonInfo.getDistribusjonstype()).isEqualTo(VEDTAK);
		assertThat(distribusjonInfo.getDistribusjonstidspunkt()).isEqualTo(KJERNETID);
		assertThat(distribusjonInfo.getModus()).isEqualTo(T);

		assertThat(dokumentInfo.getBestillendeFagsystem()).isEqualTo(BESTILLENDE_FAGSYSTEM);
		assertThat(dokumentInfo.getFagomrade()).isEqualTo(FAGOMRADE_DAG);
		assertThat(dokumentInfo.getForsendelseTittel()).isEqualTo(FORSENDELSE_TITTEL);
		assertThat(dokumentInfo.getBrevProduksjonApplikasjon()).isEqualTo(DOKUMENT_PRODUKSJON_APP);
		assertThat(dokumentInfo.getMottakerId()).isEqualTo(MOTTAKER_ID);
		assertThat(dokumentInfo.getMottakerNavn()).isEqualTo(MOTTAKER_NAVN);
		assertThat(dokumentInfo.getMottakerIdType()).isEqualTo(PERSON);
		assertThat(dokumentInfo.getBatchId()).isEqualTo(BATCH_ID);
		assertThat(dokumentInfo.getArkivSystem()).isEqualTo(JOARK);
		assertThat(dokumentInfo.getArkivkode()).isEqualTo(ARKIV_KODE);

		assertThat(dokumentInfo.getDokumentReferanses())
				.hasSize(2)
				.allSatisfy(dokumentReferanse -> {
					assertThat(dokumentReferanse.getRefererTil()).isIn(HOVEDDOKUMENT, VEDLEGG);
					assertThat(dokumentReferanse.getDokumentUri()).isEqualTo(DOKUMENT_OBJEKT_REFERANSE);
					assertThat(dokumentReferanse.getRekkefolge()).isIn(1, 2);
					assertThat(dokumentReferanse.getArkivDokumentInfoId()).isEqualTo(ARKIV_DOKUMENT_INFO_ID);
					assertThat(dokumentReferanse.getDokumenttypeId()).isEqualTo(DOKUMENT_TYPE_ID);
				});

		assertThat(postadresse.getAdresselinje1()).isEqualTo(ADRESSELINJE_1);
		assertThat(postadresse.getAdresselinje2()).isEqualTo(ADRESSELINJE_2);
		assertThat(postadresse.getAdresselinje3()).isEqualTo(ADRESSELINJE_3);
		assertThat(postadresse.getPostnummer()).isEqualTo(POSTNUMMER);
		assertThat(postadresse.getPoststed()).isEqualTo(POSTSTED);
		assertThat(postadresse.getLandkode()).isEqualTo(LANDKODE);

	}

	@ParameterizedTest
	@MethodSource
	void shouldMapArkivSystem(ArkivInformasjon arkivInformasjon, ArkivSystemCode resultat) {

		var request = createOpprettForsendelseRequest().toBuilder()
				.arkivInformasjon(arkivInformasjon)
				.build();

		var distribusjonInfo = OpprettForsendelseRequestMapper.mapToDistribusjonInfo(request);
		var dokumentInfo = distribusjonInfo.getDokumentInfos().iterator().next();

		assertThat(dokumentInfo.getArkivSystem()).isEqualTo(resultat);
	}

	private static Stream<Arguments> shouldMapArkivSystem() {
		return Stream.of(
				Arguments.of(ArkivInformasjon.builder().arkivSystem(JOARK).build(), JOARK),
				Arguments.of(null, INGEN),
				Arguments.of(ArkivInformasjon.builder().arkivSystem(null).build(), INGEN)
		);
	}

	@ParameterizedTest
	@EnumSource(ModusCode.class)
	void shouldMapModus(ModusCode modus) {
		var request = createOpprettForsendelseRequest();

		var result = OpprettForsendelseRequestMapper.mapToDistribusjonInfo(request, modus);

		assertThat(result.getModus()).isEqualTo(modus);
	}

	@Test
	void shouldMapNoModus() {
		var request = createOpprettForsendelseRequest();

		var result = OpprettForsendelseRequestMapper.mapToDistribusjonInfo(request);

		assertThat(result.getModus()).isEqualTo(T);
	}

	@ParameterizedTest
	@CsvSource(value =
			{
					"null, null",
					"forsendelseMetadata, DPO_ARKIVMELDING"
			},
			nullValues = "null"
	)
	void shouldMapForsendelseMetadataAndType(String forsendelseMetadata, ForsendelseMetadataTypeCode type) {
		var request = createOpprettForsendelseRequest()
				.toBuilder()
				.forsendelseMetadata(forsendelseMetadata)
				.forsendelseMetadataType(type)
				.build();

		var distribusjonInfo = OpprettForsendelseRequestMapper.mapToDistribusjonInfo(request);
		distribusjonInfo.getDokumentInfos().forEach(dokumentInfo -> {
			assertThat(dokumentInfo.getForsendelseMetadata()).isEqualTo(forsendelseMetadata);
			assertThat(dokumentInfo.getForsendelseMetadataType()).isEqualTo(type);
		});
	}

	@ParameterizedTest
	@MethodSource
	void shouldThrowExceptionWhenOnlyOneOfForsendelseMetadataOrTypeIsSet(String forsendelseMetadata,
																		 ForsendelseMetadataTypeCode forsendelseMetadataType) {
		var request = createOpprettForsendelseRequest()
				.toBuilder()
				.forsendelseMetadata(forsendelseMetadata)
				.forsendelseMetadataType(forsendelseMetadataType)
				.build();

		var distribusjonInfo = Assertions.assertThrows(IllegalArgumentException.class,
				() -> OpprettForsendelseRequestMapper.mapToDistribusjonInfo(request));

		assertThat(distribusjonInfo.getMessage()).contains("Forsendelsesmetadata og -type må enten begge være satt, eller begge være null.");
	}

	private static Stream<Arguments> shouldThrowExceptionWhenOnlyOneOfForsendelseMetadataOrTypeIsSet() {
		return Stream.of(
				Arguments.of(null, DPO_ARKIVMELDING),
				Arguments.of("forsendelseMetadata", null)
		);
	}

}