package no.nav.dokdistadmin.administrerforsendelse.eformidlingforsendelser;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HentEformidlingforsendelserResponse {

	private final List<Forsendelse> forsendelser;

	@Data
	@Builder
	public static class Forsendelse {
		private final Long forsendelseId;
		private final String forsendelseStatus;
		private final String distribusjonKanal;
		private final String konversasjonId;
		private final String opprettetDato;
	}
}
