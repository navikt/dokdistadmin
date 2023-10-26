package no.nav.dokdistadmin.administrerforsendelse.varselinfo;

import java.util.List;

import static java.time.LocalDateTime.now;

public class VarselInfoValidator {

	public static final int SLINGRINGSMONN_FOR_VARSLINGSTIDSPUNKT = 5;

	public static boolean harUgyldigVarslingstidspunkt(List<Notifikasjon> notifikasjoner) {
		return notifikasjoner.stream()
				.anyMatch(notifikasjon -> notifikasjon.getVarslingstidspunkt().isAfter(now().plusSeconds(SLINGRINGSMONN_FOR_VARSLINGSTIDSPUNKT)));
	}

}
