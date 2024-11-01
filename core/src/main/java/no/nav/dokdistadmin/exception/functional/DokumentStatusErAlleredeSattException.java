package no.nav.dokdistadmin.exception.functional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.OK)
public class DokumentStatusErAlleredeSattException extends DokdistadminFunctionalException {
	public DokumentStatusErAlleredeSattException(String message) {
		super(message);
	}
}
