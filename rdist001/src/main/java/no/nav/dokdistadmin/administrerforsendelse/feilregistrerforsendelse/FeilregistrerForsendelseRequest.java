package no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dokdistadmin.domain.FeilTypeCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeilregistrerForsendelseRequest {

    @Positive(message = "forsendelseId må være et positivt tall")
    private Long forsendelseId;

    @NotNull(message = "type kan ikke være null")
    private FeilTypeCode feilTypeCode;

    @NotNull(message = "tidspunkt kan ikke være null")
    private LocalDateTime tidspunkt;

    @NotBlank(message = "detaljer må ha en verdi")
    private String detaljer;

    private String part;
    private String resendingDistribusjonId;
}
