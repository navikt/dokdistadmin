package no.nav.dokdistadmin.domain.exception;

/**
 * @author Andreas Berg Skomedal, Visma Consulting.
 */
public class DuplicateResponseException extends RuntimeException {

	public DuplicateResponseException() {
		super();
	}

	public DuplicateResponseException(String message) {
		super(message);
	}

	public DuplicateResponseException(String message, Exception e) {
		super(message, e);
	}
}
