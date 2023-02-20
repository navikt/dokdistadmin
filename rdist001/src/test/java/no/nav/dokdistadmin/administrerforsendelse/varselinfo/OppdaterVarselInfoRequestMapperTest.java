package no.nav.dokdistadmin.administrerforsendelse.varselinfo;

import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.VarselInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.nav.dokdistadmin.domain.VarslingKanalCode.EPOST;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.MOBILTELEFON;
import static no.nav.dokdistadmin.repository.TestUtils.createDokumentInfo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OppdaterVarselInfoRequestMapperTest {

	private static final String VARSLINGSTEKST = "Du har fått et brev";
	private static final String KONTAKTINFO_SMS = "98765432";
	private static final String KONTAKTINFO_EPOST = "mottaker@nav.no";
	private static final String VARSLINGSTITTEL = "Brev til deg";

	@Test
	void shouldMapOppdaterVarselInfoRequestSms() {
		var request = createOppdateVarselInfoRequest();
		DokumentInfo dokumentInfo = createDokumentInfo();

		List<VarselInfo> varselInfoList = OppdaterVarselInfoRequestMapper.mapOppdaterVarselInfoRequest(request, dokumentInfo);
		var smsVarsel = varselInfoList.stream().filter(varsel -> MOBILTELEFON.equals(varsel.getVarslingKanal())).findFirst();
		var epostVarsel = varselInfoList.stream().filter(varsel -> EPOST.equals(varsel.getVarslingKanal())).findFirst();

		assertTrue(smsVarsel.isPresent());
		assertTrue(epostVarsel.isPresent());

		assertEquals(VARSLINGSTEKST, smsVarsel.get().getVarslingstekst());
		assertEquals(KONTAKTINFO_SMS, smsVarsel.get().getMobiltelefonNummer());
		assertNull(smsVarsel.get().getEpostAdresse());
		assertEquals(dokumentInfo, smsVarsel.get().getDokumentInfo());

		assertEquals(String.format("Tittel %s, Tekst %s", VARSLINGSTITTEL, VARSLINGSTEKST), epostVarsel.get().getVarslingstekst());
		assertEquals(KONTAKTINFO_EPOST, epostVarsel.get().getEpostAdresse());
		assertNull(epostVarsel.get().getMobiltelefonNummer());
		assertEquals(dokumentInfo, epostVarsel.get().getDokumentInfo());
	}

	private OppdaterVarselInfoRequest createOppdateVarselInfoRequest() {
		return OppdaterVarselInfoRequest.builder()
				.forsendelseId(1234L)
				.notifikasjoner(
						List.of(
								Notifikasjon.builder()
										.kanal(MOBILTELEFON)
										.tekst(VARSLINGSTEKST)
										.kontaktInfo(KONTAKTINFO_SMS)
										.build(),
								Notifikasjon.builder()
										.kanal(EPOST)
										.tekst(VARSLINGSTEKST)
										.kontaktInfo(KONTAKTINFO_EPOST)
										.tittel(VARSLINGSTITTEL)
										.build()))
				.build();
	}
}