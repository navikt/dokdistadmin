package no.nav.dokdistadmin.administrerforsendelse;

import no.nav.dokdistadmin.administrerforsendelse.AvstemEkspederteForsendelserRequest.Forsendelse;
import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.util.Random;
import java.util.stream.IntStream;

import static java.lang.Math.abs;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EkspederteForsendelserServiceTest {

	@Mock
	DokumentDistribusjonRepository dokumentDistribusjonRepository;

	@InjectMocks
	EkspederteForsendelserService ekspederteForsendelserService;

	@BeforeEach
	void setup() {
		MDC.put(USER_ID, "testUser");
	}

	@ParameterizedTest
	@CsvSource(value = {
			"999, 1",
			"1000, 1",
			"1001, 2"
	})
	void shouldPartitionAvstemEkspederteForsendelser(Integer antallForsendelser, Integer antallPartisjoner) {
		var random = new Random();

		var forsendelseList = IntStream.range(0, antallForsendelser)
				.mapToObj(i -> new Forsendelse(abs(random.nextLong())))
				.toList();

		var avstemEkspederteForsendelserRequest = new AvstemEkspederteForsendelserRequest(forsendelseList);

		doNothing().when(dokumentDistribusjonRepository).updateDokumentInfosAvstemtArkivDato(anyList(), anyString());

		ekspederteForsendelserService.avstemEkspederteForsendelser(avstemEkspederteForsendelserRequest);

		verify(dokumentDistribusjonRepository, times(antallPartisjoner)).updateDokumentInfosAvstemtArkivDato(anyList(), anyString());
	}

}