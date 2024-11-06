package no.nav.dokdistadmin.exception.functional;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class IkkeSammenfallendeStatusException extends DokdistadminFunctionalException {
	public IkkeSammenfallendeStatusException(String message) {
		super(message);
	}
}
