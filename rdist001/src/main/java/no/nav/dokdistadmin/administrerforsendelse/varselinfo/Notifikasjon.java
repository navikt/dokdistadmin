package no.nav.dokdistadmin.administrerforsendelse.varselinfo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import no.nav.dokdistadmin.domain.VarslingKanalCode;

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

	@NotNull(message = "varslingstidspunkt kan ikke være null")
	private LocalDateTime varslingstidspunkt;
}
