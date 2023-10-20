package no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Varsel {
	private Set<Epostvarsel> epostvarsel;
	private Set<Smsvarsel> smsvarsel;

	@Data
	@Builder
	public static class Epostvarsel {
		private String adresse;
		private String tittel;
		private String tekst;
		private LocalDateTime tidspunkt;
	}

	@Data
	@Builder
	public static class Smsvarsel {
		private String telefonnummer;
		private String tekst;
		private LocalDateTime tidspunkt;
	}
}
