package no.nav.dokdistadmin.administrerforsendelse.post;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Data
@Builder
public class OppdaterPostadresseRequest {

	@NotNull(message = "forsendelseId kan ikke være null")
	@Positive(message = "forsendelseId må være et positivt tall")
	private Long forsendelseId;

	private String adresselinje1;
	private String adresselinje2;
	private String adresselinje3;
	private String postnummer;
	private String poststed;

	@NotNull(message = "landkode kan ikke være null")
	@Pattern(regexp = "^[a-zA-Z]{2}$", message = "landkode må bestå av nøyaktig to bokstaver")
	private String landkode;
}
