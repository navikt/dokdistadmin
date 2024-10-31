package no.nav.dokdistadmin.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.exception.functional.DistribusjonIkkeFunnetException;
import no.nav.dokdistadmin.exception.functional.DokumentStatusErAlleredeSattException;
import no.nav.dokdistadmin.exception.functional.FlereForsendelserFunnetException;
import no.nav.dokdistadmin.exception.functional.ForsendelseIkkeFunnetException;
import no.nav.dokdistadmin.exception.functional.ForsendelseIkkeFunnetInfomeldingException;
import no.nav.dokdistadmin.exception.functional.KanIkkeBestemmeDokumentrekkefoelgeException;
import no.nav.dokdistadmin.exception.functional.OppdaterVarselInfoException;
import no.nav.dokdistadmin.exception.functional.PostdestinasjonIkkeFunnetException;
import no.nav.dokdistadmin.exception.functional.UlovligStatusOvergangException;
import no.nav.dokdistadmin.exception.functional.ValideringFeiletException;
import no.nav.dokdistadmin.exception.technical.DokdistadminTechnicalException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String RDIST001_FUNKSJONELL_FEILMELDING = "rdist001 feilet funksjonelt med feilmelding: {}";
	private static final String RDIST001_TEKNISK_FEILMELDING = "rdist001 feilet teknisk med feilmelding: {}";

	@ExceptionHandler({
			ConstraintViolationException.class,
			UlovligStatusOvergangException.class,
			DokumentStatusErAlleredeSattException.class,
			OppdaterVarselInfoException.class,
			ValideringFeiletException.class
	})
	public ResponseEntity<Object> inputValidationExceptionHandler(Exception e) {
		log.warn(RDIST001_FUNKSJONELL_FEILMELDING, e.getMessage(), e);

		return getResponseEntity(BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler({
			ForsendelseIkkeFunnetException.class,
			PostdestinasjonIkkeFunnetException.class,
			DistribusjonIkkeFunnetException.class
	})
	public ResponseEntity<Object> resourceNotFoundExceptionHandler(Exception e) {
		log.warn(RDIST001_FUNKSJONELL_FEILMELDING, e.getMessage(), e);

		return getResponseEntity(NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler({
			ForsendelseIkkeFunnetInfomeldingException.class,
	})
	public ResponseEntity<Object> resourceNotFoundInfomeldingExceptionHandler(Exception e) {
		log.info(RDIST001_FUNKSJONELL_FEILMELDING, e.getMessage(), e);

		return getResponseEntity(NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler({
			KanIkkeBestemmeDokumentrekkefoelgeException.class
	})
	public ResponseEntity<Object> noContentExceptionHandler(Exception e) {
		log.warn(RDIST001_FUNKSJONELL_FEILMELDING, e.getMessage(), e);

		return getResponseEntity(NO_CONTENT, e.getMessage());
	}

	@ExceptionHandler({
			FlereForsendelserFunnetException.class
	})
	public ResponseEntity<Object> conflictExceptionHandler(Exception e) {
		log.warn(RDIST001_FUNKSJONELL_FEILMELDING, e.getMessage(), e);

		return getResponseEntity(CONFLICT, e.getMessage());
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		String feilmelding = ex.getBindingResult().getFieldErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.collect(Collectors.joining(", "));

		log.warn(RDIST001_FUNKSJONELL_FEILMELDING, feilmelding, ex);

		return getResponseEntity(BAD_REQUEST, feilmelding);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		if (ex.getCause() instanceof InvalidFormatException ife) {
			return handleInvalidFormatException(ex, ife);
		}

		return handleExceptionInternal(ex, ex.getMessage(), headers, BAD_REQUEST, request);
	}

	@ExceptionHandler({
			DokdistadminTechnicalException.class
	})
	public ResponseEntity<Object> handleTechnicalException(Exception e) throws Exception {
		if (e instanceof DokdistadminTechnicalException) {
			log.warn(RDIST001_TEKNISK_FEILMELDING, e.getMessage(), e);

			return getResponseEntity(INTERNAL_SERVER_ERROR, e.getMessage());
		} else {
			throw e;
		}
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleAll(Exception e) {
		String feilmelding = format("rdist001 feilet med feilmelding=%s", e.getMessage());

		log.warn(feilmelding, e);

		return getResponseEntity(INTERNAL_SERVER_ERROR, feilmelding);
	}

	private static ResponseEntity<Object> handleInvalidFormatException(HttpMessageNotReadableException e, InvalidFormatException invalidFormatException) {
		String feilmelding;
		var fieldName = invalidFormatException.getPath().getFirst().getFieldName();
		var value = invalidFormatException.getValue();
		var targetType = invalidFormatException.getTargetType();

		if (targetType.isEnum()) {
			feilmelding = format("Feltet %s=%s må være en av %s", fieldName, value, Arrays.toString(targetType.getEnumConstants()));
		} else if (targetType.equals(LocalDateTime.class)) {
			feilmelding = format("Feltet %s=%s må være et gyldig tidspunkt", fieldName, value);
		} else if (targetType.equals(Long.class)) {
			feilmelding = format("Feltet %s=%s må være et tall", fieldName, value);
		} else {
			feilmelding = format("'%s' er ikke en gyldig verdi for feltet %s", value, fieldName);
		}

		log.warn(RDIST001_FUNKSJONELL_FEILMELDING, feilmelding, e);

		return getResponseEntity(BAD_REQUEST, feilmelding);
	}

	private static ResponseEntity<Object> getResponseEntity(HttpStatus status, String message) {
		return ResponseEntity.status(status)
				.contentType(APPLICATION_JSON)
				.body(format("\"%s\"", message));
	}

}
