package no.nav.dokdistadmin.exception.functional;

public class DokdistadminFunctionalException extends RuntimeException {
	public DokdistadminFunctionalException(String message) {
		super(message);
	}

	public DokdistadminFunctionalException(String message, Throwable cause) {
		super(message, cause);
	}
}
