package no.nav.dokdistadmin.administrerforsendelse;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.administrerforsendelse.eformidlingforsendelser.HentEformidlingforsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.eformidlingforsendelser.HentEformidlingforsendelserResponseMapper;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.AvstemEkspederteForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.AvstemForsendelserRequest;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.EkspederteForsendelse;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.HentEkspederteForsendelserMapper;
import no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.HentEkspederteForsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.uekspederteforsendelser.HentUekspederteForsendelserMapper;
import no.nav.dokdistadmin.administrerforsendelse.uekspederteforsendelser.HentUekspederteForsendelserResponse;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.BEKREFTET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.FEILET;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.OVERSENDT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.RETURPOSTBEHANDLET;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class AdministrerForsendelseService {
	private static final int BATCH_SIZE = 1000;
	private static final int MAX_FORSENDELSER = 10000;

	private static final int OPPRETTET_ANTALL_DAGER_SIDEN = 60;
	private static final EnumSet<DokumentStatusCode> EKSPEDERTSTATUSER = EnumSet.of(EKSPEDERT, FEILET, RETURPOSTBEHANDLET);
	private static final EnumSet<DokumentStatusCode> EFORMIDLINGSTATUSER = EnumSet.of(OVERSENDT, BEKREFTET);

	private final DokumentInfoRepository dokumentInfoRepository;
	private final DokumentDistribusjonRepository dokumentDistribusjonRepository;
	private final HentEkspederteForsendelserMapper hentEkspederteForsendelserMapper;
	private final HentUekspederteForsendelserMapper hentUekspederteForsendelserMapper;
	private final HentEformidlingforsendelserResponseMapper hentEformidlingforsendelserResponseMapper;

	public AdministrerForsendelseService(
			DokumentInfoRepository dokumentInfoRepository,
			DokumentDistribusjonRepository dokumentDistribusjonRepository) {
		this.dokumentInfoRepository = dokumentInfoRepository;
		this.dokumentDistribusjonRepository = dokumentDistribusjonRepository;
		this.hentEformidlingforsendelserResponseMapper = new HentEformidlingforsendelserResponseMapper();
		this.hentEkspederteForsendelserMapper = new HentEkspederteForsendelserMapper();
		this.hentUekspederteForsendelserMapper = new HentUekspederteForsendelserMapper();
	}

	public HentEkspederteForsendelserResponse hentEkspederteForsendelser(int maksForsendelser) {
		int topN = maksForsendelser == 0 ? MAX_FORSENDELSER : maksForsendelser;
		List<Long> dokumentInfoIds = dokumentInfoRepository.findEkspedertDokumentInfo(topN);

		var partitioned = partitionList(dokumentInfoIds);

		List<EkspederteForsendelse> result = new ArrayList<>();
		partitioned.forEach(partition -> result.addAll(
						dokumentInfoRepository.fetchEkspedertDokumentInfo(partition).stream()
								.map(hentEkspederteForsendelserMapper::mapForsendelse)
								.toList()
				)
		);

		return new HentEkspederteForsendelserResponse(result);
	}

	@Transactional
	public int avstemEkspederteForsendelser(AvstemEkspederteForsendelserRequest avstemEkspederteForsendelserRequest) {
		var userId = MDC.get(USER_ID);
		var forsendelser = avstemEkspederteForsendelserRequest.getForsendelser().stream()
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
		var avstemtReferanse = avstemForsendelserRequest.getAvstemtReferanse();
		var forsendelser = avstemForsendelserRequest.getForsendelser().stream()
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

	public HentUekspederteForsendelserResponse hentUekspederteForsendelser(String distribusjonkanal, Long antallTimer) {
		DistribusjonKanalCode distribusjonkanalCode = DistribusjonKanalCode.fromString(distribusjonkanal);

		var opprettetEtter = LocalDateTime.now().minusDays(OPPRETTET_ANTALL_DAGER_SIDEN);
		var opprettetFoer = LocalDateTime.now().minusHours(antallTimer);

		List<DistribusjonInfo> distribusjonInfoList = dokumentDistribusjonRepository.findDistribusjonInfoByDokumentStatusAndDistribusjonKanal(
				EKSPEDERTSTATUSER, distribusjonkanalCode, opprettetEtter, opprettetFoer);

		return hentUekspederteForsendelserMapper.map(distribusjonInfoList);
	}

	public HentEformidlingforsendelserResponse hentEformidlingForsendelser(DistribusjonKanalCode distribusjonKanal) {

		List<DokumentInfo> dokumentInfoList = dokumentInfoRepository.findDokumentInfoByDokumentStatusAndDistribusjonKanal(
				EFORMIDLINGSTATUSER, distribusjonKanal);

		return hentEformidlingforsendelserResponseMapper.map(dokumentInfoList);
	}

}
