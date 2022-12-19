package no.nav.dokdistadmin.administrerforsendelse;

import no.nav.dokdistadmin.administrerforsendelse.AvstemEkspederteForsendelserRequest.Forsendelse;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.stream.LongStream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.createEkspederteForsendelser;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EkspederteForsendelserServiceTest {

	private static final int MAX_FORSENDELSER = 2;
	private static final int MAX_UPDATE_PER_CALL = 1000;
	private static final Long FORSENDELSE_ID = 1L;

	@Mock
	DokumentInfoRepository dokumentInfoRepository;

	@Mock
	DokumentDistribusjonRepository dokumentDistribusjonRepository;

	@InjectMocks
	EkspederteForsendelserService ekspederteForsendelserService;

	@BeforeEach
	void setup() {
		MDC.put(USER_ID, "testUser");
	}

	@Test
	void shouldHentEkspederteForsendelser() {
		PageImpl<DokumentInfo> page = new PageImpl<>(createEkspederteForsendelser());

		Mockito.when(dokumentInfoRepository.findEkspedertDokumentInfo(PageRequest.of(0, MAX_FORSENDELSER)))
						.thenReturn(page);

		var result = ekspederteForsendelserService.hentEkspederteForsendelser(MAX_FORSENDELSER);

		assertNotNull(result);
	}

	@Test
	void shouldReturnNullWhenNoEkspedertDokumentInfoFound() {
		PageImpl<DokumentInfo> page = new PageImpl<>(emptyList());

		Mockito.when(dokumentInfoRepository.findEkspedertDokumentInfo(PageRequest.of(0, MAX_FORSENDELSER)))
				.thenReturn(page);

		var result = ekspederteForsendelserService.hentEkspederteForsendelser(MAX_FORSENDELSER);

		assertNull(result);
	}

	@Test
	void shouldAvstemEkspederteForsendelser() {
		var forsendelse = new Forsendelse(FORSENDELSE_ID);

		var avstemEkspederteForsendelserRequest = new AvstemEkspederteForsendelserRequest(singletonList(forsendelse));

		doNothing().when(dokumentDistribusjonRepository).updateDokumentInfosAvstemtArkivDato(anyList(), anyString());

		ekspederteForsendelserService.avstemEkspederteForsendelser(avstemEkspederteForsendelserRequest);
	}

	@Test
	void shouldAvstemEkspederteForsendelserWhenNoForsendelser() {
		var avstemEkspederteForsendelserRequest = new AvstemEkspederteForsendelserRequest(emptyList());

		ekspederteForsendelserService.avstemEkspederteForsendelser(avstemEkspederteForsendelserRequest);

		verify(dokumentDistribusjonRepository, never()).updateDokumentInfosAvstemtArkivDato(anyList(), anyString());

	}

	@Test
	void shouldPartitionAvstemEkspederteForsendelser() {
		var forsendelseList = LongStream.range(0, MAX_UPDATE_PER_CALL + 1)
				.mapToObj(Forsendelse::new)
				.toList();

		var avstemEkspederteForsendelserRequest = new AvstemEkspederteForsendelserRequest(forsendelseList);

		doNothing().when(dokumentDistribusjonRepository).updateDokumentInfosAvstemtArkivDato(anyList(), anyString());

		ekspederteForsendelserService.avstemEkspederteForsendelser(avstemEkspederteForsendelserRequest);

		verify(dokumentDistribusjonRepository, times(2)).updateDokumentInfosAvstemtArkivDato(anyList(), anyString());
	}

}