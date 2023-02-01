package no.nav.dokdistadmin.administrerforsendelse;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.administrerforsendelse.map.HentEkspederteForsendelserMapper;
import no.nav.dokdistadmin.administrerforsendelse.map.HentUekspederteForsendelserMapper;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
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
	public void avstemEkspederteForsendelser(AvstemEkspederteForsendelserRequest avstemEkspederteForsendelserRequest) {
		var forsendelser = avstemEkspederteForsendelserRequest.getForsendelser().stream()
				.map(Forsendelse::getForsendelseId)
				.toList();

		Collection<List<Long>> forsendelseIdPartisjoner = partitionList(forsendelser);
		forsendelseIdPartisjoner.forEach(partition -> {
			var antallOppdaterteForsendelser = dokumentDistribusjonRepository.updateDokumentInfosAvstemtArkivDato(partition, MDC.get(USER_ID));
			log.info("avstemEkspederteForsendelser har oppdatert avstemtArkivDato på {} forsendelser", antallOppdaterteForsendelser);
		});
	}

	@Transactional
	public void avstemForsendelser(AvstemForsendelserRequest avstemForsendelserRequest) {
		var avstemtReferanse = avstemForsendelserRequest.getAvstemtReferanse();
		var forsendelser = avstemForsendelserRequest.getForsendelser().stream()
				.map(Forsendelse::getForsendelseId)
				.toList();

		Collection<List<Long>> forsendelseIdPartisjoner = partitionList(forsendelser);
		forsendelseIdPartisjoner.forEach(partition -> {
			var antallOppdaterteForsendelser = dokumentInfoRepository.updateAvstemtReferanseAndAvstemtDatoForIdIn(avstemtReferanse, partition, MDC.get(USER_ID));
			log.info("avstemForsendelser har oppdatert avstemtReferanse og avstemtDato på {} forsendelser", antallOppdaterteForsendelser);
		});
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
}
