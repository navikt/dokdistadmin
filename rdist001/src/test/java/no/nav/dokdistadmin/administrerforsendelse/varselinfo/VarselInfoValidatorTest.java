package no.nav.dokdistadmin.administrerforsendelse.varselinfo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static java.time.LocalDateTime.now;
import static no.nav.dokdistadmin.administrerforsendelse.varselinfo.VarselInfoValidator.SLINGRINGSMONN_FOR_VARSLINGSTIDSPUNKT;
import static no.nav.dokdistadmin.administrerforsendelse.varselinfo.VarselInfoValidator.harUgyldigVarslingstidspunkt;
import static org.assertj.core.api.Assertions.assertThat;

class VarselInfoValidatorTest {

	@ParameterizedTest
	@CsvSource(value = {
			"-1, false",
			"1, true"
	})
	void skalValidereVarslingstidspunkt(int seconds, boolean valid) {
		var notifikasjon = Notifikasjon.builder()
				.varslingstidspunkt(now().plusSeconds(SLINGRINGSMONN_FOR_VARSLINGSTIDSPUNKT + seconds))
				.build();

		var result = harUgyldigVarslingstidspunkt(List.of(notifikasjon));

		assertThat(result).isEqualTo(valid);
	}
}