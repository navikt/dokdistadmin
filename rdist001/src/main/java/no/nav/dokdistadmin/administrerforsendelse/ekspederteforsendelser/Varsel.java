package no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Varsel {
	private List<EpostVarsel> epostVarsel;
	private List<SmsVarsel> smsVarsel;

	@Data
	@Builder
	public static class EpostVarsel {
		private String adresse;
		private String tittel;
		private String tekst;
		private LocalDateTime varslingstidspunkt;
	}

	@Data
	@Builder
	public static class SmsVarsel {
		private String telefonnummer;
		private String tekst;
		private LocalDateTime varslingstidspunkt;
	}
}
