package no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Set;

@Value
@Builder
public class Varsel {
	Set<Epostvarsel> epostvarsel;
	Set<Smsvarsel> smsvarsel;

	@Value
	@Builder
	public static class Epostvarsel {
		String adresse;
		String tittel;
		String tekst;
		LocalDateTime tidspunkt;
	}

	@Value
	@Builder
	public static class Smsvarsel {
		String telefonnummer;
		String tekst;
		LocalDateTime tidspunkt;
	}
}
