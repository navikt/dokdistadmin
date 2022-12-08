package no.nav.dokdistadmin.domain;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Tests of DistribusjonInfoTest.
 *
 * @author Lars Aune
 */
public class DistribusjonInfoTest {
	@Test
	public void assignInitalValues() {
		DistribusjonInfo distribusjonInfo = new DistribusjonInfo();
		distribusjonInfo.addDokumentInfo(new DokumentInfo());
		distribusjonInfo.assignInitialValues();
		assertThat(distribusjonInfo.getDistribusjonDato(), is(notNullValue()));
		assertThat(distribusjonInfo.getDistribusjonStatus(), Matchers.is(DistribusjonStatusCode.OPPRETTET));
		assertThat(distribusjonInfo.getModus(), Matchers.is(ModusCode.P));
		assertThat(distribusjonInfo.getDokumentInfos(), hasSize(1));
		assertThat(distribusjonInfo.getDokumentInfos().iterator().next().getDokumentStatus(), Matchers.is(DokumentStatusCode.OPPRETTET));
	}
}