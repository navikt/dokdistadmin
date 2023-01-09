package no.nav.dokdistadmin.exception.functional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class MissingClaimException extends DokdistFunctionalException {

	public MissingClaimException(String message) {
		super(message);
	}
}
