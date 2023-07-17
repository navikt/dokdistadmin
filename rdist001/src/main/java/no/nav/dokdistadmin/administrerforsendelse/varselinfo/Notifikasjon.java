package no.nav.dokdistadmin.administrerforsendelse.varselinfo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import no.nav.dokdistadmin.domain.VarslingKanalCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notifikasjon {

	@NotNull(message = "kanal kan ikke være null")
	private VarslingKanalCode kanal;

	@NotBlank(message = "tekst må inneholde minst ett tegn")
	private String tekst;

	@NotBlank(message = "kontaktInfo må innholde en epostadresse eller et telefonnummer")
	private String kontaktInfo;

	private String tittel;

	@PastOrPresent
	private LocalDateTime varslingstidspunkt;
}
