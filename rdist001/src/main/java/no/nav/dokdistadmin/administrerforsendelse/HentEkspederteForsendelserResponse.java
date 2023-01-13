package no.nav.dokdistadmin.administrerforsendelse;

import java.util.List;

public record HentEkspederteForsendelserResponse(
		List<EkspederteForsendelse> forsendelser
) {
}
