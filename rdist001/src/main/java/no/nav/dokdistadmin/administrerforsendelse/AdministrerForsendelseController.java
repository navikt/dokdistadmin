package no.nav.dokdistadmin.administrerforsendelse;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.administrerforsendelse.eformidlingforsendelser.HentEformidlingforsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.AvstemEkspederteForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.AvstemForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.HentEkspederteForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.HentEkspederteForsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.feilregistrerforsendelse.FeilregistrerForsendelseRequest;
import no.nav.dokdistadmin.administrerforsendelse.finnforsendelse.FinnForsendelseRequest;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.HentForsendelseResponse;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequest;
import no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser.OppdaterForsendelseRequest;
import no.nav.dokdistadmin.administrerforsendelse.post.HentPostdestinasjonResponse;
import no.nav.dokdistadmin.administrerforsendelse.uekspederteforsendelser.HentUekspederteForsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.varselinfo.OppdaterVarselInfoRequest;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.security.token.support.core.api.Protected;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Protected
@RestController
@RequestMapping("/rest/v1/administrerforsendelse")
public class AdministrerForsendelseController {

	private final AdministrerForsendelseService forsendelserService;
	private final VarselInfoService varselInfoService;
	private final OppdaterForsendelseService oppdaterForsendelseService;
	private final PostService postService;
	private final FeilregistrerForsendelseService feilregistrerForsendelseService;

	public AdministrerForsendelseController(AdministrerForsendelseService forsendelserService,
											VarselInfoService varselInfoService,
											OppdaterForsendelseService oppdaterForsendelseService,
											PostService postService,
											FeilregistrerForsendelseService feilregistrerForsendelseService) {
		this.forsendelserService = forsendelserService;
		this.varselInfoService = varselInfoService;
		this.oppdaterForsendelseService = oppdaterForsendelseService;
		this.postService = postService;
		this.feilregistrerForsendelseService = feilregistrerForsendelseService;
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
		log.info("hentForsendelse har mottatt kall om å hente forsendelse med forsendelseId={}", forsendelseId);

		HentForsendelseResponse hentForsendelseResponse = forsendelserService.hentForsendelse(forsendelseId);
		log.info("hentForsendelse har hentet forsendelse med forsendelseId={}", forsendelseId);

		return ResponseEntity.ok(hentForsendelseResponse);
	}

	@GetMapping("/finnforsendelse")
	public ResponseEntity<Forsendelse> finnForsendelse(@RequestBody @Valid FinnForsendelseRequest finnForsendelseRequest) {
		log.info("finnforsendelse har mottatt kall om å finne forsendelse med {}={}",
				finnForsendelseRequest.getOppslagsnoekkel().getValue(),
				finnForsendelseRequest.getVerdi());

		var forsendelse = forsendelserService.finnForsendelse(finnForsendelseRequest);

		log.info("finnforsendelse har funnet forsendelse med forsendelseId={} og {}={}", forsendelse.getForsendelseId(),
				finnForsendelseRequest.getOppslagsnoekkel().getValue(),
				finnForsendelseRequest.getVerdi());

		return ResponseEntity.ok(forsendelse);
	}

	@PutMapping("/oppdaterforsendelse")
	public ResponseEntity<String> oppdaterForsendelse(@RequestBody @Valid @NotNull OppdaterForsendelseRequest oppdaterForsendelseRequest) {
		log.info("oppdaterForsendelse har mottatt kall om å oppdatere forsendelse med forsendelseId={}",
				oppdaterForsendelseRequest.getForsendelseId());

		oppdaterForsendelseService.oppdaterForsendelse(oppdaterForsendelseRequest);
		log.info("oppdaterForsendelse har oppdatert forsendelse med forsendelseId={}", oppdaterForsendelseRequest.getForsendelseId());

		return ResponseEntity.ok().build();
	}

	@PutMapping("/feilregistrerforsendelse")
	public ResponseEntity<Void> feilregistrerForsendelse(@RequestBody @Valid @NotNull FeilregistrerForsendelseRequest feilregistrerForsendelseRequest) {
		log.info("feilregistrerForsendelse har mottatt kall om å feilregistrere forsendelse med forsendelseId={}",
				feilregistrerForsendelseRequest.getForsendelseId());

		feilregistrerForsendelseService.feilregistrerForsendelse(feilregistrerForsendelseRequest);

		log.info("feilregistrerForsendelse har feilregistrert forsendelse med forsendelseId={}",
				feilregistrerForsendelseRequest.getForsendelseId());

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

	@GetMapping("/hentpostdestinasjon/{landkode}")
	public ResponseEntity<HentPostdestinasjonResponse> hentPostdestinasjon(
			@PathVariable("landkode") @NotBlank(message = "landkode må ha en verdi") String landkode) {

		log.info("hentPostdestinasjon har mottatt kall om å hente postdestinasjon for landkode={}", landkode);

		var postdestinasjon = postService.hentPostdestinasjon(landkode);

		log.info("hentPostdestinasjon har hentet postdestinasjon for landkode={}", landkode);

		return ResponseEntity.ok(postdestinasjon);
	}
}