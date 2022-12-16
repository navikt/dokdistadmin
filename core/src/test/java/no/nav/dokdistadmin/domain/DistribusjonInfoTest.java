package no.nav.dokdistadmin.domain;

import org.junit.jupiter.api.Test;

import static no.nav.dokdistadmin.domain.ModusCode.P;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DistribusjonInfoTest {

	@Test
	public void assignInitalValues() {
		DistribusjonInfo distribusjonInfo = new DistribusjonInfo();
		distribusjonInfo.addDokumentInfo(new DokumentInfo());
		distribusjonInfo.assignInitialValues();

		assertNotNull(distribusjonInfo.getDistribusjonDato());
		assertEquals(DistribusjonStatusCode.OPPRETTET, distribusjonInfo.getDistribusjonStatus());
		assertEquals(P, distribusjonInfo.getModus());
		assertEquals(1, distribusjonInfo.getDokumentInfos().size());
		assertEquals(DokumentStatusCode.OPPRETTET, distribusjonInfo.getDokumentInfos().iterator().next().getDokumentStatus());
	}
}