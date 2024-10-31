package no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import no.nav.dokdistadmin.administrerforsendelse.Forsendelse;

import java.util.List;

public record AvstemEkspederteForsendelserRequest(
		@Valid @NotEmpty(message = "forsendelser kan ikke være null eller en tom liste") List<Forsendelse> forsendelser) {

}
