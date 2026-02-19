package no.nav.dokdistadmin.administrerforsendelse.distribuertilnykanal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DistribuerTilNyKanalRequest {

    @Positive(message = "forsendelseId må være et positivt tall")
    long forsendelseId;

    @NotBlank(message = "kanal må ha en verdi")
    String kanal;

    @NotBlank(message = "arsak må ha en verdi")
    String arsak;

    @NotBlank(message = "arsakBeskrivelse må ha en verdi")
    @Size(max = 1000, message = "arsakBeskrivelse kan ikke være lengre enn 1000 tegn")
    String arsakBeskrivelse;
}

