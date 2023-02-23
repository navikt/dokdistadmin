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
    @NotBlank private String bestillingsId;
    @NotNull private DistribusjonKanalCode distribusjonsKanal;
    @NotBlank private String bestillendeFagsystem;
    @NotNull private FagomradeCode tema;
    @NotBlank private String forsendelseTittel;
    @NotBlank private String dokumentProdApp;
    @NotBlank private String originalDistribusjonId;
    @NotNull private Mottaker mottaker;
    @NotEmpty private List<Dokument> dokumenter;
    private String batchId;
    private DistribusjonsTypeKode distribusjonstype;
    private DistribusjonstidspunktKode distribusjonstidspunkt;
    private ArkivInformasjon arkivInformasjon;
    private Postadresse postadresse;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Mottaker {
        @NotBlank private String mottakerId;
        @NotBlank private String mottakerNavn;
        @NotNull private MottakerIdTypeCode mottakerType;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArkivInformasjon {
        @NotNull private ArkivSystemCode arkivSystem;
        @NotBlank private String arkivId;
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
        @NotBlank String landkode;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Dokument {
        @NotNull private RefererTilCode tilknyttetSom;
        @NotBlank private String dokumentObjektReferanse;
        @PositiveOrZero private Integer rekkefolge;
        private String arkivDokumentInfoId;
        @NotBlank private String dokumenttypeId;
    }
}
