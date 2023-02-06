package no.nav.dokdistadmin.administrerforsendelse.eformidlingforsendelser;

import no.nav.dokdistadmin.administrerforsendelse.eformidlingforsendelser.HentEformidlingforsendelserResponse.Forsendelse;
import no.nav.dokdistadmin.domain.DokumentInfo;

import java.util.List;

public class HentEformidlingforsendelserResponseMapper {

	public HentEformidlingforsendelserResponse map(List<DokumentInfo> dokumentInfoList) {
		return HentEformidlingforsendelserResponse.builder()
				.forsendelser(getForsendelser(dokumentInfoList))
				.build();
	}

	private List<Forsendelse> getForsendelser(List<DokumentInfo> dokumentInfoList) {

		return dokumentInfoList.stream()
					   .map(dokumentInfo -> Forsendelse.builder()
							   .forsendelseId(dokumentInfo.getDokumentInfoId())
							   .forsendelseStatus(dokumentInfo.getDokumentStatus().name())
							   .distribusjonKanal(dokumentInfo.getDistribusjonInfo().getDistribusjonKanal().name())
							   .konversasjonId(dokumentInfo.getKonversasjonId())
							   .build())
					   .toList();
	}
}
