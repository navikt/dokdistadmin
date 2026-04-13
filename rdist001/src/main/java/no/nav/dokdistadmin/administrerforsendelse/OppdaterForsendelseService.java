package no.nav.dokdistadmin.administrerforsendelse;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser.OppdaterForsendelseRequest;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.VarselStatusCode;
import no.nav.dokdistadmin.exception.functional.ForsendelseIkkeFunnetException;
import no.nav.dokdistadmin.exception.functional.IkkeSammenfallendeStatusException;
import no.nav.dokdistadmin.exception.functional.StatusErAlleredeSattException;
import no.nav.dokdistadmin.exception.functional.UlovligStatusOvergangException;
import no.nav.dokdistadmin.exception.functional.ValideringFeiletException;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalDateTime.now;
import static no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser.StatusovergangValidator.isLovligDokumentstatusOvergang;
import static no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser.StatusovergangValidator.isLovligVarselstatusOvergang;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.SDP;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.valueOf;
import static no.nav.dokdistadmin.utils.EnumUtils.validateEnum;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@Transactional(readOnly = true)
public class OppdaterForsendelseService {

	private final DokumentInfoRepository dokumentInfoRepository;

	public OppdaterForsendelseService(DokumentInfoRepository dokumentInfoRepository) {
		this.dokumentInfoRepository = dokumentInfoRepository;
	}

	@Transactional
	public void oppdaterForsendelse(OppdaterForsendelseRequest oppdaterForsendelseRequest) {

		DokumentInfo dokumentInfo = dokumentInfoRepository.fetchDokumentInfo(oppdaterForsendelseRequest.getForsendelseId());

		if (dokumentInfo == null) {
			throw new ForsendelseIkkeFunnetException(format("Forsendelse med forsendelseId=%s ikke funnet i dokdistDb",
					oppdaterForsendelseRequest.getForsendelseId()));
		}

		if (isGyldigForsendelseStatus(oppdaterForsendelseRequest.getForsendelseStatus())) {
			oppdaterDokumentstatusOgDistribusjonstatus(dokumentInfo, oppdaterForsendelseRequest.getForsendelseStatus());
		}

		if (isNotBlank(oppdaterForsendelseRequest.getKonversasjonId())) {
			dokumentInfo.setKonversasjonId(oppdaterForsendelseRequest.getKonversasjonId());
		}

		if (oppdaterForsendelseRequest.getVarselStatus() != null) {
			oppdaterVarselstatus(dokumentInfo, oppdaterForsendelseRequest.getVarselStatus());
		}

		if (SDP.equals(dokumentInfo.getDistribusjonInfo().getDistribusjonKanal())) {
			oppdaterDigitalDistribusjonAdresseFraDPI(dokumentInfo, oppdaterForsendelseRequest);
		}

		if (oppdaterForsendelseRequest.getForsendelseMetadata() != null || oppdaterForsendelseRequest.getForsendelseMetadataType() != null) {
			oppdaterForsendelseMetadata(dokumentInfo, oppdaterForsendelseRequest);
		}
	}

	private void oppdaterDokumentstatusOgDistribusjonstatus(DokumentInfo dokumentInfo, String nyForsendelsestatus) {
		final String dokumentstatus = dokumentInfo.getDokumentStatus().name();
		final String distribusjonstatus = dokumentInfo.getDistribusjonInfo().getDistribusjonStatus().name();

		if (!distribusjonstatus.equals(dokumentstatus)) {
			throw new IkkeSammenfallendeStatusException(format("Ikke sammenfallende statuser på forsendelse: distribusjonStatus er ikke lik dokumentStatus. distribusjonStatus=%s, dokumentStatus=%s",
					distribusjonstatus, dokumentstatus));
		}

		if (dokumentstatus.equals(nyForsendelsestatus)) {
			throw new StatusErAlleredeSattException(format("Dokumentstatus er allerede satt: Fikk forespørsel om å sette ny dokumentStatus=%s. Dokumentstatus for forsendelse=%s er allerede dokumentStatus=%s",
					nyForsendelsestatus,
					dokumentInfo.getDokumentInfoId(),
					dokumentstatus));
		}

		if (!isLovligDokumentstatusOvergang(dokumentstatus, nyForsendelsestatus)) {
			throw new UlovligStatusOvergangException(format("Ulovlig statusovergang: kan ikke sette ny dokumentStatus=%s når dokumentStatus=%s. Lovlige statusoverganger er " +
							"OPPRETTET -> KLAR_FOR_DIST, " +
							"KLAR_FOR_DIST -> OVERSENDT/EKSPEDERT, " +
							"OVERSENDT -> BEKREFTET/EKSPEDERT/FEILET, " +
							"BEKREFTET -> EKSPEDERT/FEILET",
					nyForsendelsestatus, dokumentstatus));
		}

		DokumentStatusCode nyDokumentStatus = valueOf(nyForsendelsestatus);
		DistribusjonStatusCode nyDistribusjonStatus = DistribusjonStatusCode.valueOf(nyForsendelsestatus);

		dokumentInfo.setDokumentStatus(nyDokumentStatus);
		dokumentInfo.getDistribusjonInfo().setDistribusjonStatus(nyDistribusjonStatus);

		if (EKSPEDERT.equals(nyDokumentStatus)) {
			dokumentInfo.setEkspedertDato(now());
		}
	}

