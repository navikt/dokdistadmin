package no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OppdaterPostAdresse {
	private Long forsendelseId;
	private String adresselinje1;
	private String adresselinje2;
	private String adresselinje3;
	private String postnummer;
	private String poststed;
	private String landkode;
}
