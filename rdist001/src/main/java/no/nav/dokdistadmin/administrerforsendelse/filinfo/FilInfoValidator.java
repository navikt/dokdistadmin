package no.nav.dokdistadmin.administrerforsendelse.filinfo;

import no.nav.dokdistadmin.domain.FilStatusCode;
import no.nav.dokdistadmin.domain.FilTypeCode;
import no.nav.dokdistadmin.exception.functional.UgyldigInputException;

import static no.nav.dokdistadmin.utils.EnumUtils.validateEnum;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class FilInfoValidator {

	public static void validerFilInfoRequest(FilInfoRequest request) {
		validerFilnavnOgTypeHvisFilInfoIdErSatt(request);
		validerFilType(request);
		validerFilStatus(request);
		validerFilnavnOgTypeHvisFilInfoIdErNull(request);

	}

	private static void validerFilnavnOgTypeHvisFilInfoIdErSatt(FilInfoRequest request) {
		if (request.filInfoId() != null && !erFilnavnAndTypeNull(request)) {
				throw new UgyldigInputException("filnavn og filtype kan ikke oppgis når filInfoId er satt");
			}

	}

	private static void validerFilnavnOgTypeHvisFilInfoIdErNull(FilInfoRequest filInfoRequest){
		if (filInfoRequest.filInfoId() == null && erFilnavnAndTypeNull(filInfoRequest)) {
				throw new UgyldigInputException("filnavn og filtype kan ikke være null eller tom");
			}

	}

	private static void validerFilType(FilInfoRequest filInfoRequest) {
		if (isNotBlank(filInfoRequest.filtype())) {
			validateEnum(FilTypeCode.class, filInfoRequest.filtype());
		}
	}

	private static boolean erFilnavnAndTypeNull(FilInfoRequest filInfoRequest) {
		return isBlank(filInfoRequest.filnavn()) && isBlank(filInfoRequest.filtype());
	}

	private static void validerFilStatus(FilInfoRequest filInfoRequest) {
		if (isNotBlank(filInfoRequest.status())) {
			validateEnum(FilStatusCode.class, filInfoRequest.status());
		}
	}

	private FilInfoValidator() {
	}
}
