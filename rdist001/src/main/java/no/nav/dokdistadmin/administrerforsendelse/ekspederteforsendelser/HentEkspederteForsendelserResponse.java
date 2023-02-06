package no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser;

import java.util.List;

public record HentEkspederteForsendelserResponse(
		List<EkspederteForsendelse> forsendelser
) {
}
