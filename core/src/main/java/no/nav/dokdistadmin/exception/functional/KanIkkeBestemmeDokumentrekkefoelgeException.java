package no.nav.dokdistadmin.exception.functional;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@ResponseStatus(NO_CONTENT)
public class KanIkkeBestemmeDokumentrekkefoelgeException extends DokdistadminFunctionalException {

	public KanIkkeBestemmeDokumentrekkefoelgeException(String message) {
		super(message);
	}
}
