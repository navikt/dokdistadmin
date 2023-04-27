package no.nav.dokdistadmin.exception;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.exception.functional.DokumentStatusErAlleredeSattException;
import no.nav.dokdistadmin.exception.functional.ForsendelseIkkeFunnetException;
import no.nav.dokdistadmin.exception.functional.KanIkkeBestemmeDokumentrekkefoelgeException;
import no.nav.dokdistadmin.exception.functional.OppdaterVarselInfoException;
import no.nav.dokdistadmin.exception.functional.UlovligStatusOvergangException;
import no.nav.dokdistadmin.exception.technical.DokdistadminTechnicalException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Slf4j
@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(
			{
					ConstraintViolationException.class,
					MethodArgumentTypeMismatchException.class,
					UlovligStatusOvergangException.class,
					DokumentStatusErAlleredeSattException.class,
					OppdaterVarselInfoException.class,
					ForsendelseIkkeFunnetException.class,
					KanIkkeBestemmeDokumentrekkefoelgeException.class
			}
	)
	public ResponseEntity<Object> inputValidationExceptionHandler(Exception ex) {
		log.warn("rdist001 feilet funkjonell med feilmelding={}", ex.getMessage());

		if (ex instanceof ForsendelseIkkeFunnetException) {
			return new ResponseEntity<>(responseBody(ex), NOT_FOUND);
		} else if (ex instanceof KanIkkeBestemmeDokumentrekkefoelgeException) {
			return new ResponseEntity<>(responseBody(ex), NO_CONTENT);
		} else {
			return new ResponseEntity<>(responseBody(ex), BAD_REQUEST);
		}
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		String errormessage = ex.getBindingResult().getFieldErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.collect(Collectors.joining(", "));

		return new ResponseEntity<>(errormessage, BAD_REQUEST);
	}

	@ExceptionHandler({
			DokdistadminTechnicalException.class
	})
	public ResponseEntity<Object> handleTechnicalException(Exception ex) throws Exception {
		if (ex instanceof DokdistadminTechnicalException) {
			log.warn("rdist001 feilet teknisk med feilmelding={}", ex.getMessage());
			return new ResponseEntity<>(responseBody(ex), INTERNAL_SERVER_ERROR);
		} else {
			throw ex;
		}
	}

	private Map<String, Object> responseBody(Exception e) {
		Map<String, Object> responseBody = new HashMap<>();
		responseBody.put("message", e.getMessage());
		responseBody.put("error", e.getStackTrace());
		return responseBody;
	}
}
