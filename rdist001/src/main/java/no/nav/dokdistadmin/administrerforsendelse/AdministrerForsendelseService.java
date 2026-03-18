package no.nav.dokdistadmin.administrerforsendelse;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.administrerforsendelse.eformidlingforsendelser.HentEformidlingforsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.eformidlingforsendelser.HentEformidlingforsendelserResponseMapper;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.AvstemEkspederteForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.AvstemForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.EkspedertForsendelse;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.HentEkspederteForsendelserMapper;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.HentEkspederteForsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.HentForsendelseResponse;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.HentForsendelseResponseMapper;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequest;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequestMapper;
import no.nav.dokdistadmin.administrerforsendelse.uekspederteforsendelser.HentUekspederteForsendelserMapper;
import no.nav.dokdistadmin.administrerforsendelse.uekspederteforsendelser.HentUekspederteForsendelserResponse;
import no.nav.dokdistadmin.config.DokdistadminProperties;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DistribusjonsTypeKode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.Oppslagsnoekkel;
import no.nav.dokdistadmin.exception.functional.FlereForsendelserFunnetException;
import no.nav.dokdistadmin.exception.functional.ForsendelseIkkeFunnetException;
import no.nav.dokdistadmin.exception.functional.ForsendelseIkkeFunnetInfomeldingException;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import no.nav.dokdistadmin.repository.projections.DokumentInfoIdHolder;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.BEKREFTET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.FEILET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OVERSENDT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.RETURPOSTBEHANDLET;
import static no.nav.dokdistadmin.domain.Oppslagsnoekkel.JOURNALPOSTID;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class AdministrerForsendelseService {
	private static final int BATCH_SIZE = 1000;
	private static final int MAX_FORSENDELSER = 10000;

	private static final int OPPRETTET_ANTALL_DAGER_SIDEN = 60;
	private static final EnumSet<DokumentStatusCode> STATUSER_DER_FORSENDELSE_ER_EKSPEDERT = EnumSet.of(EKSPEDERT, FEILET, RETURPOSTBEHANDLET);
	private static final EnumSet<DokumentStatusCode> STATUSER_DER_FORSENDELSE_IKKE_ER_EKSPEDERT = EnumSet.complementOf(STATUSER_DER_FORSENDELSE_ER_EKSPEDERT);
	private static final EnumSet<DokumentStatusCode> STATUSER_DER_FORSENDELSE_ER_DISTRIBUERT = EnumSet.of(OVERSENDT, BEKREFTET);
	public static final EnumSet<DistribusjonKanalCode> DISTRIBUSJON_KANALER = EnumSet.allOf(DistribusjonKanalCode.class);

	private final DokdistadminProperties dokdistadminProperties;
	private final DokumentInfoRepository dokumentInfoRepository;
	private final DokumentDistribusjonRepository dokumentDistribusjonRepository;
	private final HentUekspederteForsendelserMapper hentUekspederteForsendelserMapper;
	private final HentEformidlingforsendelserResponseMapper hentEformidlingforsendelserResponseMapper;

	public AdministrerForsendelseService(
			DokdistadminProperties dokdistadminProperties,
			DokumentInfoRepository dokumentInfoRepository,
			DokumentDistribusjonRepository dokumentDistribusjonRepository) {
		this.dokdistadminProperties = dokdistadminProperties;
		this.dokumentInfoRepository = dokumentInfoRepository;
		this.dokumentDistribusjonRepository = dokumentDistribusjonRepository;
		this.hentEformidlingforsendelserResponseMapper = new HentEformidlingforsendelserResponseMapper();
		this.hentUekspederteForsendelserMapper = new HentUekspederteForsendelserMapper();
	}

	public HentForsendelseResponse hentForsendelse(Long forsendelseId) {
		DokumentInfo dokumentInfo = dokumentInfoRepository.fetchDokumentInfo(forsendelseId);

		if (dokumentInfo == null) {
			throw new ForsendelseIkkeFunnetException(format("Forsendelse med forsendelseId=%s ikke funnet i dokdistDb", forsendelseId));
		}

		return HentForsendelseResponseMapper.map(dokumentInfo);
	}

	@Transactional
	public Forsendelse opprettForsendelse(OpprettForsendelseRequest persisterForsendelseRequest) {

		var bestillingsId = persisterForsendelseRequest.getBestillingsId();

		if (dokumentInfoRepository.existsByDokumentId(bestillingsId)) {
			log.warn("Forsendelse med bestillingsId={} finnes allerede i databasen til dokdist", bestillingsId);
			var forsendelseId = dokumentInfoRepository.findDokumentInfoByDokumentId(bestillingsId).getDokumentInfoId();
			return new Forsendelse(forsendelseId);
		}

		DistribusjonInfo distribusjonInfo = OpprettForsendelseRequestMapper.mapToDistribusjonInfo(persisterForsendelseRequest, dokdistadminProperties.getModus());

		distribusjonInfo = dokumentDistribusjonRepository.persist(distribusjonInfo);

		var forsendelseId = distribusjonInfo.getDokumentInfos().iterator().next().getDokumentInfoId();

		return new Forsendelse(forsendelseId);
	}

	public HentEkspederteForsendelserResponse hentEkspederteForsendelser(int maksForsendelser) {
		int topN = maksForsendelser == 0 ? MAX_FORSENDELSER : maksForsendelser;
		List<Long> dokumentInfoIds = dokumentInfoRepository.findEkspedertDokumentInfo(topN, DISTRIBUSJON_KANALER);

		var partitioned = partitionList(dokumentInfoIds);

		List<EkspedertForsendelse> result = new ArrayList<>();
		partitioned.forEach(partition -> result.addAll(
						dokumentInfoRepository.fetchEkspedertDokumentInfo(partition).stream()
								.map(HentEkspederteForsendelserMapper::mapForsendelse)
								.toList()
				)
		);

		return new HentEkspederteForsendelserResponse(result);
	}

	@Transactional
	public int avstemEkspederteForsendelser(AvstemEkspederteForsendelserRequest avstemEkspederteForsendelserRequest) {
		var userId = MDC.get(USER_ID);
		var forsendelser = avstemEkspederteForsendelserRequest.forsendelser().stream()
				.map(Forsendelse::getForsendelseId)
				.toList();

		AtomicInteger antallOppdaterteForsendelser = new AtomicInteger();

		Collection<List<Long>> forsendelseIdPartisjoner = partitionList(forsendelser);
		forsendelseIdPartisjoner.forEach(partition ->
				antallOppdaterteForsendelser.addAndGet(dokumentInfoRepository.updateDokumentInfosAvstemtArkivDato(partition, userId))
		);

		return antallOppdaterteForsendelser.get();
	}

	@Transactional
	public int avstemForsendelser(AvstemForsendelserRequest avstemForsendelserRequest) {
		var userId = MDC.get(USER_ID);
		var avstemtReferanse = avstemForsendelserRequest.avstemtReferanse();
		var forsendelser = avstemForsendelserRequest.forsendelser().stream()
				.map(Forsendelse::getForsendelseId)
				.toList();

		AtomicInteger antallOppdaterteForsendelser = new AtomicInteger();

		Collection<List<Long>> forsendelseIdPartisjoner = partitionList(forsendelser);
		forsendelseIdPartisjoner.forEach(partition ->
				antallOppdaterteForsendelser.addAndGet(dokumentInfoRepository.updateAvstemtReferanseAndAvstemtDatoForIdIn(avstemtReferanse, partition, userId))
		);

		return antallOppdaterteForsendelser.get();
	}

	// Del opp liste med forsendelseIder i partisjoner med størrelse lik BATCH_SIZE
	Collection<List<Long>> partitionList(final List<Long> list) {
		return IntStream.range(0, list.size()).boxed()
				.collect(groupingBy(partition -> (partition / BATCH_SIZE), mapping(list::get, toList())))
				.values();
	}

	public HentUekspederteForsendelserResponse hentUekspederteForsendelser(DistribusjonKanalCode distribusjonkanalCode, Long antallTimer) {
		var opprettetEtter = LocalDateTime.now().minusDays(OPPRETTET_ANTALL_DAGER_SIDEN);
		var opprettetFoer = LocalDateTime.now().minusHours(antallTimer);

		var start = Instant.now();

		List<DistribusjonInfo> distribusjonInfoList = dokumentDistribusjonRepository.findDistribusjonInfoByDokumentStatusAndDistribusjonKanal(
				STATUSER_DER_FORSENDELSE_IKKE_ER_EKSPEDERT, distribusjonkanalCode, opprettetEtter, opprettetFoer);

		var varighet = Duration.between(start, Instant.now()).toSeconds();

		log.info("hentUekspederteForsendelser hentet {} uekspederte forsendelser på {} sekunder", distribusjonInfoList.size(), varighet);

		return hentUekspederteForsendelserMapper.map(distribusjonInfoList);
	}

	public HentEformidlingforsendelserResponse hentEformidlingForsendelser(DistribusjonKanalCode distribusjonKanal) {

		List<DokumentInfo> dokumentInfoList = dokumentInfoRepository.findDokumentInfoByDokumentStatusAndDistribusjonKanal(
				STATUSER_DER_FORSENDELSE_ER_DISTRIBUERT, distribusjonKanal);

		return hentEformidlingforsendelserResponseMapper.map(dokumentInfoList);
	}

	public Forsendelse finnForsendelse(Oppslagsnoekkel oppslagsnoekkel, String verdi) {

		List<DokumentInfoIdHolder> forsendelser = switch (oppslagsnoekkel) {
			case KONVERSASJONSID -> dokumentInfoRepository.findIdsByKonversasjonId(verdi);
			case BESTILLINGSID -> dokumentInfoRepository.findIdsByDokumentId(verdi);
			case JOURNALPOSTID -> {
				var forsendelse = dokumentInfoRepository.findTopByArkivkodeOrderByDokumentInfoIdDesc(verdi);
				yield forsendelse != null ? singletonList(forsendelse) : emptyList();
			}
		};

		if (forsendelser.isEmpty()) {
			if (JOURNALPOSTID == oppslagsnoekkel) {
				throw new ForsendelseIkkeFunnetInfomeldingException(format("finnForsendelse fant ikke forsendelse med %s=%s. Dette er forventet for distribusjoner opprettet av bdist001",
						oppslagsnoekkel.value,
						verdi));
			} else {
				throw new ForsendelseIkkeFunnetException(format("finnForsendelse fant ikke forsendelse med %s=%s",
						oppslagsnoekkel.value,
						verdi));
			}
		}

		if (forsendelser.size() > 1) {
			throw new FlereForsendelserFunnetException(format("finnForsendelse fant flere enn en forsendelse med %s=%s",
					oppslagsnoekkel.value,
					verdi));
		}

		return new Forsendelse(forsendelser.getFirst().getDokumentInfoId());
	}

	public List<HentForsendelseResponse> hentForsendelser(List<Long> journalpostliste,
														  List<DistribusjonsTypeKode> distribusjonstyper,
														  List<DokumentStatusCode> dokumentstatus,
														  boolean inkluderAvstemte,
														  Optional<DistribusjonKanalCode> distribusjonskanal) {
		return dokumentInfoRepository.fetchDokumentInfoList(journalpostliste, distribusjonstyper, dokumentstatus, inkluderAvstemte, distribusjonskanal)
				.map(HentForsendelseResponseMapper::map)
				.toList();
	}
}
