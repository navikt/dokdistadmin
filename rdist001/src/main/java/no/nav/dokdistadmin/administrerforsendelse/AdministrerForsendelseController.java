package no.nav.dokdistadmin.administrerforsendelse;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.administrerforsendelse.eformidlingforsendelser.HentEformidlingforsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.AvstemEkspederteForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.AvstemForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.HentEkspederteForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.HentEkspederteForsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.HentForsendelseResponse;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequest;
import no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser.OppdaterForsendelseRequest;
import no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser.OppdaterForsendelseService;
import no.nav.dokdistadmin.administrerforsendelse.uekspederteforsendelser.HentUekspederteForsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.varselinfo.OppdaterVarselInfoRequest;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.exception.functional.DokumentStatusErAlleredeSattException;
import no.nav.dokdistadmin.exception.functional.UlovligStatusOvergangException;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Validated
@Protected
@RestController
@RequestMapping("/rest/v1/administrerforsendelse")
public class AdministrerForsendelseController {

	private final AdministrerForsendelseService forsendelserService;
	private final VarselInfoService varselInfoService;
	private final OppdaterForsendelseService oppdaterForsendelseService;

	public AdministrerForsendelseController(AdministrerForsendelseService forsendelserService,
											VarselInfoService varselInfoService, OppdaterForsendelseService oppdaterForsendelseService) {
		this.forsendelserService = forsendelserService;
		this.varselInfoService = varselInfoService;
		this.oppdaterForsendelseService = oppdaterForsendelseService;
	}

	@PostMapping
	public ResponseEntity<Forsendelse> opprettForsendelse(@RequestBody @Valid OpprettForsendelseRequest opprettForsendelseRequest) {
		log.info("opprettForsendelse har mottatt kall om å persistere forsendelse med bestillingsId={}", opprettForsendelseRequest.getBestillingsId());

		Forsendelse forsendelse = forsendelserService.opprettForsendelse(opprettForsendelseRequest);

		log.info("opprettForsendelse har persistert forsendelse med bestillingsId={}. ForsendelseId={}", opprettForsendelseRequest
				.getBestillingsId(), forsendelse.getForsendelseId());

		return ResponseEntity.ok(forsendelse);
	}

	@GetMapping("/{forsendelseId}")
	public ResponseEntity<HentForsendelseResponse> hentForsendelse(
			@PathVariable("forsendelseId") @Positive(message = "forsendelseId må være et positivt tall") Long forsendelseId) {
		log.info("rdist001 har mottatt kall om å hente forsendelse med forsendelseId={}", forsendelseId);

		HentForsendelseResponse hentForsendelseResponse = forsendelserService.hentForsendelse(forsendelseId);
		log.info("rdist001 har hentet forsendelse med forsendelseId={}", forsendelseId);

		return ResponseEntity.ok(hentForsendelseResponse);
	}

