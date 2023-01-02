package no.nav.dokdistadmin.administrerforsendelse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvstemEkspederteForsendelserRequest {

	@Valid
	@NotEmpty(message = "forsendelser kan ikke være null eller en tom liste")
	private List<Forsendelse> forsendelser;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Forsendelse {

		@Positive(message = "forsendelseId må være et positivt tall")
		private Long forsendelseId;
	}

}
