package no.nav.dokdistadmin.administrerforsendelse;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser.OppdaterForsendelseRequest;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.VarselStatusCode;
import no.nav.dokdistadmin.exception.functional.DokumentStatusErAlleredeSattException;
import no.nav.dokdistadmin.exception.functional.ForsendelseIkkeFunnetException;
import no.nav.dokdistadmin.exception.functional.IkkeSammenfallendeStatusException;
import no.nav.dokdistadmin.exception.functional.UlovligStatusOvergangException;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser.StatusovergangValidator.isDigitalAdresseSatt;
import static no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser.StatusovergangValidator.isDistribusjonStatusEqualToDokumentStatus;
import static no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser.StatusovergangValidator.isDokumentStatusEqualToForsendelseStatus;
import static no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser.StatusovergangValidator.isLovligStatusovergang;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.SDP;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.valueOf;
import static no.nav.dokdistadmin.domain.VarselStatusCode.FERDIGSTILT;
import static no.nav.dokdistadmin.domain.VarselStatusCode.OPPRETTET;
import static no.nav.dokdistadmin.utils.EnumUtils.stringToEnum;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@Transactional(readOnly = true)
public class OppdaterForsendelseService {

	private final DokumentInfoRepository dokumentInfoRepository;
	private final DokumentDistribusjonRepository dokumentDistribusjonRepository;

	public OppdaterForsendelseService(DokumentInfoRepository dokumentInfoRepository,
									  DokumentDistribusjonRepository dokumentDistribusjonRepository) {
		this.dokumentInfoRepository = dokumentInfoRepository;
		this.dokumentDistribusjonRepository = dokumentDistribusjonRepository;
	}

	@Transactional
	public void oppdaterForsendelse(OppdaterForsendelseRequest oppdaterForsendelseRequest) {

		DokumentInfo dokumentInfo = dokumentInfoRepository.fetchDokumentInfo(oppdaterForsendelseRequest.getForsendelseId());

		if (isNull(dokumentInfo)) {
			throw new ForsendelseIkkeFunnetException(format("Forsendelse med forsendelseId=%s ikke funnet i dokdistDb",
					oppdaterForsendelseRequest.getForsendelseId()));
		}

		if (isNotBlank(oppdaterForsendelseRequest.getForsendelseStatus())) {
			validateForsendelseStatus(oppdaterForsendelseRequest.getForsendelseStatus());
			oppdaterDokumentAndDistribusjonStatus(dokumentInfo, oppdaterForsendelseRequest.getForsendelseStatus());
		}

		if (isNotBlank(oppdaterForsendelseRequest.getKonversasjonId())) {
			oppdaterKonversasjonId(dokumentInfo, oppdaterForsendelseRequest.getKonversasjonId());
		}

		if (nonNull(oppdaterForsendelseRequest.getVarselStatus())) {
			oppdaterVarselStatus(dokumentInfo, oppdaterForsendelseRequest.getVarselStatus());
		}

		if (SDP.equals(dokumentInfo.getDistribusjonInfo().getDistribusjonKanal())) {
			oppdaterDigitalDistribusjonAdresseFraDPI(dokumentInfo, oppdaterForsendelseRequest);
		}
	}

	private void oppdaterDokumentAndDistribusjonStatus(DokumentInfo dokumentInfo, String nyForsendelseStatus) {
		final String dokumentStatus = dokumentInfo.getDokumentStatus().name();
		if (!isDistribusjonStatusEqualToDokumentStatus(dokumentInfo)) {
			throw new IkkeSammenfallendeStatusException(format("Ikke sammenfallende statuser på forsendelse: distribusjonStatus er ikke lik dokumentStatus. distribusjonStatus=%s, dokumentStatus=%s",
					dokumentInfo.getDistribusjonInfo().getDistribusjonStatus(), dokumentStatus));
		} else if (isDokumentStatusEqualToForsendelseStatus(dokumentStatus, nyForsendelseStatus)) {
			throw new DokumentStatusErAlleredeSattException(format("DokumentStatus er allerede satt: Fikk forespørsel om å sette ny dokumentStatus=%s. Dokumentstatus på forsendelse er allerede dokumentStatus=%s",
					nyForsendelseStatus, dokumentStatus));
		} else if (isLovligStatusovergang(dokumentStatus, nyForsendelseStatus)) {
			setForsendelseStatus(dokumentInfo, nyForsendelseStatus);
			dokumentInfoRepository.updateDokumentStatus(dokumentInfo.getDokumentInfoId(), valueOf(nyForsendelseStatus), MDC.get(USER_ID));
			DistribusjonInfo distribusjonInfo = dokumentInfo.getDistribusjonInfo();
			dokumentDistribusjonRepository.updateDistribusjonStatus(distribusjonInfo.getDistribusjonInfoId(),
					DistribusjonStatusCode.valueOf(nyForsendelseStatus), MDC.get(USER_ID));

		} else {
			throw new UlovligStatusOvergangException(format("Ulovlig statusovergang: kan ikke sette ny dokumentStatus=%s når dokumentStatus=%s. Lovlige statusoverganger er " +
							"OPPRETTET -> KLAR_FOR_DIST, " +
							"KLAR_FOR_DIST -> OVERSENDT/EKSPEDERT, " +
							"OVERSENDT -> BEKREFTET/EKSPEDERT/FEILET, " +
							"BEKREFTET -> EKSPEDERT/FEILET",
					nyForsendelseStatus, dokumentStatus));
		}
	}

