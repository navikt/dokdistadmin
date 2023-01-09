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
		return new ResponseEntity<>(message, BAD_REQUEST);
	}


//	//TODO sjekk at regex dekker null og blank
//	@GetMapping("/administrerforsendelse/{forsendelseId}")
//	public HentForsendelseResponse getForsendelse(@PathVariable("forsendelseId") long forsendelseId) {
//		log.info("rdist001 har mottatt kall om å hente forsendelse med forsendelseId={}", forsendelseId);
//		try {
//			var response = administrerForsendelseService.hentForsendelse(forsendelseId);
//			log.info("rdist001 har hentet forsendelse med forsendelseId={}", forsendelseId);
//			return response;
//		} catch (ForsendelseIkkeFunnetException e) {
//			log.error(HENT_FORSENDELSE_ERROR_MSG, forsendelseId, e.getMessage());
//			throw e;
//		} catch (KanIkkeBestemmeDokumentRekkefolgeException e) {
//			log.info(HENT_FORSENDELSE_ERROR_MSG, forsendelseId, e.getMessage());
//			throw e;
//		} catch (Exception e) {
//			log.error(HENT_FORSENDELSE_ERROR_MSG, forsendelseId, e.getMessage());
//			throw e;
//		}
//	}

//	@POST
//	@Path("/administrerforsendelse")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@SecuredWithBasicAuth
//	public Response persisterForsendelse(PersisterForsendelseRequestTo persisterForsendelseRequestTo) {
//		log.info("rdist001 har mottatt kall om å persistere forsendelse med bestillingsId={}", persisterForsendelseRequestTo.getBestillingsId());
//		try {
//			PersisterForsendelseResponseTo responseTo = administrerForsendelseService.persisterForsendelse(persisterForsendelseRequestTo);
//			log.info("rdist001 har persistert forsendelse med bestillingsId={}. ForsendelseId={}", persisterForsendelseRequestTo
//					.getBestillingsId(), responseTo.getForsendelseId());
//			return Response.ok(responseTo).build();
//		} catch (IllegalArgumentException e) {
//			log.error(PERSISTER_FORSENDELSE_ERROR_MSG, persisterForsendelseRequestTo.getBestillingsId(), e.getMessage());
//			return createErrorResponse(Response.Status.BAD_REQUEST, e);
//		} catch (ForsendelseAlleredePersistertException e) {
//			PersisterForsendelseResponseTo responseTo = PersisterForsendelseResponseTo.builder().forsendelseId(e.getForsendelseId()).build();
//			return Response.ok(responseTo).build();
//		} catch (Exception e) {
//			log.warn(PERSISTER_FORSENDELSE_ERROR_MSG, persisterForsendelseRequestTo.getBestillingsId(), e.getMessage());
//			return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e);
//		}
//	}
//
//	@PUT
//	@Path("/administrerforsendelse/feilregistrerforsendelse")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@SecuredWithBasicAuth
//	public Response feilRegistrerForsendelse(FeilRegistrerForsendelseRequest feilRegistrerForsendelseRequest) {
//		try {
//			feilRegistrerForsendelseService.persisterFeilRegistrerForsendelse(feilRegistrerForsendelseRequest);
//			return Response.ok().build();
//		} catch (IllegalFeilRegistrerForsendelseException | ForsendelseIkkeFunnetException |
//				 IllegalArgumentException e) {
//			log.error(OPPDATER_FEILREGISTRERFORSENDELSE_ERROR_MSG, feilRegistrerForsendelseRequest.getForsendelseId(), e.getMessage());
//			return createErrorResponse(Response.Status.BAD_REQUEST, e);
//		} catch (Exception e) {
//			log.error(OPPDATER_FEILREGISTRERFORSENDELSE_ERROR_MSG, feilRegistrerForsendelseRequest.getForsendelseId(), e.getMessage());
//			return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e);
//		}
//	}
//
//	@PUT
//	@Path("/administrerforsendelse/oppdaterdigitalinfo")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@SecuredWithBasicAuth
//	public Response oppdaterForsendelseStatusOgDigitalPostInfo(OppdaterForsendelseRequestTo digitalPostAdresseRequest) {
//		log.info(format("rdist001 har mottatt kall om å oppdatere forsendelseStatus på forsendelse med forsendelseId=%s", digitalPostAdresseRequest.getForsendelseId()));
//		try {
//			administrerForsendelseService.oppdaterForsendelseStatusOgDigitalAdresse(digitalPostAdresseRequest);
//			log.info(format("rdist001 har oppdatert forsendelseId=%s til forsendelseStatus=%s og satt digitalLeverandoeradresse, digitalPostkasseadresse",
//					digitalPostAdresseRequest.getForsendelseId(), digitalPostAdresseRequest.getForsendelseStatus()));
//			return Response.ok().build();
//		} catch (DokumentStatusErAlleredeSattException e) {
//			log.warn(OPPDATER_FORSENDELSE_STATUS_ERROR_MSG, digitalPostAdresseRequest.getForsendelseId(), e.getMessage());
//			return Response.ok().build();
//		} catch (IllegalArgumentException | DigitalPostkasseAdresseErIkkeSattException |
//				 UlovligStatusOvergangException e) {
//			log.error(OPPDATER_FORSENDELSE_STATUS_ERROR_MSG, digitalPostAdresseRequest.getForsendelseId(), e.getMessage());
//			return createErrorResponse(Response.Status.BAD_REQUEST, e);
//		} catch (ForsendelseIkkeFunnetException e) {
//			log.error(OPPDATER_FORSENDELSE_STATUS_ERROR_MSG, digitalPostAdresseRequest.getForsendelseId(), e.getMessage());
//			return createErrorResponse(Response.Status.NOT_FOUND, e);
//		} catch (IkkeSammenfallendeStatusException e) {
//			log.error(OPPDATER_FORSENDELSE_STATUS_ERROR_MSG, digitalPostAdresseRequest.getForsendelseId(), e.getMessage());
//			return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e);
//		} catch (Exception e) {
//			log.error(OPPDATER_FORSENDELSE_STATUS_ERROR_MSG, digitalPostAdresseRequest.getForsendelseId(), e.getMessage());
//			return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e);
//		}
//	}
//
//	@PUT
//	@Path("/administrerforsendelse/oppdatervarselinfo")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@SecuredWithBasicAuth
//	public Response oppdaterVarselInfo(OppdaterVarselInfoRequestTo oppdaterVarselInfoRequest) {
//		log.info(format("rdist001 har mottatt kall om å oppdatere varselinfo på forsendelse med forsendelseId=%s", oppdaterVarselInfoRequest.getForsendelseId()));
//		try {
//			administrerForsendelseService.oppdaterVarselInfo(oppdaterVarselInfoRequest);
//			log.info(format("rdist001 har oppdatert varselinfo for forsendelseId=%s", oppdaterVarselInfoRequest.getForsendelseId()));
//			return Response.ok().build();
//		} catch (ForsendelseIkkeFunnetException e) {
//			log.error(OPPDATER_VARSELINFO_STATUS_ERROR_MSG, oppdaterVarselInfoRequest.getForsendelseId(), e.getMessage());
//			return createErrorResponse(Response.Status.NOT_FOUND, e);
//		} catch (FeilKanalException e) {
//			log.error(OPPDATER_VARSELINFO_STATUS_ERROR_MSG, oppdaterVarselInfoRequest.getForsendelseId(), e.getMessage());
//			return createErrorResponse(Response.Status.BAD_REQUEST, e);
//		} catch (Exception e) {
//			log.error(OPPDATER_VARSELINFO_STATUS_ERROR_MSG, oppdaterVarselInfoRequest.getForsendelseId(), e.getMessage());
//			return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e);
//		}
//	}
//
//	@PUT
//	@Path("/administrerforsendelse")
//	@SecuredWithBasicAuth
//	public Response oppdaterForsendelseStatusOgKonversasjonId(@QueryParam("forsendelseId") String forsendelseId,
//															  @QueryParam("forsendelseStatus") String forsendelseStatus,
//															  @QueryParam("konversasjonsId") String konversasjonId,
//															  @QueryParam("varselStatus") String varselStatus) {
//		log.info(format("rdist001 har mottatt kall om å oppdatere forsendelseStatus på forsendelse med forsendelseId=%s", forsendelseId));
//		try {
//			administrerForsendelseService.oppdaterForsendelseStatusOgKonversasjonId(forsendelseId, forsendelseStatus, konversasjonId, varselStatus);
//			log.info(format("rdist001 har oppdatert forsendelseStatus til forsendelseStatus=%s på forsendelse med forsendelseId=%s og varselStatus=%s", forsendelseStatus, forsendelseId, varselStatus));
//			return Response.ok().build();
//		} catch (DokumentStatusErAlleredeSattException e) {
//			log.warn(OPPDATER_FORSENDELSE_STATUS_ERROR_MSG, forsendelseId, e.getMessage());
//			return Response.ok().build();
//		} catch (IllegalArgumentException | DigitalPostkasseAdresseErIkkeSattException |
//				 UlovligStatusOvergangException e) {
//			log.error(OPPDATER_FORSENDELSE_STATUS_ERROR_MSG, forsendelseId, e.getMessage());
//			return createErrorResponse(Response.Status.BAD_REQUEST, e);
//		} catch (ForsendelseIkkeFunnetException e) {
//			log.error(OPPDATER_FORSENDELSE_STATUS_ERROR_MSG, forsendelseId, e.getMessage());
//			return createErrorResponse(Response.Status.NOT_FOUND, e);
//		} catch (IkkeSammenfallendeStatusException e) {
//			log.error(OPPDATER_FORSENDELSE_STATUS_ERROR_MSG, forsendelseId, e.getMessage());
//			return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e);
//		} catch (Exception e) {
//			log.error(OPPDATER_FORSENDELSE_STATUS_ERROR_MSG, forsendelseId, e.getMessage());
//			return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e);
//		}
//	}
//
//
//	@GET
//	@Path("/administrerforsendelse/hentpostdestinasjon/{landkode}")
//	@SecuredWithBasicAuth
//	public Response getPostDestination(@PathParam("landkode") String landKode) {
//		log.info("rdist001 har mottatt kall om å hente postDestinasjon for landkode={}", landKode);
//		try {
//			HentPostDestinasjonResponseTo postDestinasjon = administrerForsendelseService.findPostDestinasjon(landKode);
//			log.info("rdist001 har hentet postDestinasjon={} for landkode={},", postDestinasjon, landKode);
//			return Response.ok(postDestinasjon).build();
//		} catch (IllegalArgumentException e) {
//			log.error(HENT_POSTDESTINASJON_ERROR_MSG, landKode, e.getMessage());
//			return createErrorResponse(Response.Status.BAD_REQUEST, e);
//		} catch (PostDestinasjonIkkeFunnetException e) {
//			log.error(HENT_POSTDESTINASJON_ERROR_MSG, landKode, e.getMessage());
//			return createErrorResponse(Response.Status.NOT_FOUND, e);
//		} catch (Exception e) {
//			log.error(HENT_POSTDESTINASJON_ERROR_MSG, landKode, e.getMessage());
//			return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e);
//		}
//	}
//
//	@GET
//	@Path("/administrerforsendelse/henteformidlingforsendelser")
//	@SecuredWithBasicAuth
//	public Response getEformidlingForsendelser(@Context UriInfo uriInfo) {
//		log.info("rdist001 har mottatt kall om å hente eformidlingforsendelser");
//		try {
//			return hentEformidlingForsendelser(uriInfo);
//		} catch (Exception e) {
//			log.error(HENT_EFORMIDLINGFORSENDELSER_ERROR_MSG, e.getMessage());
//			return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e);
//		}
//	}
//
//	@GET
//	@Path("/administrerforsendelse/henteuekspederforsendelse/{distribusjonkanal}/{antalltimer}")
//	@SecuredWithBasicAuth
//	public Response hentForsendelseKvitteringIkkeMottatt(@PathParam("distribusjonkanal") String distribusjonKanal,
//														 @PathParam("antalltimer") Long antallTimer) {
//
//		try {
//			log.info("rdist001 har mottatt kall om å hente forsendelser som ikke har mottatt kvittering fra dokumentdistribusjon. distribusjonKanal={}, antallTimer={}",
//					distribusjonKanal, antallTimer);
//			List<HentForsendelseKvitteringIkkeMottattResponseTo> hentForsendelseAvstemminger = forsendelseKvitteringIkkeMottatt
//					.hentForsendelserKvitteringIkkeMottatt(distribusjonKanal, antallTimer);
//			log.info("rdist001 har hentet forsendelse som ikke har mottatt kvittering med distribusjonKanal={}, antallTimer={}", distribusjonKanal, antallTimer);
//			return hentForsendelseAvstemminger.isEmpty() ? Response.status(Response.Status.NO_CONTENT).build()
//					: Response.ok(hentForsendelseAvstemminger).build();
//
//		} catch (Exception e) {
//			log.error(HENT_UEKSPEDER_FORSENDELSE_STATUS_ERROR_MSG, e.getStackTrace(), e);
//			return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e);
//		}
//	}
//
//
//
//	@PUT
//	@Path("/administrerforsendelse/avstemforsendelser")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@SecuredWithBasicAuth
//	public Response oppdaterForsendelserAvstemtInfo(OppdaterForsendelserAvstemtInfo forsendelserAvstemtInfo) {
//
//		log.info(format("rdist001 har mottatt kall om å oppdatere dokumentInfo avstemtDato og referanse med avstemtReferanse=%s ", forsendelserAvstemtInfo.getAvstemtReferanse()));
//		List<OppdaterForsendelserAvstemtInfo.Forsendelse> forsendelser = forsendelserAvstemtInfo.getForsendelser();
//		try {
//			forsendelseKvitteringIkkeMottatt.oppdaterForsendelserAvstemtInfo(forsendelserAvstemtInfo);
//			log.info("rdist001 har oppdatert dokumentInfo med forsendelser={}", forsendelser);
//			return Response.ok().build();
//		} catch (IllegalArgumentException | ForsendelseIkkeFunnetException e) {
//			log.error(OPPDATER_FORSENDELSER_AVSTEMTINFO_ERROR_MSG, forsendelser, e.getMessage());
//			return createErrorResponse(Response.Status.BAD_REQUEST, e);
//		} catch (Exception e) {
//			log.error(OPPDATER_FORSENDELSER_AVSTEMTINFO_ERROR_MSG, forsendelser, e.getMessage());
//			return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e);
//		}
//
//	}
//
//	@GET
//	@Path("/administrerforsendelse/finnforsendelse")
//	@SecuredWithBasicAuth
//	public Response finnForsendelse(@Context UriInfo uriInfo) {
//
//		try {
//			FinnForsendelseRequestTo forsendelseRequestTo = getForsendelseRequestFromUriInfo(uriInfo);
//			log.info("rdist001 har mottatt kall om å finne forsendelseId med oppslagsNoekkel:{} og  verdi: {}", forsendelseRequestTo.getOppslagsNoekkel(), forsendelseRequestTo.getVerdi());
//			FinnForsendelseResponseTo finnForsendelseResponseTo = administrerForsendelseService.findForsendelse(forsendelseRequestTo.getOppslagsNoekkel(), forsendelseRequestTo.getVerdi());
//			log.info("rdist001 har mottatt om å finne forsendelseId med oppslagsNoekkel:{} og  verdi: {}", forsendelseRequestTo.getOppslagsNoekkel(), forsendelseRequestTo.getVerdi());
//			return finnForsendelseResponseTo == null ? Response.status(Response.Status.NO_CONTENT).build()
//					: Response.ok(finnForsendelseResponseTo).build();
//		} catch (IllegalArgumentException | IllegalOppslagsNoekkelException e) {
//			log.error(FINN_FORSENDELSE_ERROR_MSG, e.getMessage());
//			return createErrorResponse(Response.Status.BAD_REQUEST, e);
//		} catch (DuplicateResponseException e) {
//			log.error("Feilet: Fant ingen unikt konversasjonsId feilmelding:{}", e.getMessage());
//			return createErrorResponse(Response.Status.CONFLICT, e);
//		} catch (Exception e) {
//			log.error(FINN_FORSENDELSE_ERROR_MSG, e.getMessage());
//			return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e);
//		}
//	}
//
//	@GET
//	@SecuredWithBasicAuth
//	@Path("/administrerforsendelse/ping")
//	public Response ping() {
//		return Response.ok().build();
//	}
//
//	private Response createErrorResponse(Response.StatusType statusType, Exception e) {
//		String uriInfoPath = uriInfo == null ? "UriInfo not found from context." : uriInfo.getPath();
//		return Response.status(statusType).entity(new ErrorResponse(statusType, e.getMessage(), uriInfoPath)).build();
//	}
//
//	private FinnForsendelseRequestTo getForsendelseRequestFromUriInfo(@Context UriInfo uriInfo) {
//
//		return uriInfo.getQueryParameters().entrySet().stream()
//				.filter(entry -> entry.getValue() != null || !isBlank(entry.getKey()))
//				.map(entry -> FinnForsendelseRequestTo.builder()
//						.oppslagsNoekkel(OppslagsNoekkel.valueOf(entry.getKey().toUpperCase()))
//						.verdi(entry.getValue().stream().findAny().orElse(null))
//						.build())
//				.findAny()
//				.orElseThrow(() -> new IllegalArgumentException("Ugyldig input: Feltet kan ikke være null"));
//	}
//
//	private Response hentEformidlingForsendelser(@Context UriInfo uriInfo) {
//		return uriInfo.getQueryParameters().entrySet().stream()
//				.filter(entry -> entry.getValue() != null || !isBlank(entry.getKey()))
//				.map(entry -> {
//					if (!DISTRIBUSJONSKANAL.equals(entry.getKey())) {
//						return Response.status(Response.Status.BAD_REQUEST).entity("Ugyldig noekkel: OppslagsNoekkel må være distribusjonKanal")
//								.build();
//					}
//					String distribusjonKanal = entry.getValue().stream().findAny().orElse(null);
//
//					return isBlank(distribusjonKanal) ? Response.status(Response.Status.BAD_REQUEST).entity("Ugyldig input: Spørringsverdien kan ikke være null").build() :
//							Response.ok(administrerForsendelseService.hentEformidlingForsendelser(distribusjonKanal)).build();
//				})
//				.findAny()
//				.orElseThrow(() -> new IllegalArgumentException("Ugyldig input: Feltet kan ikke være null"));
//	}

}
