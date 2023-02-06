package no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dokdistadmin.administrerforsendelse.Forsendelse;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvstemForsendelserRequest {

    @NotEmpty(message = "avstemtReferanse kan ikke være null eller en tom streng")
    private String avstemtReferanse;

    @NotNull
    @Valid
    private List<Forsendelse> forsendelser;

}
