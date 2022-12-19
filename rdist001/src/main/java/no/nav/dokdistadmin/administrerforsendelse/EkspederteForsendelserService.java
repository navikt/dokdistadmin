package no.nav.dokdistadmin.administrerforsendelse;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import no.nav.dokdistadmin.administrerforsendelse.AvstemEkspederteForsendelserRequest.Forsendelse;
import no.nav.dokdistadmin.administrerforsendelse.map.HentEkspederteForsendelserMapper;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import org.slf4j.MDC;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;

@Slf4j
@Service
public class EkspederteForsendelserService {
	private static final int MAX_UPDATE_PER_CALL = 1000;

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

	@Transactional(readOnly = true)
	public HentEkspederteForsendelserResponse hentEkspederteForsendelser(int maksForsendelser) {

			List<DokumentInfo> dokumentInfoList = dokumentInfoRepository.findEkspedertDokumentInfo(PageRequest.of(0, maksForsendelser)).getContent();
			// TODO: Sjekk korleis det fungerer når det er 0 treff
			if (dokumentInfoList.isEmpty()) {
				return null;
			}
			return mapper.map(dokumentInfoList);
	}

	@Transactional
	public void avstemEkspederteForsendelser(AvstemEkspederteForsendelserRequest avstemEkspederteForsendelserRequest) {

		List<Long> forsendelseIds = avstemEkspederteForsendelserRequest.forsendelser().stream()
				.filter(Objects::nonNull)
				.map(Forsendelse::forsendelseId)
				.toList();

		if (forsendelseIds.isEmpty()) {
			return;
		}

		List<List<Long>> forsendelserIdsCollection = Lists.partition(forsendelseIds, MAX_UPDATE_PER_CALL);

		forsendelserIdsCollection.forEach(ids -> {
			dokumentDistribusjonRepository.updateDokumentInfosAvstemtArkivDato(ids, MDC.get(USER_ID));
			log.info("avstemEkspederteForsendelser har oppdatert avstemtArkivDato på totalt {} forsendelser", ids.size());
		});
	}

}
