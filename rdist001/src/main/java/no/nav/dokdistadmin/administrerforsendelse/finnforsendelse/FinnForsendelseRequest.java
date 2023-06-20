package no.nav.dokdistadmin.administrerforsendelse.finnforsendelse;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class FinnForsendelseRequest {
	@NotNull(message = "Feltet oppslagsnoekkel kan ikke være null")
	private Oppslagsnoekkel oppslagsnoekkel;

	@NotBlank(message = "Feltet verdi må ha en verdi")
	private String verdi;
}
