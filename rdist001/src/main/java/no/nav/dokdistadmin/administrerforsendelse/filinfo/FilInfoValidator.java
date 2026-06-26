package no.nav.dokdistadmin.administrerforsendelse.filinfo;

import no.nav.dokdistadmin.domain.FilStatusCode;
import no.nav.dokdistadmin.exception.functional.UgyldigInputException;

import static java.lang.String.format;
import static no.nav.dokdistadmin.administrerforsendelse.filinfo.FilInfoMapper.VALID_FIL_TYPER;
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
		if (request.filInfoId() != null && erFilnavnEllerTypeNotNull(request)) {
			throw new UgyldigInputException("filnavn og filtype kan ikke oppgis når filInfoId er satt");
		}

	}

	private static void validerFilnavnOgTypeHvisFilInfoIdErNull(FilInfoRequest filInfoRequest) {
		if (filInfoRequest.filInfoId() == null && erFilnavnEllerTypeNull(filInfoRequest)) {
			throw new UgyldigInputException("filnavn og filtype kan ikke være null eller tom når filInfoId er null");
		}

	}

	private static void validerFilType(FilInfoRequest filInfoRequest) {
		if (isNotBlank(filInfoRequest.filtype()) && !VALID_FIL_TYPER.contains(filInfoRequest.filtype())) {
			throw new UgyldigInputException(format("Ugyldig input: %s er ikke en gyldig kodeverdi i %s", filInfoRequest.filtype(), VALID_FIL_TYPER));
		}
	}

	private static boolean erFilnavnEllerTypeNull(FilInfoRequest filInfoRequest) {
		return isBlank(filInfoRequest.filnavn()) || isBlank(filInfoRequest.filtype());
	}

	private static boolean erFilnavnEllerTypeNotNull(FilInfoRequest filInfoRequest) {
		return isNotBlank(filInfoRequest.filnavn()) || isNotBlank(filInfoRequest.filtype());
	}

	private static void validerFilStatus(FilInfoRequest filInfoRequest) {
		if (isNotBlank(filInfoRequest.status())) {
			validateEnum(FilStatusCode.class, filInfoRequest.status());
		}
	}

	private FilInfoValidator() {
	}
}
