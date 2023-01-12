package no.nav.dokdistadmin.administrerforsendelse;

import lombok.extern.slf4j.Slf4j;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Protected
@RestController
@RequestMapping("/rest/v1/administrerforsendelse")
public class AdministrerForsendelseController {

	private final EkspederteForsendelserService ekspederteForsendelserService;

	public AdministrerForsendelseController(EkspederteForsendelserService ekspederteForsendelserService) {
		this.ekspederteForsendelserService = ekspederteForsendelserService;
	}

	// TODO: Vurder å endre fra body til pathParam eller queryParam
	@GetMapping("/hentekspederteforsendelser")
	public ResponseEntity<HentEkspederteForsendelserResponse> hentEkspederteForsendelser(@RequestBody @Valid HentEkspederteForsendelserRequest hentEkspederteForsendelserRequest) {
		log.info("rdist001 har mottatt kall om å hente ekspederte forsendelser");

		HentEkspederteForsendelserResponse ekspederteForsendelser = ekspederteForsendelserService.hentEkspederteForsendelser(hentEkspederteForsendelserRequest.getMaksForsendelser());
		log.info("rdist001 har hentet {} ekspederte forsendelser.", ekspederteForsendelser.forsendelser().size());

		return ekspederteForsendelser.forsendelser().isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(ekspederteForsendelser);
	}

	@PutMapping("/avstemekspederteforsendelser")
	public ResponseEntity<Void> avstemEkspederteForsendelser(@RequestBody @Valid AvstemEkspederteForsendelserRequest forsendelserRequest) {
		log.info("rdist001 har mottatt kall om å avstemme {} ekspederte forsendelser", forsendelserRequest.getForsendelser().size());

		ekspederteForsendelserService.avstemEkspederteForsendelser(forsendelserRequest);
		log.info("rdist001 har avstemt ekspederte forsendelser");

		return ResponseEntity.ok().build();
	}

	@ResponseStatus(BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> invalidInputHandler(MethodArgumentNotValidException exception) {
		var message = exception.getAllErrors().get(0).getDefaultMessage();
		return ResponseEntity.badRequest().body(message);
	}

}
