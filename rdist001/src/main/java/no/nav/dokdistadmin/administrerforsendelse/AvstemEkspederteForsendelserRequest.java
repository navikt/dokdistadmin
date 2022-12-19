package no.nav.dokdistadmin.administrerforsendelse;

import java.util.List;

public record AvstemEkspederteForsendelserRequest(
		List<Forsendelse> forsendelser
) {

	public record Forsendelse(
			Long forsendelseId
	) {
	}
}
