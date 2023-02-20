package no.nav.dokdistadmin.exception.functional;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(BAD_REQUEST)
public class OppdaterVarselInfoException extends DokdistadminFunctionalException {

	public OppdaterVarselInfoException(String message) {
		super(message);
	}
}
