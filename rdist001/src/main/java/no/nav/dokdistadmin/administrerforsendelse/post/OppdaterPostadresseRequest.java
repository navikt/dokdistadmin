package no.nav.dokdistadmin.administrerforsendelse.post;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OppdaterPostadresseRequest {
	private Long forsendelseId;
	private String adresselinje1;
	private String adresselinje2;
	private String adresselinje3;
	private String postnummer;
	private String poststed;
	private String landkode;
}
