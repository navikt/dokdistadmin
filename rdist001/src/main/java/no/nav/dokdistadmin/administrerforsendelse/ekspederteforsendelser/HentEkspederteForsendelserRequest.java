package no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record HentEkspederteForsendelserRequest(
		@PositiveOrZero(message = "maksForsendelser må være et positivt tall")
		@NotNull(message = "maksForsendelser kan ikke være 'null'")
		Integer maksForsendelser) {

}