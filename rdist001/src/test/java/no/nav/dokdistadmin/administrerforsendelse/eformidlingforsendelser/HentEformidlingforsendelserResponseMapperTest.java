package no.nav.dokdistadmin.administrerforsendelse.eformidlingforsendelser;

import no.nav.dokdistadmin.administrerforsendelse.TestUtils;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HentEformidlingforsendelserResponseMapperTest {

	private final HentEformidlingforsendelserResponseMapper mapper = new HentEformidlingforsendelserResponseMapper();

	@Test
	public void shouldMapDokumentInfoList() {
		var dokumentInfo = TestUtils.createDokumentInfo();
		dokumentInfo.setDistribusjonInfo(DistribusjonInfo.builder()
				.distribusjonKanal(DistribusjonKanalCode.TRYGDERETTEN)
				.build());

		HentEformidlingforsendelserResponse result = mapper.map(List.of(dokumentInfo, dokumentInfo));

		assertEquals(2, result.getForsendelser().size());

		assertThat(result.getForsendelser())
				.allSatisfy(forsendelse -> {
					assertEquals(dokumentInfo.getDokumentInfoId(), forsendelse.getForsendelseId());
					assertEquals(dokumentInfo.getDokumentStatus().name(), forsendelse.getForsendelseStatus());
					assertEquals(dokumentInfo.getDistribusjonInfo().getDistribusjonKanal().name(), forsendelse.getDistribusjonKanal());
					assertEquals(dokumentInfo.getKonversasjonId(), forsendelse.getKonversasjonId());
				});
	}

}
