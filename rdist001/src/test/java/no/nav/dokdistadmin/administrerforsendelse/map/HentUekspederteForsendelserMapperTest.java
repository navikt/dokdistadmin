package no.nav.dokdistadmin.administrerforsendelse.map;

import no.nav.dokdistadmin.administrerforsendelse.HentUekspederteForsendelserResponse;
import no.nav.dokdistadmin.administrerforsendelse.HentUekspederteForsendelserResponse.UekspedertForsendelse;
import no.nav.dokdistadmin.administrerforsendelse.TestUtils;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.createDokumentInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HentUekspederteForsendelserMapperTest {

	private final HentUekspederteForsendelserMapper mapper = new HentUekspederteForsendelserMapper();

	@Test
	void shouldMapDistribusjonInfoList() {
		var distribusjonInfoList = List.of(
				createDistribusjonInfo(),
				createDistribusjonInfo()
		);

		var result = mapper.map(distribusjonInfoList);

		assertThat(result.getUekspederteForsendelser())
				.extracting(UekspedertForsendelse::getDistribusjonId)
				.containsExactlyInAnyOrderElementsOf(
						distribusjonInfoList.stream()
								.map(DistribusjonInfo::getDistribusjonId)
								.toList());
	}

	@Test
	void shouldMapDistribusjonInfo() {
		var distribusjonInfo = createDistribusjonInfo();

		var result = mapper.mapDistribusjonInfo(distribusjonInfo);
		var expectedOpprettetDato = mapper.convertDateTimeToString(distribusjonInfo.getChangeStamp().getOpprettetDato());
		var expectedDistribusjonDato = mapper.convertDateTimeToString(distribusjonInfo.getDistribusjonDato());

		assertEquals(distribusjonInfo.getDistribusjonId(), result.getDistribusjonId());
		assertEquals(distribusjonInfo.getDistribusjonKanal().name(), result.getDistribusjonKanal());
		assertEquals(expectedOpprettetDato, result.getOpprettetDato());
		assertEquals(expectedDistribusjonDato, result.getDistribusjonDato());
		assertEquals(distribusjonInfo.getDistribusjonStatus().name(), result.getDistribusjonStatus());

		assertThat(result.getDokumenter())
				.extracting(HentUekspederteForsendelserResponse.DokumentInfo::getForsendelseId)
				.containsExactlyInAnyOrderElementsOf(distribusjonInfo.getDokumentInfos().stream()
						.map(it -> it.getDokumentInfoId().toString())
						.toList());

	}

	@Test
	void shouldMapDokumentInfoList() {
		var dokumentInfoList = Set.of(
				createDokumentInfo(),
				createDokumentInfo()
		);

		var result = mapper.mapDokumentInfoList(dokumentInfoList);

		assertThat(result)
				.extracting(HentUekspederteForsendelserResponse.DokumentInfo::getForsendelseId)
				.containsExactlyInAnyOrderElementsOf(
						dokumentInfoList.stream()
								.map(it -> String.valueOf(it.getDokumentInfoId()))
								.toList());
	}

	@Test
	void shouldMapDokumentInfo() {
		var dokumentInfo = TestUtils.createDokumentInfo();

		var result = mapper.mapDokumentInfo(dokumentInfo);
		var expectedAvstemtArkivDato = mapper.convertDateTimeToString(dokumentInfo.getAvstemtArkivDato());

		assertEquals(dokumentInfo.getDokumentInfoId().toString(), result.getForsendelseId());
		assertEquals(dokumentInfo.getDokumentId(), result.getDokumentId());
		assertEquals(dokumentInfo.getDokumentStatus().name(), result.getDokumentStatus());
		assertEquals(dokumentInfo.getBestillendeFagsystem(), result.getBestillendeFagsystem());
		assertEquals(dokumentInfo.getFagomrade().name(), result.getFagomradeCode());
		assertEquals(dokumentInfo.getArkivkode(), result.getJournalpostId());
		assertEquals(dokumentInfo.getKonversasjonId(), result.getKonversasjonId());
		assertEquals(dokumentInfo.getBrevProduksjonApplikasjon(), result.getBrevProduksjonApplikasjon());
		assertEquals(expectedAvstemtArkivDato, result.getAvstemtDato());
		assertEquals(dokumentInfo.getAvstemtReferanse(), result.getAvstemtReferanse());
	}

	@ParameterizedTest
	@MethodSource
	void shouldConvertDateTimeToString(LocalDateTime localDateTime, String expected) {
		var result = mapper.convertDateTimeToString(localDateTime);

		assertEquals(expected, result);
	}

	private static Stream<Arguments> shouldConvertDateTimeToString() {
		return Stream.of(
				Arguments.of(LocalDateTime.of(2023, 1, 25, 13, 15, 30), "2023-01-25 13:15:30"),
				Arguments.of(null, "")
		);
	}

}