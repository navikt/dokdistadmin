package no.nav.dokdistadmin.administrerforsendelse.varselinfo;

import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.VarselInfo;

import java.util.List;

import static no.nav.dokdistadmin.domain.VarslingKanalCode.EPOST;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.MOBILTELEFON;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class OppdaterVarselInfoRequestMapper {

	public static List<VarselInfo> mapOppdaterVarselInfoRequest(OppdaterVarselInfoRequest oppdaterVarselInfoRequest, DokumentInfo dokumentInfo) {

		return oppdaterVarselInfoRequest.getNotifikasjoner().stream()
				.map(notifikasjon -> VarselInfo.builder()
						.varslingKanal(notifikasjon.getKanal())
						.dokumentInfo(dokumentInfo)
						.varslingstittel(isNotBlank(notifikasjon.getTittel()) ? notifikasjon.getTittel() : null)
						.varslingstekst(notifikasjon.getTekst())
						.epostAdresse(EPOST.equals(notifikasjon.getKanal()) ? notifikasjon.getKontaktInfo() : null)
						.mobiltelefonNummer(MOBILTELEFON.equals(notifikasjon.getKanal()) ? notifikasjon.getKontaktInfo() : null)
						.varslingstidspunkt(notifikasjon.getVarslingstidspunkt())
						.build())
				.toList();
	}
}
