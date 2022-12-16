package no.nav.dokdistadmin.domain;

import org.junit.jupiter.api.Test;

import static no.nav.dokdistadmin.domain.DokumentStatusCode.OPPRETTET;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DokumentInfoTest {

	@Test
	public void assignInitialValues() {
		DokumentInfo dokumentInfo = new DokumentInfo();
		dokumentInfo.assignInitialValues();

		assertEquals(OPPRETTET, dokumentInfo.getDokumentStatus());
	}
}