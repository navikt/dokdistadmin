package no.nav.dokdistadmin.administrerforsendelse.uekspederteforsendelser;

import no.nav.dokdistadmin.administrerforsendelse.uekspederteforsendelser.HentUekspederteForsendelserResponse.UekspedertForsendelse;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DokumentInfo;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

public class HentUekspederteForsendelserMapper {

	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

	public HentUekspederteForsendelserResponse map(List<DistribusjonInfo> distribusjonInfoList) {
		return HentUekspederteForsendelserResponse.builder()
				.uekspederteForsendelser(
						distribusjonInfoList.stream()
								.map(this::mapDistribusjonInfo)
								.toList())
				.build();
	}

	public UekspedertForsendelse mapDistribusjonInfo(DistribusjonInfo distribusjonInfo) {
		return UekspedertForsendelse.builder()
				.distribusjonId(distribusjonInfo.getDistribusjonId())
				.distribusjonKanal(distribusjonInfo.getDistribusjonKanal().name())
				.opprettetDato(convertDateTimeToString(distribusjonInfo.getChangeStamp().getOpprettetDato()))
				.distribusjonDato(convertDateTimeToString(distribusjonInfo.getDistribusjonDato()))
				.distribusjonStatus(distribusjonInfo.getDistribusjonStatus().name())
				.dokumenter(mapDokumentInfoList(distribusjonInfo.getDokumentInfos()))
				.build();
	}

	List<HentUekspederteForsendelserResponse.DokumentInfo> mapDokumentInfoList(Set<DokumentInfo> dokumentInfos) {
		return dokumentInfos.stream()
				.map(this::mapDokumentInfo)
				.toList();
	}

	HentUekspederteForsendelserResponse.DokumentInfo mapDokumentInfo(DokumentInfo dokumentInfo) {
		return HentUekspederteForsendelserResponse.DokumentInfo.builder()
				.forsendelseId(String.valueOf(dokumentInfo.getDokumentInfoId()))
				.dokumentId(dokumentInfo.getDokumentId())
				.dokumentStatus(dokumentInfo.getDokumentStatus().name())
				.bestillendeFagsystem(dokumentInfo.getBestillendeFagsystem())
				.fagomradeCode(dokumentInfo.getFagomrade() == null ? null : dokumentInfo.getFagomrade().name())
				.journalpostId(dokumentInfo.getArkivkode())
				.konversasjonId(dokumentInfo.getKonversasjonId())
				.brevProduksjonApplikasjon(dokumentInfo.getBrevProduksjonApplikasjon())
				.build();
	}

	public String convertDateTimeToString(LocalDateTime localDateTime) {
		return localDateTime == null ? StringUtils.EMPTY : localDateTime.format(DATE_TIME_FORMATTER);
	}
}
