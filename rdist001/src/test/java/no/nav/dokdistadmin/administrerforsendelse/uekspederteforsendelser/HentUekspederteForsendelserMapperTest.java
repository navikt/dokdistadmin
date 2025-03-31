package no.nav.dokdistadmin.administrerforsendelse.uekspederteforsendelser;

import no.nav.dokdistadmin.administrerforsendelse.uekspederteforsendelser.HentUekspederteForsendelserResponse.UekspedertForsendelse;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.DOKUMENTINFO_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfo;
import static org.assertj.core.api.Assertions.assertThat;

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

		assertThat(result.getDistribusjonId()).isEqualTo(distribusjonInfo.getDistribusjonId());
		assertThat(result.getDistribusjonKanal()).isEqualTo(distribusjonInfo.getDistribusjonKanal().name());
		assertThat(result.getOpprettetDato()).isEqualTo(expectedOpprettetDato);
		assertThat(result.getDistribusjonDato()).isEqualTo(expectedDistribusjonDato);
		assertThat(result.getDistribusjonStatus()).isEqualTo(distribusjonInfo.getDistribusjonStatus().name());

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
		var dokumentInfo = createDokumentInfo();
		ReflectionTestUtils.setField(dokumentInfo, "dokumentInfoId", DOKUMENTINFO_ID);

		var result = mapper.mapDokumentInfo(dokumentInfo);

		assertThat(result.getForsendelseId()).isEqualTo(dokumentInfo.getDokumentInfoId().toString());
		assertThat(result.getDokumentId()).isEqualTo(dokumentInfo.getDokumentId());
		assertThat(result.getDokumentStatus()).isEqualTo(dokumentInfo.getDokumentStatus().name());
		assertThat(result.getBestillendeFagsystem()).isEqualTo(dokumentInfo.getBestillendeFagsystem());
		assertThat(result.getFagomradeCode()).isEqualTo(dokumentInfo.getFagomrade());
		assertThat(result.getJournalpostId()).isEqualTo(dokumentInfo.getArkivkode());
		assertThat(result.getKonversasjonId()).isEqualTo(dokumentInfo.getKonversasjonId());
		assertThat(result.getBrevProduksjonApplikasjon()).isEqualTo(dokumentInfo.getBrevProduksjonApplikasjon());

	}

	@ParameterizedTest
	@MethodSource
	void shouldConvertDateTimeToString(LocalDateTime localDateTime, String expected) {
		var result = mapper.convertDateTimeToString(localDateTime);

		assertThat(result).isEqualTo(expected);
	}

	private static Stream<Arguments> shouldConvertDateTimeToString() {
		return Stream.of(
				Arguments.of(LocalDateTime.of(2023, 1, 25, 13, 15, 30), "2023-01-25 13:15:30"),
				Arguments.of(null, "")
		);
	}

}