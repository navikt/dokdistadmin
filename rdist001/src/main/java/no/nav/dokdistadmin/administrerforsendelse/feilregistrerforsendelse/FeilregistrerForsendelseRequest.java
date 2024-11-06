package no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;
import no.nav.dokdistadmin.domain.FeilTypeCode;

import java.time.LocalDateTime;

@Value
@Builder
public class FeilregistrerForsendelseRequest {

    @Positive(message = "forsendelseId må være et positivt tall")
    Long forsendelseId;

    @NotNull(message = "type kan ikke være null")
    FeilTypeCode feilTypeCode;

    @NotNull(message = "tidspunkt kan ikke være null")
    LocalDateTime tidspunkt;

    @NotBlank(message = "detaljer må ha en verdi")
    String detaljer;

    String part;
    String resendingDistribusjonId;
}
