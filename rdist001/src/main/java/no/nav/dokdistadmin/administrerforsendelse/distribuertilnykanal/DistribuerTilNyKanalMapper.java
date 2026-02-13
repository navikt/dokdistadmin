package no.nav.dokdistadmin.administrerforsendelse.distribuertilnykanal;

import no.nav.dokdistadmin.administrerforsendelse.forsendelser.HentForsendelseResponse;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequest;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.ForsendelseMetadataTypeCode;
import no.nav.dokdistadmin.domain.MottakerIdTypeCode;
import no.nav.dokdistadmin.domain.RefererTilCode;

import java.util.UUID;
import java.util.stream.IntStream;

public class DistribuerTilNyKanalMapper {

    protected static final String RESENDING_DOKUMENTTYPE_ID = "U000001";

    private DistribuerTilNyKanalMapper() {
    }

    public static OpprettForsendelseRequest mapTilOpprettForsendelseRequest(HentForsendelseResponse originalForsendelse, DistribusjonKanalCode nyKanal) {

        var dokumenter = IntStream.range(0, originalForsendelse.getDokumenter().size())
                .mapToObj(i -> mapDokument(originalForsendelse.getDokumenter().get(i), i))
                .toList();

        var builder = OpprettForsendelseRequest.builder()
                .bestillingsId(UUID.randomUUID().toString())
                .distribusjonsKanal(nyKanal)
                .originalDistribusjonId(originalForsendelse.getBestillingsId())
                .bestillendeFagsystem(originalForsendelse.getBestillendeFagsystem())
                .tema(originalForsendelse.getTema())
                .forsendelseTittel(originalForsendelse.getForsendelseTittel())
                .dokumentProdApp(originalForsendelse.getDokumentProdApp())
                .mottaker(mapMottaker(originalForsendelse.getMottaker()))
                .dokumenter(dokumenter)
                .batchId(originalForsendelse.getBatchId())
                .distribusjonstype(originalForsendelse.getDistribusjonstype())
                .distribusjonstidspunkt(originalForsendelse.getDistribusjonstidspunkt())
                .forsendelseMetadata(originalForsendelse.getForsendelseMetadata());

        if (originalForsendelse.getForsendelseMetadataType() != null) {
            builder.forsendelseMetadataType(ForsendelseMetadataTypeCode.valueOf(originalForsendelse.getForsendelseMetadataType()));
        }

        if (originalForsendelse.getArkivInformasjon() != null) {
            builder.arkivInformasjon(mapArkivInformasjon(originalForsendelse.getArkivInformasjon()));
        }

        if (originalForsendelse.getPostadresse() != null) {
            builder.postadresse(mapPostadresse(originalForsendelse.getPostadresse()));
        }

        return builder.build();
    }

    private static OpprettForsendelseRequest.Mottaker mapMottaker(HentForsendelseResponse.Mottaker mottaker) {
        return OpprettForsendelseRequest.Mottaker.builder()
                .mottakerId(mottaker.getMottakerId())
                .mottakerNavn(mottaker.getMottakerNavn())
                .mottakerType(MottakerIdTypeCode.valueOf(mottaker.getMottakerType()))
                .build();
    }

    private static OpprettForsendelseRequest.ArkivInformasjon mapArkivInformasjon(HentForsendelseResponse.ArkivInformasjon arkivInfo) {
        return OpprettForsendelseRequest.ArkivInformasjon.builder()
                .arkivSystem(arkivInfo.getArkivSystem())
                .arkivId(arkivInfo.getArkivId())
                .build();
    }

    private static OpprettForsendelseRequest.Postadresse mapPostadresse(HentForsendelseResponse.Postadresse postadresse) {
        return OpprettForsendelseRequest.Postadresse.builder()
                .adresselinje1(postadresse.getAdresselinje1())
                .adresselinje2(postadresse.getAdresselinje2())
                .adresselinje3(postadresse.getAdresselinje3())
                .postnummer(postadresse.getPostnummer())
                .poststed(postadresse.getPoststed())
                .landkode(postadresse.getLandkode())
                .build();
    }

    private static OpprettForsendelseRequest.Dokument mapDokument(HentForsendelseResponse.Dokument dokument, int index) {
        return OpprettForsendelseRequest.Dokument.builder()
                .tilknyttetSom(RefererTilCode.valueOf(dokument.getTilknyttetSom()))
                .dokumentObjektReferanse(dokument.getDokumentObjektReferanse())
                .rekkefolge(index)
                .arkivDokumentInfoId(dokument.getArkivDokumentInfoId())
                .dokumenttypeId(RESENDING_DOKUMENTTYPE_ID)
                .build();
    }
}

