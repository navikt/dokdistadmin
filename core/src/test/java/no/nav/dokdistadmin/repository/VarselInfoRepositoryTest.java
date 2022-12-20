package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.config.AbstractRepositoryTest;
import no.nav.dokdistadmin.domain.VarselInfo;
import no.nav.dokdistadmin.domain.VarslingKanalCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class VarselInfoRepositoryTest extends AbstractRepositoryTest {

	private static final String MOBILTELEFONNUMMER = "95123456";
	private static final String MELDING = "Dette er en melding";
	@Test
	void shouldSaveVarselInfo() {
		var varsel = varselInfoRepository.save(createSMSVarselInfo());

		assertNotNull(varsel.getVarselInfoId());
	}

	private static VarselInfo createSMSVarselInfo() {
		return VarselInfo.builder()
				.mobiltelefonNummer(MOBILTELEFONNUMMER)
				.varslingKanal(VarslingKanalCode.MOBILTELEFON)
				.varslingstekst(MELDING)
				.build();
	}

}