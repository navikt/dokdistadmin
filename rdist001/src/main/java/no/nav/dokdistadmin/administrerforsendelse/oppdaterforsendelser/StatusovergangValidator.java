package no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser;

import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;

import static no.nav.dokdistadmin.domain.DokumentStatusCode.BEKREFTET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.KLAR_FOR_DIST;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OVERSENDT;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class StatusovergangValidator {

	public static boolean isLovligStatusovergang(String oldDokumentStatus, String nyForsendelseStatus) {
		return isStatusOvergangOpprettetToKlarForDist(oldDokumentStatus, nyForsendelseStatus) ||
				isStatusOvergangKlarForDistToOversendt(oldDokumentStatus, nyForsendelseStatus) ||
				isStatusOvergangKlarForDistToEkspedert(oldDokumentStatus,nyForsendelseStatus) ||
				isStatusOvergangOversendtToBekreftet(oldDokumentStatus, nyForsendelseStatus) ||
				isStatusOvergangOversendtToEkspedert(oldDokumentStatus, nyForsendelseStatus) ||
				isStatusOvergangOversendtToFeilet(oldDokumentStatus, nyForsendelseStatus) ||
				isStatusOvergangBekreftetToEkspedert(oldDokumentStatus, nyForsendelseStatus) ||
				isStatusOvergangBekreftetToFeilet(oldDokumentStatus, nyForsendelseStatus);
	}

	public static boolean isDistribusjonStatusEqualToDokumentStatus(DokumentInfo dokumentInfo) {
		return dokumentInfo.getDistribusjonInfo().getDistribusjonStatus().name().equals(dokumentInfo.getDokumentStatus().name());
	}

	public static boolean isDokumentStatusEqualToForsendelseStatus(String oldDokumentStatus, String nyForsendelseStatus) {
		return oldDokumentStatus.equals(nyForsendelseStatus);
	}

	public static boolean isDigitalAdresseSatt(OppdaterForsendelseRequest oppdaterForsendelseRequest) {
		return isNotBlank(oppdaterForsendelseRequest.getDigitalLeverandoeradresse()) ||
				isNotBlank(oppdaterForsendelseRequest.getDigitalPostkasseadresse());
	}

	private static boolean isStatusOvergangOpprettetToKlarForDist(String oldDokumentStatus, String nyForsendelseStatus) {
		return DokumentStatusCode.OPPRETTET.name().equals(oldDokumentStatus) && KLAR_FOR_DIST.name().equals(nyForsendelseStatus);
	}

	private static boolean isStatusOvergangKlarForDistToOversendt(String oldDokumentStatus, String nyForsendelseStatus) {
		return KLAR_FOR_DIST.name().equals(oldDokumentStatus) && OVERSENDT.name().equals(nyForsendelseStatus);
	}

	// Dokdistdpv forsendelser til Altinn støtter dette statusovergang
	private static boolean isStatusOvergangKlarForDistToEkspedert(String oldDokumentStatus, String nyForsendelseStatus) {
		return KLAR_FOR_DIST.name().equals(oldDokumentStatus) && EKSPEDERT.name().equals(nyForsendelseStatus);
	}

	private static boolean isStatusOvergangOversendtToBekreftet(String oldDokumentStatus, String nyForsendelseStatus) {
		return OVERSENDT.name().equals(oldDokumentStatus) && BEKREFTET.name().equals(nyForsendelseStatus);
	}

	private static boolean isStatusOvergangOversendtToEkspedert(String oldDokumentStatus, String nyForsendelseStatus) {
		return OVERSENDT.name().equals(oldDokumentStatus) && EKSPEDERT.name().equals(nyForsendelseStatus);
	}

	private static boolean isStatusOvergangOversendtToFeilet(String oldDokumentStatus, String nyForsendelseStatus) {
		return OVERSENDT.name().equals(oldDokumentStatus) && DokumentStatusCode.FEILET.name().equals(nyForsendelseStatus);
	}

	private static boolean isStatusOvergangBekreftetToEkspedert(String oldDokumentStatus, String nyForsendelseStatus) {
		return BEKREFTET.name().equals(oldDokumentStatus) && EKSPEDERT.name().equals(nyForsendelseStatus);
	}

	private static boolean isStatusOvergangBekreftetToFeilet(String oldDokumentStatus, String nyForsendelseStatus) {
		return BEKREFTET.name().equals(oldDokumentStatus) && DokumentStatusCode.FEILET.name().equals(nyForsendelseStatus);
	}

	private StatusovergangValidator() {
	}
}
