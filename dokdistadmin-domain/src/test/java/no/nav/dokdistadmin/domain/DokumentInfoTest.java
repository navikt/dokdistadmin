package no.nav.dokdistadmin.domain;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Enhetstest for DokumentInfo
 * @Author Lars Aune
 */
public class DokumentInfoTest {
	@Test
	public void assignInitialValues() {
		DokumentInfo dokumentInfo = new DokumentInfo();
		dokumentInfo.assignInitialValues();
		assertThat(dokumentInfo.getDokumentStatus(), Matchers.is(DokumentStatusCode.OPPRETTET));
	}
}