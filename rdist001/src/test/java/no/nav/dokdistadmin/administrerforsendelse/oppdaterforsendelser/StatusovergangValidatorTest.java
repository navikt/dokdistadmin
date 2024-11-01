package no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class StatusovergangValidatorTest {

	@ParameterizedTest
	@CsvSource({
			"OPPRETTET, KLAR_FOR_DIST",
			"KLAR_FOR_DIST, OVERSENDT",
			"KLAR_FOR_DIST, EKSPEDERT",
			"OVERSENDT, BEKREFTET",
			"OVERSENDT, EKSPEDERT",
			"OVERSENDT, FEILET",
			"BEKREFTET, EKSPEDERT",
			"BEKREFTET, FEILET"
	})
	void skalValidereLovligeStatusoverganger(String oldDokumentStatus, String nyForsendelseStatus) {
		var result = StatusovergangValidator.isLovligDokumentstatusovergang(oldDokumentStatus, nyForsendelseStatus);
		assertThat(result).isTrue();
	}

	@ParameterizedTest
	@CsvSource({
			"OPPRETTET, BEKREFTET",
			"OPPRETTET, EKSPEDERT",
			"OPPRETTET, FEILET",
			"OPPRETTET, RETURPOSTBEHANDLET",
			"OPPRETTET, OPPRETTET",
			"KLAR_FOR_DIST, BEKREFTET",
			"KLAR_FOR_DIST, FEILET",
			"KLAR_FOR_DIST, RETURPOSTBEHANDLET",
			"KLAR_FOR_DIST, KLAR_FOR_DIST",
			"OVERSENDT, KLAR_FOR_DIST",
			"OVERSENDT, OPPRETTET",
			"OVERSENDT, RETURPOSTBEHANDLET",
			"OVERSENDT, OVERSENDT",
			"BEKREFTET, KLAR_FOR_DIST",
			"BEKREFTET, OPPRETTET",
			"BEKREFTET, RETURPOSTBEHANDLET",
			"BEKREFTET, BEKREFTET",
			"EKSPEDERT, OPPRETTET",
			"EKSPEDERT, KLAR_FOR_DIST",
			"EKSPEDERT, OVERSENDT",
			"EKSPEDERT, BEKREFTET",
			"EKSPEDERT, RETURPOSTBEHANDLET",
			"EKSPEDERT, EKSPEDERT",
			"FEILET, OPPRETTET",
			"FEILET, KLAR_FOR_DIST",
			"FEILET, OVERSENDT",
			"FEILET, BEKREFTET",
			"FEILET, RETURPOSTBEHANDLET",
			"FEILET, EKSPEDERT",
			"FEILET, FEILET",
			"RETURPOSTBEHANDLET, OPPRETTET",
			"RETURPOSTBEHANDLET, KLAR_FOR_DIST",
			"RETURPOSTBEHANDLET, OVERSENDT",
			"RETURPOSTBEHANDLET, BEKREFTET",
			"RETURPOSTBEHANDLET, EKSPEDERT",
			"RETURPOSTBEHANDLET, FEILET",
			"RETURPOSTBEHANDLET, RETURPOSTBEHANDLET"
	})
	void skalValidereUlovligeStatusoverganger(String oldDokumentStatus, String nyForsendelseStatus) {
		var result = StatusovergangValidator.isLovligDokumentstatusovergang(oldDokumentStatus, nyForsendelseStatus);
		assertThat(result).isFalse();
	}
}