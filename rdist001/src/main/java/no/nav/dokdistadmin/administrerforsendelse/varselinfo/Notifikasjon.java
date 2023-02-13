package no.nav.dokdistadmin.administrerforsendelse.varselinfo;


import lombok.Builder;
import lombok.Data;
import no.nav.dokdistadmin.domain.VarslingKanalCode;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Notifikasjon {

	@NotBlank(message = "kanal må være en gyldig varslingKanalCode")
	private VarslingKanalCode kanal;

	@NotBlank(message = "tekst må inneholde mist ett tegn")
	private String tekst;

	@NotBlank(message = "kontaktInfo må innholde en epostadresse eller et telefonnummer")
	private String kontaktInfo;

	private String tittel;

}
