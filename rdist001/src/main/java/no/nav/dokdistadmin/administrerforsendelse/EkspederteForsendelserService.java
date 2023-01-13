package no.nav.dokdistadmin.administrerforsendelse;

import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.administrerforsendelse.AvstemEkspederteForsendelserRequest.Forsendelse;
import no.nav.dokdistadmin.administrerforsendelse.map.HentEkspederteForsendelserMapper;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import org.slf4j.MDC;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class EkspederteForsendelserService {
	private static final int BATCH_SIZE = 1000;
	private static final int MAX_FORSENDELSER = 10000;

	private final DokumentInfoRepository dokumentInfoRepository;
	private final DokumentDistribusjonRepository dokumentDistribusjonRepository;
	private final HentEkspederteForsendelserMapper mapper;

	public EkspederteForsendelserService(
			DokumentInfoRepository dokumentInfoRepository,
			DokumentDistribusjonRepository dokumentDistribusjonRepository) {
		this.dokumentInfoRepository = dokumentInfoRepository;
		this.dokumentDistribusjonRepository = dokumentDistribusjonRepository;
		this.mapper = new HentEkspederteForsendelserMapper();
	}

	public HentEkspederteForsendelserResponse hentEkspederteForsendelser(int maksForsendelser) {

		var pageRequest = PageRequest.of(0, maksForsendelser == 0 ? MAX_FORSENDELSER : maksForsendelser);

		return mapper.map(dokumentInfoRepository.findEkspedertDokumentInfo(pageRequest).getContent());
	}

	@Transactional
	public void avstemEkspederteForsendelser(AvstemEkspederteForsendelserRequest avstemEkspederteForsendelserRequest) {

		List<Long> forsendelseIds = avstemEkspederteForsendelserRequest.getForsendelser().stream()
				.map(Forsendelse::getForsendelseId)
				.toList();

		//Del opp liste med forsendelseIder i partisjoner med størrelse lik BATCH_SIZE
		Map<Integer, List<Long>> forsendelseIdPartisjoner = IntStream.range(0, forsendelseIds.size()).boxed()
				.collect(groupingBy(partition -> (partition / BATCH_SIZE), mapping(forsendelseIds::get, toList())));

		forsendelseIdPartisjoner.forEach((key, value) -> {
			dokumentDistribusjonRepository.updateDokumentInfosAvstemtArkivDato(value, MDC.get(USER_ID));
			log.info("avstemEkspederteForsendelser har oppdatert avstemtArkivDato på totalt {} forsendelser", value.size());
		});
	}

}
