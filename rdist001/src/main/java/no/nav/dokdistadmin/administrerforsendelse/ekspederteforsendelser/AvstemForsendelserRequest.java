package no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import no.nav.dokdistadmin.administrerforsendelse.Forsendelse;

import java.util.List;

public record AvstemForsendelserRequest(
        @NotEmpty(message = "avstemtReferanse kan ikke være null eller en tom streng") String avstemtReferanse,
        @NotNull @Valid List<Forsendelse> forsendelser) {
}
