package no.nav.dokdistadmin.administrerforsendelse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Postadresse {
	String adresselinje1;
	String adresselinje2;
	String adresselinje3;
	String postnummer;
	String poststed;
	String landkode;
}
