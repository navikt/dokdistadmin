package no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dokdistadmin.administrerforsendelse.Forsendelse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvstemEkspederteForsendelserRequest {

	@Valid
	@NotEmpty(message = "forsendelser kan ikke være null eller en tom liste")
	private List<Forsendelse> forsendelser;

}
