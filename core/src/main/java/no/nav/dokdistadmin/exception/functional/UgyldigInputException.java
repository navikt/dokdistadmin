package no.nav.dokdistadmin.exception.functional;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(BAD_REQUEST)
public class UgyldigInputException extends DokdistadminFunctionalException {

	public UgyldigInputException(String message) {
		super(message);
	}
}
