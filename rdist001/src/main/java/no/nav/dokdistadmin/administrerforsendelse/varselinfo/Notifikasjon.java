package no.nav.dokdistadmin.administrerforsendelse.varselinfo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import no.nav.dokdistadmin.domain.VarslingKanalCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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

}
