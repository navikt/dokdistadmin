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
	private List<Epostvarsel> epostvarsel;
	private List<Smsvarsel> smsvarsel;

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
