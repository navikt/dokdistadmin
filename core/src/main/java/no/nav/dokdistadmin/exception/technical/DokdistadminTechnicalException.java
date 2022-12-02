package no.nav.dokdistadmin.exception.technical;

public abstract class DokdistadminTechnicalException extends RuntimeException {
	public DokdistadminTechnicalException(String message) {
		super(message);
	}

	public DokdistadminTechnicalException(String message, Throwable cause) {
		super(message, cause);
	}
}
