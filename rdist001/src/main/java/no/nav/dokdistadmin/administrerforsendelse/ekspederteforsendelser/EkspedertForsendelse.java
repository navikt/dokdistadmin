package no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser;

import lombok.Builder;
import lombok.Value;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;

@Value
@Builder
public class EkspedertForsendelse {
	Long forsendelseId;
	String journalpostId;
	DistribusjonKanalCode distribusjonsKanal;
	String ekspedertDato;
	PostadresseTo postadresse;
	Digitalpostkasse digitalpostkasse;
	Varsel varsel;

	@Value
	@Builder
	public static class PostadresseTo {
		String adresselinje1;
		String adresselinje2;
		String adresselinje3;
		String postnummer;
		String poststed;
		String landkode;
	}

	@Value
	@Builder
	public static class Digitalpostkasse {
		String digitalpostkasseadresse;
		String digitalpostkasseleverandor;
	}
}
