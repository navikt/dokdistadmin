package no.nav.dokdistadmin.administrerforsendelse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Forsendelse {

	@Positive(message = "forsendelseId må være et positivt tall")
	private Long forsendelseId;
}
