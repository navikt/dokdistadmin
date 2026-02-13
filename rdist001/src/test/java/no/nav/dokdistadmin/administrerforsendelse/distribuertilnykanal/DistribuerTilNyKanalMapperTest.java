package no.nav.dokdistadmin.administrerforsendelse.distribuertilnykanal;

import no.nav.dokdistadmin.administrerforsendelse.forsendelser.HentForsendelseResponse;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequest;
import no.nav.dokdistadmin.domain.ArkivSystemCode;
import no.nav.dokdistadmin.domain.DistribusjonsTypeKode;
import no.nav.dokdistadmin.domain.DistribusjonstidspunktKode;
import no.nav.dokdistadmin.domain.ForsendelseMetadataTypeCode;
import no.nav.dokdistadmin.domain.MottakerIdTypeCode;
import no.nav.dokdistadmin.domain.RefererTilCode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.nav.dokdistadmin.administrerforsendelse.distribuertilnykanal.DistribuerTilNyKanalMapper.RESENDING_DOKUMENTTYPE_ID;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class DistribuerTilNyKanalMapperTest {

    @Test
    void skalMappeOriginalForsendelseTilOpprettRequest() {
        HentForsendelseResponse originalForsendelse = createForsendelse();

		OpprettForsendelseRequest result = DistribuerTilNyKanalMapper.mapTilOpprettForsendelseRequest(originalForsendelse, PRINT);

        assertThat(result.getBestillingsId()).isNotNull();
        assertThat(result.getBestillingsId()).isNotEqualTo(originalForsendelse.getBestillingsId());
        assertThat(result.getDistribusjonsKanal()).isEqualTo(PRINT);
        assertThat(result.getOriginalDistribusjonId()).isEqualTo(originalForsendelse.getBestillingsId());
        assertThat(result.getBestillendeFagsystem()).isEqualTo(originalForsendelse.getBestillendeFagsystem());
        assertThat(result.getTema()).isEqualTo(originalForsendelse.getTema());
        assertThat(result.getForsendelseTittel()).isEqualTo(originalForsendelse.getForsendelseTittel());
        assertThat(result.getDokumentProdApp()).isEqualTo(originalForsendelse.getDokumentProdApp());

        assertThat(result.getBatchId()).isEqualTo(originalForsendelse.getBatchId());
        assertThat(result.getDistribusjonstype()).isEqualTo(originalForsendelse.getDistribusjonstype());
        assertThat(result.getDistribusjonstidspunkt()).isEqualTo(originalForsendelse.getDistribusjonstidspunkt());
        assertThat(result.getForsendelseMetadata()).isEqualTo(originalForsendelse.getForsendelseMetadata());
        assertThat(result.getForsendelseMetadataType()).isEqualTo(ForsendelseMetadataTypeCode.valueOf(originalForsendelse.getForsendelseMetadataType()));

        var originalMottaker = originalForsendelse.getMottaker();
        assertThat(result.getMottaker())
                .satisfies(mottaker -> {
                    assertThat(mottaker.getMottakerId()).isEqualTo(originalMottaker.getMottakerId());
                    assertThat(mottaker.getMottakerNavn()).isEqualTo(originalMottaker.getMottakerNavn());
                    assertThat(mottaker.getMottakerType()).isEqualTo(MottakerIdTypeCode.valueOf(originalMottaker.getMottakerType()));
                });

        assertThat(result.getArkivInformasjon())
                .usingRecursiveComparison()
                .isEqualTo(originalForsendelse.getArkivInformasjon());

        assertThat(result.getPostadresse())
                .usingRecursiveComparison()
                .isEqualTo(originalForsendelse.getPostadresse());

        var originalDokumenter = originalForsendelse.getDokumenter();
        var originalHoveddokument = originalDokumenter.getFirst();
        var originalVedlegg = originalDokumenter.getLast();

        assertThat(result.getDokumenter())
                .hasSize(2)
                .extracting(
                        OpprettForsendelseRequest.Dokument::getTilknyttetSom,
                        OpprettForsendelseRequest.Dokument::getDokumentObjektReferanse,
                        OpprettForsendelseRequest.Dokument::getRekkefolge,
                        OpprettForsendelseRequest.Dokument::getArkivDokumentInfoId,
                        OpprettForsendelseRequest.Dokument::getDokumenttypeId
                )
                .containsExactly(
                        tuple(RefererTilCode.valueOf(originalHoveddokument.getTilknyttetSom()),
                                originalHoveddokument.getDokumentObjektReferanse(),
                                0,
                                originalHoveddokument.getArkivDokumentInfoId(),
                                RESENDING_DOKUMENTTYPE_ID),
                        tuple(RefererTilCode.valueOf(originalVedlegg.getTilknyttetSom()),
                                originalVedlegg.getDokumentObjektReferanse(),
                                1,
                                originalVedlegg.getArkivDokumentInfoId(),
                                RESENDING_DOKUMENTTYPE_ID)
                );
    }

    private HentForsendelseResponse createForsendelse() {
        return HentForsendelseResponse.builder()
                .distribusjonstype(DistribusjonsTypeKode.VEDTAK)
                .distribusjonstidspunkt(DistribusjonstidspunktKode.UMIDDELBART)
                .batchId("batch-123")
                .forsendelseMetadata("metadata-innhold".getBytes())
                .forsendelseMetadataType("DPO_ARKIVMELDING")
                .arkivInformasjon(HentForsendelseResponse.ArkivInformasjon.builder()
                        .arkivSystem(ArkivSystemCode.JOARK)
                        .arkivId("453123456")
                        .build())
                .mottaker(HentForsendelseResponse.Mottaker.builder()
                        .mottakerId("12345678901")
                        .mottakerNavn("Test Testesen")
                        .mottakerType("PERSON")
                        .build())
                .postadresse(HentForsendelseResponse.Postadresse.builder()
                        .adresselinje1("Testveien 1")
                        .adresselinje2("Etasje 3")
                        .adresselinje3("Inngang B")
                        .postnummer("0123")
                        .poststed("Oslo")
                        .landkode("NO")
                        .build())
                .dokumenter(List.of(
                        HentForsendelseResponse.Dokument.builder()
                                .tilknyttetSom("HOVEDDOKUMENT")
                                .dokumentObjektReferanse("dok-ref-1")
                                .arkivDokumentInfoId("arkiv-dok-1")
                                .dokumenttypeId("TYPE_A")
                                .build(),
                        HentForsendelseResponse.Dokument.builder()
                                .tilknyttetSom("VEDLEGG")
                                .dokumentObjektReferanse("dok-ref-2")
                                .arkivDokumentInfoId("arkiv-dok-2")
                                .dokumenttypeId("TYPE_B")
                                .build()
                )).build();
    }
}

