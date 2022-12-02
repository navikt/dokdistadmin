package no.nav.dokdistadmin.exception.functional;

public class DokdistFunctionalException extends RuntimeException {
	public DokdistFunctionalException(String message) {
		super(message);
	}

	public DokdistFunctionalException(String message, Throwable cause) {
		super(message, cause);
	}
}
