package no.nav.dokdistadmin.administrerforsendelse;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.administrerforsendelse.AvstemEkspederteForsendelserRequest.Forsendelse;
import no.nav.dokdistadmin.administrerforsendelse.map.HentEkspederteForsendelserMapper;
import no.nav.dokdistadmin.administrerforsendelse.map.HentUekspederteForsendelserMapper;
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
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.EKSPEDERT;
import static no.nav.dokdistadmin.domain.DokumentStatusCode.FEILET;
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

	private final DokumentInfoRepository dokumentInfoRepository;
	private final DokumentDistribusjonRepository dokumentDistribusjonRepository;
	private final HentEkspederteForsendelserMapper hentEkspederteForsendelserMapper;
	private final HentUekspederteForsendelserMapper hentUekspederteForsendelserMapper;

	public AdministrerForsendelseService(
			DokumentInfoRepository dokumentInfoRepository,
			DokumentDistribusjonRepository dokumentDistribusjonRepository) {
		this.dokumentInfoRepository = dokumentInfoRepository;
		this.dokumentDistribusjonRepository = dokumentDistribusjonRepository;
		this.hentEkspederteForsendelserMapper = new HentEkspederteForsendelserMapper();
		this.hentUekspederteForsendelserMapper = new HentUekspederteForsendelserMapper();
	}

	public HentEkspederteForsendelserResponse hentEkspederteForsendelser(int maksForsendelser) {
		int topN = maksForsendelser == 0 ? MAX_FORSENDELSER : maksForsendelser;
		List<Long> dokumentInfoIds = dokumentInfoRepository.findEkspedertDokumentInfo(topN);

		if (dokumentInfoIds.size() > BATCH_SIZE) {
			var partitioned = partitionList(dokumentInfoIds);
			List<EkspederteForsendelse> result = new ArrayList<>();
			partitioned.forEach((key, value) -> result.addAll(dokumentInfoRepository.fetchEkspedertDokumentInfo(value)
					.stream().map(hentEkspederteForsendelserMapper::mapForsendelse).toList()));
			return new HentEkspederteForsendelserResponse(result);
		} else {
			List<DokumentInfo> dokumentInfos = dokumentInfoRepository.fetchEkspedertDokumentInfo(dokumentInfoIds);
			return hentEkspederteForsendelserMapper.map(dokumentInfos);
		}
	}

	@Transactional
	public void avstemEkspederteForsendelser(AvstemEkspederteForsendelserRequest avstemEkspederteForsendelserRequest) {
		List<Long> forsendelseIds = avstemEkspederteForsendelserRequest.getForsendelser().stream()
				.map(Forsendelse::getForsendelseId)
				.toList();

		if (forsendelseIds.size() > BATCH_SIZE) {
			Map<Integer, List<Long>> forsendelseIdPartisjoner = partitionList(forsendelseIds);
			forsendelseIdPartisjoner.forEach((key, value) -> {
				dokumentDistribusjonRepository.updateDokumentInfosAvstemtArkivDato(value, MDC.get(USER_ID));
				log.info("avstemEkspederteForsendelser har oppdatert avstemtArkivDato på totalt={} forsendelser", value.size());
			});
		} else {
			dokumentDistribusjonRepository.updateDokumentInfosAvstemtArkivDato(forsendelseIds, MDC.get(USER_ID));
			log.info("avstemEkspederteForsendelser har oppdatert avstemtArkivDato på totalt={} forsendelser", forsendelseIds.size());
		}
	}

	@Transactional
	public void avstemForsendelser(AvstemForsendelserRequest avstemForsendelserRequest) {

		var forsendelser = avstemForsendelserRequest.getForsendelser().stream()
				.map(it -> Long.valueOf(it.getForsendelseId()))
				.toList();
		var avstemtReferanse = avstemForsendelserRequest.getAvstemtReferanse();

		var oppdaterteForsendelser = dokumentInfoRepository.updateAvstemtReferanseAndAvstemtDatoForIdIn(avstemtReferanse, forsendelser, MDC.get(USER_ID));

		log.info("avstemForsendelser har oppdatert {} forsendelser", oppdaterteForsendelser);
	}

	//Del opp liste med forsendelseIder i partisjoner med størrelse lik BATCH_SIZE
	Map<Integer, List<Long>> partitionList(final List<Long> list) {
		return IntStream.range(0, list.size()).boxed()
				.collect(groupingBy(partition -> (partition / BATCH_SIZE), mapping(list::get, toList())));
	}

	public HentUekspederteForsendelserResponse hentUekspederteForsendelser(String distribusjonkanal, Long antallTimer) {
		DistribusjonKanalCode distribusjonkanalCode = DistribusjonKanalCode.fromString(distribusjonkanal);

		var opprettetEtter = LocalDateTime.now().minusDays(OPPRETTET_ANTALL_DAGER_SIDEN);
		var opprettetFoer = LocalDateTime.now().minusHours(antallTimer);

		List<DistribusjonInfo> distribusjonInfoList = dokumentDistribusjonRepository.findDistribusjonInfoByDokumentStatusAndDistribusjonKanal(
				EKSPEDERTSTATUSER, distribusjonkanalCode, opprettetEtter, opprettetFoer);

		return hentUekspederteForsendelserMapper.map(distribusjonInfoList);
	}
}
