package no.nav.dokdistadmin.exception.functional;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.OK;

@ResponseStatus(OK)
public class StatusErAlleredeSattException extends DokdistadminFunctionalException {
	public StatusErAlleredeSattException(String message) {
		super(message);
	}
}
