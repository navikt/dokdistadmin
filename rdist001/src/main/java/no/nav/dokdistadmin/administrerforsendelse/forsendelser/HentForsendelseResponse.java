package no.nav.dokdistadmin.administrerforsendelse.forsendelser;

import lombok.Builder;
import lombok.Value;
import no.nav.dokdistadmin.domain.ArkivSystemCode;
import no.nav.dokdistadmin.domain.DistribusjonsTypeKode;
import no.nav.dokdistadmin.domain.DistribusjonstidspunktKode;
import no.nav.dokdistadmin.domain.FagomradeCode;
import no.nav.dokdistadmin.domain.ModusCode;

import java.util.List;

@Value
@Builder
public class HentForsendelseResponse {

	String bestillingsId;
	String originalBestillingsId;
	String konversasjonId;
	String bestillendeFagsystem;
	ModusCode modus;
	String forsendelseStatus;
	String distribusjonKanal;
	FagomradeCode tema;
	String forsendelseTittel;
	String batchId;
	String dokumentProdApp;
	Mottaker mottaker;
	ArkivInformasjon arkivInformasjon;
	Postadresse postadresse;
	List<Dokument> dokumenter;
	DistribusjonsTypeKode distribusjonstype;
	DistribusjonstidspunktKode distribusjonstidspunkt;
	String varselStatus;


	@Value
	@Builder
	public static class Mottaker {
		String mottakerId;
		String mottakerNavn;
		String mottakerType;
	}

	@Value
	@Builder
	public static class ArkivInformasjon {
		ArkivSystemCode arkivSystem;
		String arkivId;
	}

	@Value
	@Builder
	public static class Postadresse {
		String adresselinje1;
		String adresselinje2;
		String adresselinje3;
		String postnummer;
		String poststed;
		String landkode;
	}

	@Value
	@Builder
	public static class Dokument {
		String tilknyttetSom;
		String dokumentObjektReferanse;
		String arkivDokumentInfoId;
		String dokumenttypeId;
	}
}
