package no.nav.dokdistadmin.administrerforsendelse.varselinfo;

import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.VarselInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.EPOSTADDRESSE;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.TELEFONNUMMER;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.VARSELTEKST;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.VARSELTITTEL;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.EPOST;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.MOBILTELEFON;
import static no.nav.dokdistadmin.repository.TestUtils.createDokumentInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

class OppdaterVarselInfoRequestMapperTest {

	@Test
	void shouldMapOppdaterVarselInfoRequestSms() {
		var request = createOppdateVarselInfoRequest();
		DokumentInfo dokumentInfo = createDokumentInfo();

		List<VarselInfo> varselInfoList = OppdaterVarselInfoRequestMapper.mapOppdaterVarselInfoRequest(request, dokumentInfo);

		assertThat(varselInfoList).anySatisfy(varsel -> {
			assertThat(varsel.getVarslingKanal()).isEqualTo(MOBILTELEFON);
			assertThat(varsel.getVarslingstekst()).isEqualTo(VARSELTEKST);
			assertThat(varsel.getMobiltelefonNummer()).isEqualTo(TELEFONNUMMER);
			assertNull(varsel.getEpostAdresse());
			assertThat(varsel.getDokumentInfo()).isEqualTo(dokumentInfo);
		});

		assertThat(varselInfoList).anySatisfy(varsel -> {
			assertThat(varsel.getVarslingKanal()).isEqualTo(EPOST);
			assertThat(varsel.getVarslingstekst()).isEqualTo(VARSELTEKST);
			assertThat(varsel.getEpostAdresse()).isEqualTo(EPOSTADDRESSE);
			assertNull(varsel.getMobiltelefonNummer());
			assertThat(varsel.getDokumentInfo()).isEqualTo(dokumentInfo);
			assertThat(varsel.getVarslingstittel()).isEqualTo(VARSELTITTEL);
		});
	}

	private OppdaterVarselInfoRequest createOppdateVarselInfoRequest() {
		return OppdaterVarselInfoRequest.builder()
				.forsendelseId(1234L)
				.notifikasjoner(
						List.of(
								Notifikasjon.builder()
										.kanal(MOBILTELEFON)
										.tekst(VARSELTEKST)
										.kontaktInfo(TELEFONNUMMER)
										.varslingstidspunkt(null)
										.build(),
								Notifikasjon.builder()
										.kanal(EPOST)
										.tekst(VARSELTEKST)
										.kontaktInfo(EPOSTADDRESSE)
										.tittel(VARSELTITTEL)
										.varslingstidspunkt(null)
										.build()))
				.build();
	}
}