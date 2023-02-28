package no.nav.dokdistadmin.administrerforsendelse.forsendelser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dokdistadmin.domain.ArkivSystemCode;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DistribusjonsTypeKode;
import no.nav.dokdistadmin.domain.DistribusjonstidspunktKode;
import no.nav.dokdistadmin.domain.FagomradeCode;
import no.nav.dokdistadmin.domain.MottakerIdTypeCode;
import no.nav.dokdistadmin.domain.RefererTilCode;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpprettForsendelseRequest {
    @NotBlank(message = "bestillingsId må ha en verdi")
    private String bestillingsId;

    @NotNull(message = "distribusjonsKanal kan ikke være null")
    private DistribusjonKanalCode distribusjonsKanal;

    @NotBlank(message = "bestillendeFagsystem må ha en verdi")
    private String bestillendeFagsystem;

    @NotNull(message = "tema kan ikke være null")
    private FagomradeCode tema;

    @NotBlank(message = "forsendelseTittel må ha en verdi")
    private String forsendelseTittel;

    @NotBlank(message = "dokumentProdApp må ha en verdi")
    private String dokumentProdApp;

    @NotBlank(message = "originalDistribusjonId må ha en verdi")
    private String originalDistribusjonId;

    @Valid
    @NotNull(message = "mottaker kan ikke være null")
    private Mottaker mottaker;

    @Valid
    @NotEmpty(message = "dokumenter kan ikke være null eller en tom liste")
    private List<Dokument> dokumenter;

    private String batchId;
    private DistribusjonsTypeKode distribusjonstype;
    private DistribusjonstidspunktKode distribusjonstidspunkt;

    @Valid
    private ArkivInformasjon arkivInformasjon;

    @Valid
    private Postadresse postadresse;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Mottaker {
        @NotBlank(message = "mottakerId må ha en verdi")
        private String mottakerId;

        @NotBlank(message = "mottakerNavn må ha en verdi")
        private String mottakerNavn;

        @NotNull(message = "mottakerType kan ikke være null")
        private MottakerIdTypeCode mottakerType;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArkivInformasjon {
        @NotNull(message = "arkivSystem kan ikke være null")
        private ArkivSystemCode arkivSystem;

        @NotBlank(message = "arkivId må ha en verdi")
        private String arkivId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Postadresse {
        String adresselinje1;
        String adresselinje2;
        String adresselinje3;
        String postnummer;
        String poststed;

        @NotBlank(message = "landkode må ha en verdi")
        String landkode;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Dokument {
        @NotNull(message = "tilknyttetSom kan ikke være null")
        private RefererTilCode tilknyttetSom;

        @NotBlank(message = "dokumentObjektReferanse må ha en verdi")
        private String dokumentObjektReferanse;

        @NotNull(message = "rekkefolge kan ikke være null")
        @PositiveOrZero(message = "rekkefolge må være 0 eller et positivt tall")
        private Integer rekkefolge;

        private String arkivDokumentInfoId;

        @NotBlank(message = "dokumenttypeId må ha en verdi")
        private String dokumenttypeId;
    }
}
