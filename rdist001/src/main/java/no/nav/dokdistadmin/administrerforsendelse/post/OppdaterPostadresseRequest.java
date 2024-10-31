package no.nav.dokdistadmin.administrerforsendelse.post;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OppdaterPostadresseRequest {

	@NotNull(message = "forsendelseId kan ikke være null")
	@Positive(message = "forsendelseId må være et positivt tall")
	Long forsendelseId;

	String adresselinje1;
	String adresselinje2;
	String adresselinje3;
	String postnummer;
	String poststed;

	@NotNull(message = "landkode kan ikke være null")
	@Pattern(regexp = "^[a-zA-Z]{2}$", message = "landkode må bestå av nøyaktig to bokstaver")
	String landkode;
}
