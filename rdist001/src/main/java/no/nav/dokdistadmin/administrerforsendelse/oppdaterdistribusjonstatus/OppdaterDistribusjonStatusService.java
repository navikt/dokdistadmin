package no.nav.dokdistadmin.administrerforsendelse.oppdaterdistribusjonstatus;

import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.exception.functional.DistribusjonIkkeFunnetException;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static no.nav.dokdistadmin.utils.EnumUtils.validateEnum;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;

@Service
@Transactional(readOnly = true)
public class OppdaterDistribusjonStatusService {

	private final DokumentDistribusjonRepository dokumentDistribusjonRepository;
	private final DokumentInfoRepository dokumentInfoRepository;

	public OppdaterDistribusjonStatusService(
			DokumentDistribusjonRepository dokumentDistribusjonRepository,
			DokumentInfoRepository dokumentInfoRepository) {
		this.dokumentDistribusjonRepository = dokumentDistribusjonRepository;
		this.dokumentInfoRepository = dokumentInfoRepository;
	}

	@Transactional
	public void oppdaterDistribusjonStatus(OppdaterDistribusjonStatusRequest request) {
		DistribusjonInfo distribusjon = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(request.distribusjonId());

		if (distribusjon == null) {
			throw new DistribusjonIkkeFunnetException(
					"Distribusjon med distribusjonId=%s ikke funnet i dokdistDb".formatted(request.distribusjonId()));
		}

		validateEnum(DistribusjonStatusCode.class, request.distribusjonstatus());
		validateEnum(DokumentStatusCode.class, request.dokumentstatus());

		MDC.put(USER_ID, request.kilde());
		distribusjon.setDistribusjonStatus(DistribusjonStatusCode.valueOf(request.distribusjonstatus()));

		dokumentInfoRepository.updateStatusForAllDokumentInfosRelatedTo(
				distribusjon,
				DokumentStatusCode.valueOf(request.dokumentstatus()),
				request.kilde());
	}
}
