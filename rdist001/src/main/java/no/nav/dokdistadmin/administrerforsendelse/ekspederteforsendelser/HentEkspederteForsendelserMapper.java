package no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.Postadresse;
import no.nav.dokdistadmin.exception.functional.JsonParseException;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.DITTNAV;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.SDP;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.EPOST;
import static no.nav.dokdistadmin.domain.VarslingKanalCode.MOBILTELEFON;

public class HentEkspederteForsendelserMapper {

	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
	private final ObjectMapper objectMapper;

	public HentEkspederteForsendelserMapper() {
		this.objectMapper = new ObjectMapper();
	}

	public HentEkspederteForsendelserResponse map(List<DokumentInfo> dokumentInfos) {
		return new HentEkspederteForsendelserResponse(dokumentInfos.stream()
				.filter(Objects::nonNull)
				.map(this::mapForsendelse)
				.toList());
	}

	public EkspederteForsendelse mapForsendelse(DokumentInfo dokumentInfo) {
		return EkspederteForsendelse.builder()
				.forsendelseId(Objects.requireNonNull(dokumentInfo.getDokumentInfoId(), "ForsendelseId kan ikke være null"))
				.journalpostId(dokumentInfo.getArkivkode())
				.distribusjonsKanal(getDistribusjonKanal(dokumentInfo))
				.ekspedertDato(convertDateTimeToString(dokumentInfo.getEkspedertDato()))
				.postadresse(PRINT.equals(getDistribusjonKanal(dokumentInfo)) ? mapPostadresse(dokumentInfo) : null)
				.digitalpostkasse(SDP.equals(getDistribusjonKanal(dokumentInfo)) ? mapDigitalpostkasse(dokumentInfo) : null)
				.varsel(DITTNAV.equals(getDistribusjonKanal(dokumentInfo)) ? mapVarsel(dokumentInfo) : null)
				.build();
	}

	private EkspederteForsendelse.Digitalpostkasse mapDigitalpostkasse(DokumentInfo dokumentInfo) {
		return EkspederteForsendelse.Digitalpostkasse.builder()
				.digitalpostkasseadresse(dokumentInfo.getDigitalPostkasseAdresse())
				.digitalpostkasseleverandor(dokumentInfo.getDigitalDistributorId())
				.build();
	}

	private EkspederteForsendelse.PostadresseTo mapPostadresse(DokumentInfo dokumentInfo) {
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

	private EkspederteForsendelse.Varsel mapVarsel(DokumentInfo dokumentInfo) {
		VarselDigitalInfo varseltekst = varselInfo(dokumentInfo);
		return EkspederteForsendelse.Varsel.builder()
				.digitalkontaktinformasjon(jsonString(varseltekst.getDigitalkontaktInfo()))
				.varseltekst(jsonString(varseltekst.getVarseltekst()))
				.build();

	}

	private VarselDigitalInfo varselInfo(DokumentInfo dokumentInfo) {
		KontaktInfo epost = new KontaktInfo();
		KontaktInfo sms = new KontaktInfo();
		dokumentInfo.getVarselInfos().stream().filter(Objects::nonNull).forEach(varselInfo -> {
			if (EPOST.equals(varselInfo.getVarslingKanal())) {
				epost.setTekstMelding(varselInfo.getVarslingstekst());
				epost.setDigitalKontakt(varselInfo.getEpostAdresse());
			} else if (MOBILTELEFON.equals(varselInfo.getVarslingKanal())) {
				sms.setTekstMelding(varselInfo.getVarslingstekst());
				sms.setDigitalKontakt(varselInfo.getMobiltelefonNummer());

			}
		});

		return VarselDigitalInfo.builder()
				.varseltekst(VarselDigitalInfo.VarselInfoTo.builder()
						.epost(getStringOrNull(epost.getTekstMelding()))
						.sms(getStringOrNull(sms.getTekstMelding()))
						.build())
				.digitalkontaktInfo(VarselDigitalInfo.VarselInfoTo.builder()
						.epost(getStringOrNull(epost.getDigitalKontakt()))
						.sms(getStringOrNull(sms.getDigitalKontakt()))
						.build())
				.build();
	}

	private DistribusjonKanalCode getDistribusjonKanal(DokumentInfo dokumentInfo) {
		return nonNull(dokumentInfo.getDistribusjonInfo()) ? dokumentInfo.getDistribusjonInfo().getDistribusjonKanal() : null;
	}

	public String convertDateTimeToString(LocalDateTime localDateTime) {
		final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
		return localDateTime == null ? StringUtils.EMPTY : localDateTime.format(DATE_TIME_FORMATTER);
	}

	private String jsonString(VarselDigitalInfo.VarselInfoTo varselInfoTo) {
		try {
			return objectMapper.writeValueAsString(varselInfoTo);
		} catch (JsonProcessingException e) {
			throw new JsonParseException("kan ikke prosess til json String", e);
		}
	}

	private String getStringOrNull(String str) {
		return StringUtils.isBlank(str) ? null : str;
	}
}
