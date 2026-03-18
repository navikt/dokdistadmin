package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.config.AbstractRepositoryTest;
import no.nav.dokdistadmin.domain.VarselInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.nav.dokdistadmin.domain.VarslingKanalCode.EPOST;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.MOBILTELEFON;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VarselInfoRepositoryTest extends AbstractRepositoryTest {

	private static final String KONTAKTINFO_SMS = "98765432";
	private static final String KONTAKTINFO_EPOST = "mottaker@nav.no";
	private static final String VARSLINGSTEKST_SMS = "Dette er en melding";
	private static final String VARSLINGSTEKST_EPOST = "Tittel Brev til deg, Tekst Dette er en melding";

	@Test
	void shouldSaveVarselInfo() {
		var varsler = varselInfoRepository.persistAll(List.of(createSMSVarselInfo(), createEpostVarselInfo()));

		assertEquals(2, varsler.size());
	}

	private static VarselInfo createSMSVarselInfo() {
		return VarselInfo.builder()
				.varslingKanal(MOBILTELEFON)
				.mobiltelefonNummer(KONTAKTINFO_SMS)
				.varslingstekst(VARSLINGSTEKST_SMS)
				.build();
	}

	private static VarselInfo createEpostVarselInfo() {
		return VarselInfo.builder()
				.varslingKanal(EPOST)
				.epostAdresse(KONTAKTINFO_EPOST)
				.varslingstekst(VARSLINGSTEKST_EPOST)
				.build();
	}
}