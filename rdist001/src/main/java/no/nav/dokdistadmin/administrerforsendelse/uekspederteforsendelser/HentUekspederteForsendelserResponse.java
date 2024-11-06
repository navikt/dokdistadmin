package no.nav.dokdistadmin.administrerforsendelse.uekspederteforsendelser;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class HentUekspederteForsendelserResponse {

	List<UekspedertForsendelse> uekspederteForsendelser;

	@Value
	@Builder
	public static class UekspedertForsendelse {
		String distribusjonId;
		List<DokumentInfo> dokumenter;
		String distribusjonKanal;
		String distribusjonStatus;
		String opprettetDato;
		String distribusjonDato;
	}

	@Value
	@Builder
	public static class DokumentInfo {
		String forsendelseId;
		String dokumentId;
		String dokumentStatus;
		String konversasjonId;
		String bestillendeFagsystem;
		String fagomradeCode;
		String journalpostId;
		String brevProduksjonApplikasjon;
	}

}
