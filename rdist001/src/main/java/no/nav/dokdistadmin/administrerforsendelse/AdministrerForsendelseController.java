package no.nav.dokdistadmin.administrerforsendelse;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.AvstemEkspederteForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.AvstemForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.HentEkspederteForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.HentEkspederteForsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.uekspederteforsendelser.HentUekspederteForsendelserResponse;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Validated
@Protected
@RestController
@RequestMapping("/rest/v1/administrerforsendelse")
public class AdministrerForsendelseController {

	private final AdministrerForsendelseService ekspederteForsendelserService;

	public AdministrerForsendelseController(AdministrerForsendelseService ekspederteForsendelserService) {
		this.ekspederteForsendelserService = ekspederteForsendelserService;
	}

	@GetMapping("/hentekspederteforsendelser")
	public ResponseEntity<HentEkspederteForsendelserResponse> hentEkspederteForsendelser(@RequestBody @Valid HentEkspederteForsendelserRequest hentEkspederteForsendelserRequest) {
		log.info("hentekspederteforsendelser har mottatt kall om å hente ekspederte forsendelser");

		HentEkspederteForsendelserResponse ekspederteForsendelser = ekspederteForsendelserService.hentEkspederteForsendelser(hentEkspederteForsendelserRequest.getMaksForsendelser());
		log.info("hentekspederteforsendelser har hentet {} ekspederte forsendelser.", ekspederteForsendelser.forsendelser().size());

		return ekspederteForsendelser.forsendelser().isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(ekspederteForsendelser);
	}

	@PutMapping("/avstemekspederteforsendelser")
	public ResponseEntity<Void> avstemEkspederteForsendelser(@RequestBody @Valid AvstemEkspederteForsendelserRequest forsendelserRequest) {
		log.info("avstemekspederteforsendelser har mottatt kall om å avstemme {} ekspederte forsendelser", forsendelserRequest.getForsendelser().size());

		var antallOppdaterteForsendelser = ekspederteForsendelserService.avstemEkspederteForsendelser(forsendelserRequest);

		log.info("avstemekspederteforsendelser har oppdatert avstemtArkivDato på {} forsendelser", antallOppdaterteForsendelser);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/hentuekspederteforsendelser/{distribusjonkanal}/{antallTimer}")
	public ResponseEntity<HentUekspederteForsendelserResponse> hentUekspederteForsendelser(
			@PathVariable String distribusjonkanal,
			@PathVariable @PositiveOrZero(message = "antallTimer må være et positivt tall") Long antallTimer) {

		log.info("hentuekspederteforsendelser har mottatt kall om å hente uekspederte forsendelser med distribusjonKanal={}, som er eldre enn {} timer",
				distribusjonkanal, antallTimer);

		HentUekspederteForsendelserResponse uekspederteForsendelser = ekspederteForsendelserService.hentUekspederteForsendelser(distribusjonkanal, antallTimer);

		log.info("hentuekspederteforsendelser har hentet {} uekspederte forsendelser med distribusjonkanal={}, som er eldre enn {} timer",
				uekspederteForsendelser.getUekspederteForsendelser().size(), distribusjonkanal, antallTimer);

		return uekspederteForsendelser.getUekspederteForsendelser().isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(uekspederteForsendelser);
	}

	@PutMapping("/avstemforsendelser")
	public ResponseEntity<Void> avstemForsendelser(@RequestBody @Valid AvstemForsendelserRequest avstemForsendelserRequest) {

		log.info("avstemforsendelser har mottatt kall om å oppdatere {} forsendelser med avstemtDato og avstemtReferanse={}",
				avstemForsendelserRequest.getForsendelser().size(),
				avstemForsendelserRequest.getAvstemtReferanse());

		var antallOppdaterteForsendelser = ekspederteForsendelserService.avstemForsendelser(avstemForsendelserRequest);

		log.info("avstemforsendelser har oppdatert avstemtReferanse og avstemtDato på {} forsendelser", antallOppdaterteForsendelser);

		return ResponseEntity.ok().build();
	}

	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler({
			MethodArgumentNotValidException.class,
			ConstraintViolationException.class,
			MethodArgumentTypeMismatchException.class
	})
	public ResponseEntity<String> inputValidationExceptionHandler(Exception exception) {
		if (exception instanceof MethodArgumentNotValidException e) {
			return ResponseEntity.badRequest().body(e.getAllErrors().get(0).getDefaultMessage());
		} else {
			return ResponseEntity.badRequest().body(exception.getMessage());
		}
	}

}