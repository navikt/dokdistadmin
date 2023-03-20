package no.nav.dokdistadmin.administrerforsendelse.forsendelser;

import lombok.Builder;
import lombok.Data;
import no.nav.dokdistadmin.domain.ArkivSystemCode;
import no.nav.dokdistadmin.domain.DistribusjonsTypeKode;
import no.nav.dokdistadmin.domain.DistribusjonstidspunktKode;
import no.nav.dokdistadmin.domain.FagomradeCode;
import no.nav.dokdistadmin.domain.ModusCode;

import java.util.List;

@Data
@Builder
public class HentForsendelseResponse {

	private final String bestillingsId;
	private final String originalBestillingsId;
	private final String konversasjonId;
	private final String bestillendeFagsystem;
	private final ModusCode modus;
	private final String forsendelseStatus;
	private final String distribusjonKanal;
	private final FagomradeCode tema;
	private final String forsendelseTittel;
	private final String batchId;
	private final String dokumentProdApp;
	private final Mottaker mottaker;
	private final ArkivInformasjon arkivInformasjon;
	private final Postadresse postadresse;
	private final List<Dokument> dokumenter;
	private final DistribusjonsTypeKode distribusjonstype;
	private final DistribusjonstidspunktKode distribusjonstidspunkt;
	private final String varselStatus;


	@Data
	@Builder
	public static class Mottaker {
		private final String mottakerId;
		private final String mottakerNavn;
		private final String mottakerType;
	}

	@Data
	@Builder
	public static class ArkivInformasjon {
		private ArkivSystemCode arkivSystem;
		private final String arkivId;
	}

	@Data
	@Builder
	public static class Postadresse {
		private final String adresselinje1;
		private final String adresselinje2;
		private final String adresselinje3;
		private final String postnummer;
		private final String poststed;
		private final String landkode;
	}

	@Data
	@Builder
	public static class Dokument {
		private final String tilknyttetSom;
		private final String dokumentObjektReferanse;
		private final String arkivDokumentInfoId;
		private final String dokumenttypeId;
	}
}