	private void oppdaterVarselstatus(DokumentInfo dokumentInfo, VarselStatusCode nyVarselstatus) {
		final VarselStatusCode opprinneligVarselStatus = dokumentInfo.getDistribusjonInfo().getVarselStatus();

		if (opprinneligVarselStatus != null && opprinneligVarselStatus.equals(nyVarselstatus)) {
			throw new StatusErAlleredeSattException(format("Varselstatus er allerede satt: fikk forespørsel om å sette ny varselstatus=%s. Varselstatus for distribusjonId=%s er allerede varselstatus=%s",
					nyVarselstatus,
					dokumentInfo.getDistribusjonInfo().getDistribusjonId(),
					opprinneligVarselStatus));
		}

		if (!isLovligVarselstatusOvergang(opprinneligVarselStatus, nyVarselstatus)) {
			throw new UlovligStatusOvergangException(format("Ulovlig varselstatusovergang: kan ikke sette ny varselStatus=%s for distribusjon når varselStatus=%s. Lovlige statusoverganger er " +
							"OPPRETTET -> FEILET, " +
							"OPPRETTET -> FERDIGSTILT",
					nyVarselstatus, opprinneligVarselStatus));
		}

		dokumentInfo.getDistribusjonInfo().setVarselStatus(nyVarselstatus);
	}

	private void oppdaterDigitalDistribusjonAdresseFraDPI(DokumentInfo dokumentInfo, OppdaterForsendelseRequest oppdaterForsendelseRequest) {
		if (isNotBlank(oppdaterForsendelseRequest.getDigitalLeverandoeradresse())) {
			dokumentInfo.setDigitalDistributorId(oppdaterForsendelseRequest.getDigitalLeverandoeradresse());
		}

		if (isNotBlank(oppdaterForsendelseRequest.getDigitalPostkasseadresse())) {
			dokumentInfo.setDigitalPostkasseAdresse(oppdaterForsendelseRequest.getDigitalPostkasseadresse());
		}
	}

	private void oppdaterForsendelseMetadata(DokumentInfo dokumentInfo, OppdaterForsendelseRequest request) {
		byte[] metadata = request.getForsendelseMetadata();

		if (metadata != null && metadata.length == 0) {
			throw new ValideringFeiletException("forsendelseMetadata kan ikke være tom");
		}

		boolean harMetadata = metadata != null;
		boolean harMetadataType = request.getForsendelseMetadataType() != null;

		if (harMetadata != harMetadataType) {
			throw new ValideringFeiletException(
					"forsendelseMetadata og forsendelseMetadataType må enten begge være satt, eller begge være null. forsendelseMetadata=%s, forsendelseMetadataType=%s"
							.formatted(harMetadata ? "<satt>" : null, request.getForsendelseMetadataType()));
		}

		if (harMetadata) {
			dokumentInfo.setForsendelseMetadata(new String(metadata, UTF_8));
			dokumentInfo.setForsendelseMetadataType(request.getForsendelseMetadataType());
		}
	}

	private boolean isGyldigForsendelseStatus(String forsendelseStatus) {
		if (isBlank(forsendelseStatus)) {
			return false;
		}

		validateEnum(DokumentStatusCode.class, forsendelseStatus.trim());
		validateEnum(DistribusjonStatusCode.class, forsendelseStatus.trim());

		return true;
	}

}
