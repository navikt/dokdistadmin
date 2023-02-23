package no.nav.dokdistadmin.administrerforsendelse;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.administrerforsendelse.eformidlingforsendelser.HentEformidlingforsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.AvstemEkspederteForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.AvstemForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.HentEkspederteForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.HentEkspederteForsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequest;
import no.nav.dokdistadmin.administrerforsendelse.uekspederteforsendelser.HentUekspederteForsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.varselinfo.OppdaterVarselInfoRequest;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.security.token.support.core.api.Protected;
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
import javax.validation.constraints.PositiveOrZero;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Validated
@Protected
@RestController
@RequestMapping("/rest/v1/administrerforsendelse")
public class AdministrerForsendelseController {

	private final AdministrerForsendelseService forsendelserService;
	private final VarselInfoService varselInfoService;

	public AdministrerForsendelseController(AdministrerForsendelseService forsendelserService,
											VarselInfoService varselInfoService) {
		this.forsendelserService = forsendelserService;
		this.varselInfoService = varselInfoService;
	}

	@PostMapping
	public ResponseEntity<Forsendelse> opprettForsendelse(OpprettForsendelseRequest opprettForsendelseRequest) {
		log.info("opprettForsendelse har mottatt kall om å persistere forsendelse med bestillingsId={}", opprettForsendelseRequest.getBestillingsId());

		Forsendelse forsendelse = forsendelserService.opprettForsendelse(opprettForsendelseRequest);

		log.info("opprettForsendelse har persistert forsendelse med bestillingsId={}. ForsendelseId={}", opprettForsendelseRequest
				.getBestillingsId(), forsendelse.getForsendelseId());

		return ResponseEntity.ok(forsendelse);
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