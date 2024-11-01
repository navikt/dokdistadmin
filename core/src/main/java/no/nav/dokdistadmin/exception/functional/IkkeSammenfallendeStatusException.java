package no.nav.dokdistadmin.exception.functional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class IkkeSammenfallendeStatusException extends DokdistadminFunctionalException {
	public IkkeSammenfallendeStatusException(String message) {
		super(message);
	}
}
