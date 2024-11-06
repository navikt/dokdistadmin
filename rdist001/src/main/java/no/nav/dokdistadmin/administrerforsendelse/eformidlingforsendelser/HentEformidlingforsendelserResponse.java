package no.nav.dokdistadmin.administrerforsendelse.eformidlingforsendelser;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class HentEformidlingforsendelserResponse {

	List<Forsendelse> forsendelser;

	@Value
	@Builder
	public static class Forsendelse {
		Long forsendelseId;
		String forsendelseStatus;
		String distribusjonKanal;
		String konversasjonId;
		String opprettetDato;
	}
}
