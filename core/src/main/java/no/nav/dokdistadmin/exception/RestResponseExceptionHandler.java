package no.nav.dokdistadmin.exception;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.exception.functional.DistribusjonIkkeFunnetException;
import no.nav.dokdistadmin.exception.functional.DokumentStatusErAlleredeSattException;
import no.nav.dokdistadmin.exception.functional.ForsendelseIkkeFunnetException;
import no.nav.dokdistadmin.exception.functional.KanIkkeBestemmeDokumentrekkefoelgeException;
import no.nav.dokdistadmin.exception.functional.OppdaterVarselInfoException;
import no.nav.dokdistadmin.exception.functional.PostdestinasjonIkkeFunnetException;
import no.nav.dokdistadmin.exception.functional.UlovligStatusOvergangException;
import no.nav.dokdistadmin.exception.functional.ValideringFeiletException;
import no.nav.dokdistadmin.exception.technical.DokdistadminTechnicalException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Slf4j
@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler({
			ConstraintViolationException.class,
			UlovligStatusOvergangException.class,
			DokumentStatusErAlleredeSattException.class,
			OppdaterVarselInfoException.class,
			ValideringFeiletException.class
	})
	public ResponseEntity<Object> inputValidationExceptionHandler(Exception e) {
		log.warn("rdist001 feilet funksjonelt med feilmelding={}", e.getMessage());

		return new ResponseEntity<>(new ErrorResponseBody(e.getMessage(), e.getStackTrace()), BAD_REQUEST);
	}

	@ExceptionHandler({
			ForsendelseIkkeFunnetException.class,
			PostdestinasjonIkkeFunnetException.class,
			DistribusjonIkkeFunnetException.class
	})
	public ResponseEntity<Object> resourceNotFoundExceptionHandler(Exception e) {
		log.warn("rdist001 feilet funksjonelt med feilmelding={}", e.getMessage());
		return new ResponseEntity<>(new ErrorResponseBody(e.getMessage(), e.getStackTrace()), NOT_FOUND);
	}

	@ExceptionHandler({
			KanIkkeBestemmeDokumentrekkefoelgeException.class
	})
	public ResponseEntity<Object> noContentExceptionHandler(Exception e) {
		log.warn("rdist001 feilet funksjonelt med feilmelding={}", e.getMessage());
		return new ResponseEntity<>(new ErrorResponseBody(e.getMessage(), e.getStackTrace()), NO_CONTENT);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		String errormessage = ex.getBindingResult().getFieldErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.collect(Collectors.joining(", "));

		log.warn("rdist001 feilet funksjonelt med feilmelding={}", errormessage);

		return new ResponseEntity<>(errormessage, BAD_REQUEST);
	}

	@ExceptionHandler({
			DokdistadminTechnicalException.class
	})
	public ResponseEntity<Object> handleTechnicalException(Exception e) throws Exception {
		if (e instanceof DokdistadminTechnicalException) {
			log.warn("rdist001 feilet teknisk med feilmelding={}", e.getMessage());
			return new ResponseEntity<>(new ErrorResponseBody(e.getMessage(), e.getStackTrace()), INTERNAL_SERVER_ERROR);
		} else {
			throw e;
		}
	}

	public record ErrorResponseBody(String message, StackTraceElement[] error) {
	}
}
