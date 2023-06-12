package no.nav.dokdistadmin.administrerforsendelse;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse.FeilregistrerForsendelseRequest;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.Feilkvittering;
import no.nav.dokdistadmin.exception.functional.ForsendelseIkkeFunnetException;
import no.nav.dokdistadmin.exception.functional.ValideringFeiletException;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;
import static no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse.FeilregistrerForsendelseMapper.toFeilkvittering;
import static no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse.FeilregistrerForsendelseValidator.validerDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse.FeilregistrerForsendelseValidator.validerDokumentInfo;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


@Slf4j
@Service
@Transactional(readOnly = true)
public class FeilregistrerForsendelseService {

	public static final String FEILREGISTRER_FORSENDELSE_FEILMELDING = "rdist001 kunne ikke feilregistrere forsendelse. Feilmelding=%s";

	private final DokumentDistribusjonRepository dokumentDistribusjonRepository;
	private final DokumentInfoRepository dokumentInfoRepository;

	public FeilregistrerForsendelseService(DokumentDistribusjonRepository dokumentDistribusjonRepository,
										   DokumentInfoRepository dokumentInfoRepository) {
		this.dokumentDistribusjonRepository = dokumentDistribusjonRepository;
		this.dokumentInfoRepository = dokumentInfoRepository;
	}

	@Transactional
	public void feilregistrerForsendelse(FeilregistrerForsendelseRequest feilregistrerForsendelseRequest) {

		Long forsendelseId = feilregistrerForsendelseRequest.getForsendelseId();

		DokumentInfo dokumentInfo = dokumentInfoRepository.findDokumentInfoByDokumentInfoId(forsendelseId);

		if (dokumentInfo == null) {
			throw new ForsendelseIkkeFunnetException(format(FEILREGISTRER_FORSENDELSE_FEILMELDING, format("Fant ikke forsendelse med forsendelseId=%s", forsendelseId)));
		}

		DistribusjonInfo distribusjonInfo = dokumentInfo.getDistribusjonInfo();

		validerDistribusjonInfo(distribusjonInfo);
		validerDokumentInfo(dokumentInfo);

		Feilkvittering feilkvittering = toFeilkvittering(feilregistrerForsendelseRequest, dokumentInfo);

		oppdaterDokumentInfo(dokumentInfo, feilkvittering);
		oppdaterDistribusjonInfo(distribusjonInfo, feilregistrerForsendelseRequest.getResendingDistribusjonId());

		dokumentDistribusjonRepository.save(distribusjonInfo);
	}

	private void oppdaterDokumentInfo(DokumentInfo dokumentInfo, Feilkvittering feilkvittering) {
		dokumentInfo.setDokumentStatus(DokumentStatusCode.FEILET);
		dokumentInfo.addFeilkvittering(feilkvittering);
	}

	private void oppdaterDistribusjonInfo(DistribusjonInfo distribusjonInfo, String resendingDistribusjonId) {

		distribusjonInfo.setDistribusjonStatus(DistribusjonStatusCode.FEILET);

		if (isNotBlank(resendingDistribusjonId)) {
			distribusjonInfo.setResendingDistribusjonId(resendingDistribusjonId);
		}
	}

}
