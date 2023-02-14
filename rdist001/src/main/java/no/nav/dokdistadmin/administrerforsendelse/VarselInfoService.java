package no.nav.dokdistadmin.administrerforsendelse;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.administrerforsendelse.varselinfo.OppdaterVarselInfoRequest;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.VarselInfo;
import no.nav.dokdistadmin.exception.functional.OppdaterVarselInfoException;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import no.nav.dokdistadmin.repository.VarselInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.StreamSupport;

import static java.lang.String.format;
import static no.nav.dokdistadmin.administrerforsendelse.varselinfo.OppdaterVarselInfoRequestMapper.mapOppdaterVarselInfoRequest;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.DITTNAV;

@Slf4j
@Service
@Transactional(readOnly = true)
public class VarselInfoService {

	private static final String OPPDATERVARSELINFO_ERROR = "oppdaterVarselInfo kunne ikke oppdatere varselinfo på forsendelse med forsendelseId={}. Feilmelding={}";

	private final DokumentInfoRepository dokumentInfoRepository;
	private final VarselInfoRepository varselInfoRepository;

	public VarselInfoService(DokumentInfoRepository dokumentInfoRepository,
							 VarselInfoRepository varselInfoRepository) {
		this.dokumentInfoRepository = dokumentInfoRepository;
		this.varselInfoRepository = varselInfoRepository;
		}

	@Transactional
	public long oppdaterVarselInfo(OppdaterVarselInfoRequest oppdaterVarselInfoRequest) {

		DokumentInfo dokumentInfo = dokumentInfoRepository.findDokumentInfoByDokumentInfoId(oppdaterVarselInfoRequest.getForsendelseId());

		if (dokumentInfo == null) {
			log.warn(OPPDATERVARSELINFO_ERROR, oppdaterVarselInfoRequest.getForsendelseId(), "Forsendelse ikke funnet.");
			throw new OppdaterVarselInfoException(format("Forsendelse med forsendelseId=%s ikke funnet", oppdaterVarselInfoRequest.getForsendelseId()));
		}

		DistribusjonInfo distribusjonInfo = dokumentInfo.getDistribusjonInfo();

		if (!DITTNAV.equals(distribusjonInfo.getDistribusjonKanal())) {
			log.warn(OPPDATERVARSELINFO_ERROR, oppdaterVarselInfoRequest.getForsendelseId(), "Forsendelse har ikke forventet distribusjonskanal DITTNAV");
			throw new OppdaterVarselInfoException(format("Forsendelse med forsendelseId=%s har ikke forventet distribusjonskanal DITTNAV", oppdaterVarselInfoRequest.getForsendelseId()));
		}

		List<VarselInfo> varselInfoList = mapOppdaterVarselInfoRequest(oppdaterVarselInfoRequest, dokumentInfo);
		var oppdaterteVarselInfo = varselInfoRepository.saveAll(varselInfoList);


		return StreamSupport.stream(oppdaterteVarselInfo.spliterator(), false).count();
	}

}
