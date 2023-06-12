package no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse;


import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.exception.functional.ValideringFeiletException;

import static java.lang.String.format;
import static no.nav.dokdistadmin.administrerforsendelse.FeilregistrerForsendelseService.FEILREGISTRER_FORSENDELSE_FEILMELDING;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.FEILET;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class FeilregistrerForsendelseValidator {

	public static void validerDistribusjonInfo(DistribusjonInfo distribusjonInfo) {
		validerResendingDistribusjonId(distribusjonInfo.getResendingDistribusjonId());
	}

    public static void validerDokumentInfo(DokumentInfo dokumentInfo) {
        validateDokumentStatus(dokumentInfo.getDokumentStatus());
    }

	private static void validerResendingDistribusjonId(String resendingDistribusjonId) {
		if (isNotBlank(resendingDistribusjonId)) {
			throw new ValideringFeiletException(format(
					FEILREGISTRER_FORSENDELSE_FEILMELDING,
					format("Feltet resendingDistribusjonId på forsendelsen du prøver å feilregistrere kan ikke ha en verdi, men har verdien=%s", resendingDistribusjonId))
			);
		}
	}

	private static void validateDokumentStatus(DokumentStatusCode dokumentStatusCode) {
		if (FEILET.equals(dokumentStatusCode)) {
			throw new ValideringFeiletException(format(
					FEILREGISTRER_FORSENDELSE_FEILMELDING,
					"Feltet dokumentStatusCode på forsendelsen du prøver å feilregistrere kan ikke ha verdien FEILET"));
		}
	}
}
