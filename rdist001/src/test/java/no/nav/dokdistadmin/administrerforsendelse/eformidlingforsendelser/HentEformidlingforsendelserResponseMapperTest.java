package no.nav.dokdistadmin.administrerforsendelse.eformidlingforsendelser;

import org.junit.Test;

import java.util.List;

import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfoWithDistribusjonKanal;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfo;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.TRYGDERETTEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HentEformidlingforsendelserResponseMapperTest {

	private final HentEformidlingforsendelserResponseMapper mapper = new HentEformidlingforsendelserResponseMapper();

	@Test
	public void shouldMapDokumentInfoList() {
		var dokumentInfo = createDokumentInfo();
		dokumentInfo.setDistribusjonInfo(createDistribusjonInfoWithDistribusjonKanal(TRYGDERETTEN));

		HentEformidlingforsendelserResponse result = mapper.map(List.of(dokumentInfo, dokumentInfo));

		assertThat(result.getForsendelser())
				.hasSize(2)
				.allSatisfy(forsendelse -> {
					assertEquals(dokumentInfo.getDokumentInfoId(), forsendelse.getForsendelseId());
					assertEquals(dokumentInfo.getDokumentStatus().name(), forsendelse.getForsendelseStatus());
					assertEquals(dokumentInfo.getDistribusjonInfo().getDistribusjonKanal().name(), forsendelse.getDistribusjonKanal());
					assertEquals(dokumentInfo.getKonversasjonId(), forsendelse.getKonversasjonId());
				});
	}

}
