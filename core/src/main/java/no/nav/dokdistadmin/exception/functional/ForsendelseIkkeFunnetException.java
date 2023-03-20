package no.nav.dokdistadmin.exception.functional;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(NOT_FOUND)
public class ForsendelseIkkeFunnetException extends DokdistadminFunctionalException {

	public ForsendelseIkkeFunnetException(String message) {
		super(message);
	}
}
