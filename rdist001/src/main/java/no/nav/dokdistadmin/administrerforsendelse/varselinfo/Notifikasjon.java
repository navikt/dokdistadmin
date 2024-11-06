package no.nav.dokdistadmin.administrerforsendelse.varselinfo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import no.nav.dokdistadmin.domain.VarslingKanalCode;

import java.time.LocalDateTime;

@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notifikasjon {

	@NotNull(message = "kanal kan ikke være null")
	VarslingKanalCode kanal;

	@NotBlank(message = "tekst må inneholde minst ett tegn")
	String tekst;

	@NotBlank(message = "kontaktInfo må innholde en epostadresse eller et telefonnummer")
	String kontaktInfo;

	String tittel;
	LocalDateTime varslingstidspunkt;
}
