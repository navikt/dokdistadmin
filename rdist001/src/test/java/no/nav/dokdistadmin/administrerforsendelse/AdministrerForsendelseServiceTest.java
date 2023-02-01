package no.nav.dokdistadmin.administrerforsendelse;

import no.nav.dokdistadmin.repository.DokumentDistribusjonRepository;
import no.nav.dokdistadmin.repository.DokumentInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.lang.Math.abs;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdministrerForsendelseServiceTest {

	@Mock
	DokumentDistribusjonRepository dokumentDistribusjonRepository;

	@Mock
	DokumentInfoRepository dokumentInfoRepository;

	@InjectMocks
	AdministrerForsendelseService ekspederteForsendelserService;

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
	void shouldBatchAvstemForsendelser(int antallForsendelser, int antallKallTilDb) {
		var avstemtReferanse = "MMA-1234";
		List<Forsendelse> forsendelseList = createForsendelser(antallForsendelser);

		var avstemEkspederteForsendelserRequest = new AvstemForsendelserRequest(avstemtReferanse, forsendelseList);
		when(dokumentInfoRepository.updateAvstemtReferanseAndAvstemtDatoForIdIn(anyString(), anyList(), anyString()))
				.thenAnswer(i -> {
					List<Long> forsendelser = i.getArgument(1);
					return forsendelser.size();
				});

		ekspederteForsendelserService.avstemForsendelser(avstemEkspederteForsendelserRequest);

		verify(dokumentInfoRepository, times(antallKallTilDb)).updateAvstemtReferanseAndAvstemtDatoForIdIn(anyString(), anyList(), anyString());
	}

	@ParameterizedTest
	@CsvSource(value = {
			"999, 1",
			"1000, 1",
			"1001, 2"
	})
	void shouldBatchAvstemEkspederteForsendelser(int antallForsendelser, int antallKallTilDb) {
		List<Forsendelse> forsendelseList = createForsendelser(antallForsendelser);

		var avstemEkspederteForsendelserRequest = new AvstemEkspederteForsendelserRequest(forsendelseList);
		when(dokumentDistribusjonRepository.updateDokumentInfosAvstemtArkivDato(anyList(), anyString()))
				.thenAnswer(i -> {
					List<Long> forsendelser = i.getArgument(0);
					return forsendelser.size();
				});

		ekspederteForsendelserService.avstemEkspederteForsendelser(avstemEkspederteForsendelserRequest);

		verify(dokumentDistribusjonRepository, times(antallKallTilDb)).updateDokumentInfosAvstemtArkivDato(anyList(), anyString());
	}

	@ParameterizedTest
	@MethodSource
	void shouldPartitionList(int antallForsendelser, int[] forventetAntallForsendelserPerPartisjon) {
		var forsendelser = LongStream.range(0, antallForsendelser).boxed().toList();

		var partisjoner = ekspederteForsendelserService.partitionList(forsendelser);

		var sumAvForsendelserIAllePartisjoner = partisjoner.stream().map(List::size).reduce(0, Integer::sum);
		var faktiskAntallForsendelserPerPartisjon = partisjoner.stream().mapToInt(List::size).toArray();

		assertEquals(antallForsendelser, sumAvForsendelserIAllePartisjoner);
		assertArrayEquals(forventetAntallForsendelserPerPartisjon, faktiskAntallForsendelserPerPartisjon);
	}

	private static Stream<Arguments> shouldPartitionList() {
		return Stream.of(
				Arguments.of(999, new int[]{999}),
				Arguments.of(1000, new int[]{1000}),
				Arguments.of(1001, new int[]{1000, 1})
		);
	}

	private static List<Forsendelse> createForsendelser(Integer antallForsendelser) {
		var random = new Random();

		return IntStream.range(0, antallForsendelser)
				.mapToObj(i -> new Forsendelse(abs(random.nextLong())))
				.toList();
	}

}