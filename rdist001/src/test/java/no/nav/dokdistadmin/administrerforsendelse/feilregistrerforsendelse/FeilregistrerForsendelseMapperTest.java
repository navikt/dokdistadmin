package no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse;


import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.Feilkvittering;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.DETALJER;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.PART;
import static no.nav.dokdistadmin.domain.FeilTypeCode.MELDINGSFEIL;
import static org.assertj.core.api.Assertions.assertThat;

class FeilregistrerForsendelseMapperTest {

	@Test
	void shouldMapToFeilkvittering() {
		FeilregistrerForsendelseRequest feilregistrerForsendelse = FeilregistrerForsendelseRequest.builder()
				.tidspunkt(LocalDateTime.now())
				.detaljer(DETALJER)
				.part(PART)
				.feilTypeCode(MELDINGSFEIL)
				.build();

		DokumentInfo dokumentInfo = new DokumentInfo();

		Feilkvittering feilkvittering = FeilregistrerForsendelseMapper.toFeilkvittering(feilregistrerForsendelse, dokumentInfo);

		assertThat(feilkvittering.getDokumentInfo()).isEqualTo(dokumentInfo);
		assertThat(feilkvittering.getFeiletTidspunkt()).isEqualTo(feilregistrerForsendelse.getTidspunkt());
		assertThat(feilkvittering.getDetaljer()).isEqualTo(feilregistrerForsendelse.getDetaljer());
		assertThat(feilkvittering.getFeilpart()).isEqualTo(feilregistrerForsendelse.getPart());
		assertThat(feilkvittering.getFeiltype()).isEqualTo(feilregistrerForsendelse.getFeilTypeCode());
	}

}
