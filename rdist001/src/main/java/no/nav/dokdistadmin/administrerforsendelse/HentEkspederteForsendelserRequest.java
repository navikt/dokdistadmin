package no.nav.dokdistadmin.administrerforsendelse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HentEkspederteForsendelserRequest {

	@PositiveOrZero(message = "maksForsendelser må være et positivt tall")
	@NotNull(message = "maksForsendelser kan ikke være 'null'")
	private Integer maksForsendelser;
}