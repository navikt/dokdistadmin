package no.nav.dokdistadmin.administrerforsendelse.forsendelser;

import no.nav.dokdistadmin.exception.functional.KanIkkeBestemmeDokumentrekkefoelgeException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static java.lang.String.format;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.ARKIV_DOKUMENT_INFO_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.DOKUMENTINFO_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.DOKUMENT_OBJEKT_REFERANSE;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.DOKUMENT_REFERANSE_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.DOKUMENT_TYPE_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentReferanseWithRefererTilAndRekkefoelge;
import static no.nav.dokdistadmin.domain.RefererTilCode.HOVEDDOKUMENT;
import static no.nav.dokdistadmin.domain.RefererTilCode.VEDLEGG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HentForsendelseResponseMapperTest {

	@Test
	void skalMappeTilHentForsendelseResponse() {
		var dokumentInfo = createDokumentInfo();
		ReflectionTestUtils.setField(dokumentInfo, "dokumentInfoId", DOKUMENTINFO_ID);

		var distribusjonInfo = createDistribusjonInfo();
		dokumentInfo.addDokumentReferanse(createDokumentReferanseWithRefererTilAndRekkefoelge(HOVEDDOKUMENT, 1));
		dokumentInfo.addDokumentReferanse(createDokumentReferanseWithRefererTilAndRekkefoelge(VEDLEGG, 2));
		dokumentInfo.setDistribusjonInfo(distribusjonInfo);

		var hentForsendelseResponse = HentForsendelseResponseMapper.map(dokumentInfo);

		var forventetDistribusjonInfo = dokumentInfo.getDistribusjonInfo();
		assertThat(hentForsendelseResponse.getForsendelseId()).isEqualTo(dokumentInfo.getDokumentInfoId());
		assertThat(hentForsendelseResponse.getBestillingsId()).isEqualTo(dokumentInfo.getDokumentId());
		assertThat(hentForsendelseResponse.getOriginalBestillingsId()).isEqualTo(forventetDistribusjonInfo.getOriginalDistribusjonId());
		assertThat(hentForsendelseResponse.getKonversasjonId()).isEqualTo(dokumentInfo.getKonversasjonId());
		assertThat(hentForsendelseResponse.getBestillendeFagsystem()).isEqualTo(dokumentInfo.getBestillendeFagsystem());
		assertThat(hentForsendelseResponse.getModus()).isEqualTo(forventetDistribusjonInfo.getModus());
		assertThat(hentForsendelseResponse.getDistribusjonstype()).isEqualTo(forventetDistribusjonInfo.getDistribusjonstype());
		assertThat(hentForsendelseResponse.getDistribusjonstidspunkt()).isEqualTo(forventetDistribusjonInfo.getDistribusjonstidspunkt());
		assertThat(hentForsendelseResponse.getForsendelseStatus()).isEqualTo(dokumentInfo.getDokumentStatus().name());
		assertThat(hentForsendelseResponse.getDistribusjonKanal()).isEqualTo(forventetDistribusjonInfo.getDistribusjonKanal().name());
		assertThat(hentForsendelseResponse.getTema()).isEqualTo(dokumentInfo.getFagomrade());
		assertThat(hentForsendelseResponse.getForsendelseTittel()).isEqualTo(dokumentInfo.getForsendelseTittel());
		assertThat(hentForsendelseResponse.getForsendelseMetadata()).isEqualTo(dokumentInfo.getForsendelseMetadata());
		assertThat(hentForsendelseResponse.getForsendelseMetadataType()).isEqualTo(dokumentInfo.getForsendelseMetadataType().name());
		assertThat(hentForsendelseResponse.getBatchId()).isEqualTo(dokumentInfo.getBatchId());
		assertThat(hentForsendelseResponse.getDokumentProdApp()).isEqualTo(dokumentInfo.getBrevProduksjonApplikasjon());
		assertThat(hentForsendelseResponse.getVarselStatus()).isEqualTo(forventetDistribusjonInfo.getVarselStatus().name());

		var faktiskMottaker = hentForsendelseResponse.getMottaker();
		assertThat(faktiskMottaker.getMottakerId()).isEqualTo(dokumentInfo.getMottakerId());
		assertThat(faktiskMottaker.getMottakerType()).isEqualTo(dokumentInfo.getMottakerIdType().name());
		assertThat(faktiskMottaker.getMottakerNavn()).isEqualTo(dokumentInfo.getMottakerNavn());

		var faktiskArkivInformasjon = hentForsendelseResponse.getArkivInformasjon();
		assertThat(faktiskArkivInformasjon.getArkivSystem()).isEqualTo(dokumentInfo.getArkivSystem());
		assertThat(faktiskArkivInformasjon.getArkivId()).isEqualTo(dokumentInfo.getArkivkode());

		var forventetPostadresse = dokumentInfo.getPostadresse();
		var faktiskPostadresse = hentForsendelseResponse.getPostadresse();
		assertThat(faktiskPostadresse.getAdresselinje1()).isEqualTo(forventetPostadresse.getAdresselinje1());
		assertThat(faktiskPostadresse.getAdresselinje2()).isEqualTo(forventetPostadresse.getAdresselinje2());
		assertThat(faktiskPostadresse.getAdresselinje3()).isEqualTo(forventetPostadresse.getAdresselinje3());
		assertThat(faktiskPostadresse.getLandkode()).isEqualTo(forventetPostadresse.getLandkode());
		assertThat(faktiskPostadresse.getPostnummer()).isEqualTo(forventetPostadresse.getPostnummer());
		assertThat(faktiskPostadresse.getPoststed()).isEqualTo(forventetPostadresse.getPoststed());

		assertThat(hentForsendelseResponse.getDokumenter())
				.hasSize(2)
				.allSatisfy(dokument -> {
					assertThat(dokument.getTilknyttetSom()).isIn(HOVEDDOKUMENT.name(), VEDLEGG.name());
					assertThat(dokument.getDokumentObjektReferanse()).isEqualTo(DOKUMENT_OBJEKT_REFERANSE);
					assertThat(dokument.getArkivDokumentInfoId()).isEqualTo(ARKIV_DOKUMENT_INFO_ID);
					assertThat(dokument.getDokumenttypeId()).isEqualTo(DOKUMENT_TYPE_ID);
				});
	}

	@Test
	void skalKasteExceptionForUgyldigRekkefoelge() {
		var dokumentInfo = createDokumentInfo();
		var distribusjonInfo = createDistribusjonInfo();

		ReflectionTestUtils.setField(dokumentInfo, "dokumentInfoId", DOKUMENTINFO_ID);

		var dokumentReferanseHoveddokument = createDokumentReferanseWithRefererTilAndRekkefoelge(HOVEDDOKUMENT, -1);
		var dokumentReferanseVedlegg1 = createDokumentReferanseWithRefererTilAndRekkefoelge(VEDLEGG, 0);
		var dokumentReferanseVedlegg2 = createDokumentReferanseWithRefererTilAndRekkefoelge(VEDLEGG, null);

		ReflectionTestUtils.setField(dokumentReferanseHoveddokument, "dokumentReferanseId", DOKUMENT_REFERANSE_ID);
		ReflectionTestUtils.setField(dokumentReferanseVedlegg1, "dokumentReferanseId", DOKUMENT_REFERANSE_ID);
		ReflectionTestUtils.setField(dokumentReferanseVedlegg2, "dokumentReferanseId", DOKUMENT_REFERANSE_ID);

		dokumentInfo.addDokumentReferanse(dokumentReferanseHoveddokument);
		dokumentInfo.addDokumentReferanse(dokumentReferanseVedlegg1);
		dokumentInfo.addDokumentReferanse(dokumentReferanseVedlegg2);
		dokumentInfo.setDistribusjonInfo(distribusjonInfo);

		var exception = assertThrows(KanIkkeBestemmeDokumentrekkefoelgeException.class,
				() -> HentForsendelseResponseMapper.map(dokumentInfo));

		assertThat(exception.getMessage()).contains(
				"Kan ikke sortere dokumenter, da ett eller flere dokumenter har ugyldig verdi for feltet rekkefølge (rekkefølge må være større enn 0)",
				format("Dokument med dokumentReferanseId=%s har ugyldig rekkefølge=%s", DOKUMENT_REFERANSE_ID, -1),
				format("Dokument med dokumentReferanseId=%s har ugyldig rekkefølge=%s", DOKUMENT_REFERANSE_ID, 0),
				format("Dokument med dokumentReferanseId=%s har ugyldig rekkefølge=%s", DOKUMENT_REFERANSE_ID, "null")
		);
	}
}