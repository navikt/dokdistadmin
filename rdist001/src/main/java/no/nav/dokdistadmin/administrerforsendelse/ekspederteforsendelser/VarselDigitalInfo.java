package no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VarselDigitalInfo {
	private VarselInfoTo varseltekst;
	private VarselInfoTo digitalkontaktInfo;

	@Data
	@Builder
	public static class VarselInfoTo {
		private String epost;
		private String sms;
	}
}
