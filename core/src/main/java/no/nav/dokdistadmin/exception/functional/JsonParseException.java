package no.nav.dokdistadmin.exception.functional;

import no.nav.dokdistadmin.exception.technical.DokdistadminTechnicalException;

public class JsonParseException extends DokdistadminTechnicalException {
	public JsonParseException(String message, Throwable cause) {
		super(message, cause);
	}
}
