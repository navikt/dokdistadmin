package no.nav.dokdistadmin.administrerforsendelse.forsendelser;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Value;
import no.nav.dokdistadmin.administrerforsendelse.opprettforsendelse.ValiderForsendelseMetadata;
import no.nav.dokdistadmin.domain.ArkivSystemCode;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DistribusjonsTypeKode;
import no.nav.dokdistadmin.domain.DistribusjonstidspunktKode;
import no.nav.dokdistadmin.domain.ForsendelseMetadataTypeCode;
import no.nav.dokdistadmin.domain.MottakerIdTypeCode;
import no.nav.dokdistadmin.domain.RefererTilCode;

import java.util.List;

@Value
@Builder(toBuilder = true)
@ValiderForsendelseMetadata
public class OpprettForsendelseRequest {
    @NotBlank(message = "bestillingsId må ha en verdi")
    @Pattern(regexp = "[A-Za-z0-9_ .-]*", message = "bestillingsId inneholder ulovlige tegn")
    String bestillingsId;

    @NotNull(message = "distribusjonsKanal kan ikke være null")
    DistribusjonKanalCode distribusjonsKanal;

    @NotBlank(message = "bestillendeFagsystem må ha en verdi")
    String bestillendeFagsystem;

    @NotBlank(message = "tema må ha en verdi")
    String tema;

    @NotBlank(message = "forsendelseTittel må ha en verdi")
    String forsendelseTittel;

    @NotBlank(message = "dokumentProdApp må ha en verdi")
    String dokumentProdApp;

    @Valid
    @NotNull(message = "mottaker kan ikke være null")
    Mottaker mottaker;

    @Valid
    @NotEmpty(message = "dokumenter kan ikke være null eller en tom liste")
    List<Dokument> dokumenter;

    String originalDistribusjonId;
    String batchId;
    DistribusjonsTypeKode distribusjonstype;
    DistribusjonstidspunktKode distribusjonstidspunkt;
    byte[] forsendelseMetadata;
    ForsendelseMetadataTypeCode forsendelseMetadataType;

    @Valid
    ArkivInformasjon arkivInformasjon;

    @Valid
    Postadresse postadresse;

    @Value
    @Builder
    public static class Mottaker {
        @NotBlank(message = "mottakerId må ha en verdi")
        String mottakerId;

        @NotBlank(message = "mottakerNavn må ha en verdi")
        String mottakerNavn;

        @NotNull(message = "mottakerType kan ikke være null")
        MottakerIdTypeCode mottakerType;
    }

    @Value
    @Builder
    public static class ArkivInformasjon {
        @NotNull(message = "arkivSystem kan ikke være null")
        ArkivSystemCode arkivSystem;

        @NotBlank(message = "arkivId må ha en verdi")
        String arkivId;
    }

    @Value
    @Builder
    public static class Postadresse {
        String adresselinje1;
        String adresselinje2;
        String adresselinje3;
        String postnummer;
        String poststed;

        @NotBlank(message = "landkode må ha en verdi")
        String landkode;
    }

    @Value
    @Builder
    public static class Dokument {
        @NotNull(message = "tilknyttetSom kan ikke være null")
        RefererTilCode tilknyttetSom;

        @NotBlank(message = "dokumentObjektReferanse må ha en verdi")
        String dokumentObjektReferanse;

        @NotNull(message = "rekkefolge kan ikke være null")
        @PositiveOrZero(message = "rekkefolge må være 0 eller et positivt tall")
        Integer rekkefolge;

        String arkivDokumentInfoId;

        @NotBlank(message = "dokumenttypeId må ha en verdi")
        String dokumenttypeId;
    }
}