	@PutMapping
	public ResponseEntity<String> oppdatereForsendelse(@RequestBody @Valid OppdaterForsendelseRequest oppdaterForsendelseRequest) {
		log.info(format("rdist001 har mottatt kall om å oppdatere forsendelse på forsendelse med forsendelseId=%s",
				oppdaterForsendelseRequest.getForsendelseId()));
		oppdaterForsendelseService.oppdatereForsendelse(oppdaterForsendelseRequest);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/hentekspederteforsendelser")
	public ResponseEntity<HentEkspederteForsendelserResponse> hentEkspederteForsendelser(@RequestBody @Valid HentEkspederteForsendelserRequest hentEkspederteForsendelserRequest) {
		log.info("hentekspederteforsendelser har mottatt kall om å hente ekspederte forsendelser");

		HentEkspederteForsendelserResponse ekspederteForsendelser = forsendelserService.hentEkspederteForsendelser(hentEkspederteForsendelserRequest.getMaksForsendelser());
		log.info("hentekspederteforsendelser har hentet {} ekspederte forsendelser.", ekspederteForsendelser.forsendelser().size());

		return ekspederteForsendelser.forsendelser().isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(ekspederteForsendelser);
	}

	@PutMapping("/avstemekspederteforsendelser")
	public ResponseEntity<Void> avstemEkspederteForsendelser(@RequestBody @Valid AvstemEkspederteForsendelserRequest forsendelserRequest) {
		log.info("avstemekspederteforsendelser har mottatt kall om å avstemme {} ekspederte forsendelser", forsendelserRequest.getForsendelser().size());

		var antallOppdaterteForsendelser = forsendelserService.avstemEkspederteForsendelser(forsendelserRequest);

		log.info("avstemekspederteforsendelser har oppdatert avstemtArkivDato på {} forsendelser", antallOppdaterteForsendelser);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/hentuekspederteforsendelser/{distribusjonkanal}/{antallTimer}")
	public ResponseEntity<HentUekspederteForsendelserResponse> hentUekspederteForsendelser(
			@PathVariable String distribusjonkanal,
			@PathVariable @PositiveOrZero(message = "antallTimer må være et positivt tall") Long antallTimer) {

		log.info("hentuekspederteforsendelser har mottatt kall om å hente uekspederte forsendelser med distribusjonKanal={}, som er eldre enn {} timer",
				distribusjonkanal, antallTimer);

		HentUekspederteForsendelserResponse uekspederteForsendelser = forsendelserService.hentUekspederteForsendelser(distribusjonkanal, antallTimer);

		log.info("hentuekspederteforsendelser har hentet {} uekspederte forsendelser med distribusjonkanal={}, som er eldre enn {} timer",
				uekspederteForsendelser.getUekspederteForsendelser().size(), distribusjonkanal, antallTimer);

		return uekspederteForsendelser.getUekspederteForsendelser().isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(uekspederteForsendelser);
	}

	@PutMapping("/avstemforsendelser")
	public ResponseEntity<Void> avstemForsendelser(@RequestBody @Valid AvstemForsendelserRequest avstemForsendelserRequest) {

		log.info("avstemforsendelser har mottatt kall om å oppdatere {} forsendelser med avstemtDato og avstemtReferanse={}",
				avstemForsendelserRequest.getForsendelser().size(),
				avstemForsendelserRequest.getAvstemtReferanse());

		var antallOppdaterteForsendelser = forsendelserService.avstemForsendelser(avstemForsendelserRequest);

		log.info("avstemforsendelser har oppdatert avstemtReferanse og avstemtDato på {} forsendelser", antallOppdaterteForsendelser);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/henteformidlingforsendelser")
	public ResponseEntity<HentEformidlingforsendelserResponse> hentEformidlingForsendelser(@RequestParam DistribusjonKanalCode distribusjonKanal) {
		log.info("henteformidlingforsendelser har mottatt kall om å hente eformidlingforsendelser for distribusjonskanal={}", distribusjonKanal);

		var result = forsendelserService.hentEformidlingForsendelser(distribusjonKanal);

		log.info("henteformidlingforsendelser har hentet antall={} eformidlingforsendelser for distribusjonskanal={}",
				result.getForsendelser().size(), distribusjonKanal);

		return ResponseEntity.ok(result);
	}

	@PutMapping("/oppdatervarselinfo")
	public ResponseEntity<Void> oppdaterVarselInfo(@RequestBody @Valid OppdaterVarselInfoRequest oppdaterVarselInfoRequest) {
		log.info("oppdatervarselinfo har mottatt kall om å oppdatere varselinfo på forsendelse med forsendelseId={}", oppdaterVarselInfoRequest.getForsendelseId());

		var antallOppdaterteVarselInfo = varselInfoService.oppdaterVarselInfo(oppdaterVarselInfoRequest);

		log.info("oppdatervarselinfo har oppdatert antall={} varselinfo på forsendelse med forsendelseId={}", antallOppdaterteVarselInfo, oppdaterVarselInfoRequest.getForsendelseId());

		return ResponseEntity.ok().build();
	}

	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler({
			MethodArgumentNotValidException.class,
			ConstraintViolationException.class,
			MethodArgumentTypeMismatchException.class,
			UlovligStatusOvergangException.class,
			DokumentStatusErAlleredeSattException.class,

	})
	public ResponseEntity<String> inputValidationExceptionHandler(Exception exception) {
		String errormessage;
		if (exception instanceof MethodArgumentNotValidException e) {
			errormessage = e.getAllErrors().stream()
					.map(DefaultMessageSourceResolvable::getDefaultMessage)
					.collect(Collectors.joining(", "));
		} else {
			errormessage = exception.getMessage();
		}
		log.warn("rdist001 Validering av input feilet fordi: {}", errormessage);
		return ResponseEntity.badRequest().body(errormessage);
	}
}