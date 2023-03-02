package no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser;

import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.Postadresse;
import no.nav.dokdistadmin.domain.VarselInfo;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.DITTNAV;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.SDP;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.EPOST;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.MOBILTELEFON;

public class HentEkspederteForsendelserMapper {

	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

	public static EkspederteForsendelse mapForsendelse(DokumentInfo dokumentInfo) {
		return EkspederteForsendelse.builder()
				.forsendelseId(Objects.requireNonNull(dokumentInfo.getDokumentInfoId(), "ForsendelseId kan ikke være null"))
				.journalpostId(dokumentInfo.getArkivkode())
				.distribusjonsKanal(getDistribusjonKanal(dokumentInfo))
				.ekspedertDato(convertDateTimeToString(dokumentInfo.getEkspedertDato()))
				.postadresse(PRINT == getDistribusjonKanal(dokumentInfo) ? mapPostadresse(dokumentInfo) : null)
				.digitalpostkasse(SDP == getDistribusjonKanal(dokumentInfo) ? mapDigitalpostkasse(dokumentInfo) : null)
				.varsel(mapVarslerForKanal(getDistribusjonKanal(dokumentInfo), dokumentInfo.getVarselInfos()))
				.build();
	}

	private static EkspederteForsendelse.Digitalpostkasse mapDigitalpostkasse(DokumentInfo dokumentInfo) {
		return EkspederteForsendelse.Digitalpostkasse.builder()
				.digitalpostkasseadresse(dokumentInfo.getDigitalPostkasseAdresse())
				.digitalpostkasseleverandor(dokumentInfo.getDigitalDistributorId())
				.build();
	}

	private static EkspederteForsendelse.PostadresseTo mapPostadresse(DokumentInfo dokumentInfo) {
		if (dokumentInfo.getPostadresse() == null) {
			return null;
		}

		Postadresse postadresse = dokumentInfo.getPostadresse();
		return EkspederteForsendelse.PostadresseTo.builder()
				.adresselinje1(postadresse.getAdresselinje1())
				.adresselinje2(postadresse.getAdresselinje2())
				.adresselinje3(postadresse.getAdresselinje3())
				.postnummer(postadresse.getPostnummer())
				.poststed(postadresse.getPoststed())
				.landkode(postadresse.getLandkode())
				.build();

	}

	private static Varsel mapVarslerForKanal(DistribusjonKanalCode kanal, Set<VarselInfo> varselInfos) {
		if ((DITTNAV == kanal || SDP == kanal) && !varselInfos.isEmpty()) {
			return Varsel.builder()
					.epostVarsel(getEpostVarsler(varselInfos))
					.smsVarsel(getSMSVarsler(varselInfos))
					.build();
		}
		return null;
	}

	private static List<Varsel.EpostVarsel> getEpostVarsler(Set<VarselInfo> varselInfos) {
		return varselInfos.stream()
				.filter(varselInfo -> EPOST == varselInfo.getVarslingKanal())
				.map(varselInfo -> Varsel.EpostVarsel.builder()
						.adresse(varselInfo.getEpostAdresse())
						.tittel(varselInfo.getVarslingstittel())
						.tekst(varselInfo.getVarslingstekst())
						.varslingstidspunkt(varselInfo.getVarslingstidspunkt())
						.build())
				.collect(Collectors.toList());
	}

	private static List<Varsel.SmsVarsel> getSMSVarsler(Set<VarselInfo> varselInfos) {
		return varselInfos.stream()
				.filter(varselInfo -> MOBILTELEFON == varselInfo.getVarslingKanal())
				.map(varselInfo -> Varsel.SmsVarsel.builder()
						.telefonnummer(varselInfo.getMobiltelefonNummer())
						.tekst(varselInfo.getVarslingstekst())
						.varslingstidspunkt(varselInfo.getVarslingstidspunkt())
						.build())
				.collect(Collectors.toList());
	}

	private static DistribusjonKanalCode getDistribusjonKanal(DokumentInfo dokumentInfo) {
		return nonNull(dokumentInfo.getDistribusjonInfo()) ? dokumentInfo.getDistribusjonInfo().getDistribusjonKanal() : null;
	}

	private static String convertDateTimeToString(LocalDateTime localDateTime) {
		return localDateTime == null ? StringUtils.EMPTY : localDateTime.format(DATE_TIME_FORMATTER);
	}
}
