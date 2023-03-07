package no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser;

import lombok.Builder;
import lombok.Data;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;

@Data
@Builder
public class EkspedertForsendelse {
	private final Long forsendelseId;
	private final String journalpostId;
	private final DistribusjonKanalCode distribusjonsKanal;
	private final String ekspedertDato;
	private final PostadresseTo postadresse;
	private final Digitalpostkasse digitalpostkasse;
	private final Varsel varsel;

	@Data
	@Builder
	public static class PostadresseTo {
		private final String adresselinje1;
		private final String adresselinje2;
		private final String adresselinje3;
		private final String postnummer;
		private final String poststed;
		private final String landkode;
	}

	@Data
	@Builder
	public static class Digitalpostkasse {
		private final String digitalpostkasseadresse;
		private final String digitalpostkasseleverandor;
	}
}