	private void oppdaterVarselStatus(DokumentInfo dokumentInfo, VarselStatusCode nyVarselStatus) {
		final VarselStatusCode opprinneligVarselStatus = dokumentInfo.getDistribusjonInfo().getVarselStatus();
		if (!isLovligVarselStatusOvergang(opprinneligVarselStatus, nyVarselStatus)) {
			throw new UlovligStatusOvergangException(format("Ulovlig varselstatusovergang: kan ikke sette ny varselStatus=%s for distribusjon når varselStatus=%s. Lovlige statusoverganger er " +
							"OPPRETTET -> FEILET, " +
							"OPPRETTET -> FERDIGSTILT",
					nyVarselStatus, opprinneligVarselStatus));
		}
		if (opprinneligVarselStatus == null || (!opprinneligVarselStatus.equals(nyVarselStatus))) {
			dokumentInfo.getDistribusjonInfo().setVarselStatus(nyVarselStatus);
			DistribusjonInfo distribusjonInfo = dokumentInfo.getDistribusjonInfo();
			dokumentDistribusjonRepository.updateDistribusjonInfoVarselStatus(distribusjonInfo.getDistribusjonInfoId(),
					nyVarselStatus, MDC.get(USER_ID));
		}
	}

	private void oppdaterDigitalDistribusjonAdresseFraDPI(DokumentInfo dokumentInfo, OppdaterForsendelseRequest oppdaterForsendelseRequest) {
		if (isDigitalAdresseSatt(oppdaterForsendelseRequest)) {
			dokumentInfo.setDigitalDistributorId(oppdaterForsendelseRequest.getDigitalLeverandoeradresse());
			dokumentInfo.setDigitalPostkasseAdresse(oppdaterForsendelseRequest.getDigitalPostkasseadresse());
			dokumentInfoRepository.updateDokumentDigitalDistribujonAdresse(oppdaterForsendelseRequest.getForsendelseId(),
					oppdaterForsendelseRequest.getDigitalPostkasseadresse(), oppdaterForsendelseRequest.getDigitalLeverandoeradresse(), MDC.get(USER_ID));
		}
	}

	public void oppdaterKonversasjonId(DokumentInfo dokumentInfo, String konversasjonId) {
		setKonversasjonId(dokumentInfo, konversasjonId);
		dokumentInfoRepository.updateDokumentKonversasjonsId(dokumentInfo.getDokumentInfoId(), konversasjonId, MDC.get(USER_ID));
	}

	private boolean isLovligVarselStatusOvergang(VarselStatusCode opprinneligVarselStatus, VarselStatusCode nyVarselStatus) {
		return opprinneligVarselStatus == null ||
				(opprinneligVarselStatus.equals(OPPRETTET) && (nyVarselStatus.equals(VarselStatusCode.FEILET) ||
						nyVarselStatus.equals(FERDIGSTILT)));
	}

	private void setForsendelseStatus(DokumentInfo dokumentInfo, String nyForsendelseStatus) {
		dokumentInfo.setDokumentStatus(valueOf(nyForsendelseStatus));
		dokumentInfo.getDistribusjonInfo().setDistribusjonStatus(DistribusjonStatusCode.valueOf(nyForsendelseStatus));
		if (EKSPEDERT.equals(valueOf(nyForsendelseStatus))) {
			dokumentInfo.setEkspedertDato(now());
		}
	}

	private void setKonversasjonId(DokumentInfo dokumentInfo, String konversasjonId) {
		dokumentInfo.setKonversasjonId(konversasjonId);
	}

	private void validateForsendelseStatus(String forsendelseStatus) {
		if (isNotBlank(forsendelseStatus)) {
			stringToEnum(DokumentStatusCode.class, forsendelseStatus.trim());
			stringToEnum(DistribusjonStatusCode.class, forsendelseStatus.trim());
		}
	}
}
