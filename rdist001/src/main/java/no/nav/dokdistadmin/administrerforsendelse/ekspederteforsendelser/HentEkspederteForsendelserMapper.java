package no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser;

import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.Postadresse;
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
				.varsel(DITTNAV.equals(getDistribusjonKanal(dokumentInfo)) || SDP.equals(getDistribusjonKanal(dokumentInfo)) ? mapVarsel(dokumentInfo) : null)
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

	private Varsel mapVarsel(DokumentInfo dokumentInfo) {
		Varsel varsel = new Varsel();
		dokumentInfo.getVarselInfos().stream().filter(Objects::nonNull).distinct().forEach(varselInfo -> {
			if (EPOST.equals(varselInfo.getVarslingKanal())) {
				Varsel.EpostVarsel epostVarsel = Varsel.EpostVarsel.builder()
						.adresse(varselInfo.getEpostAdresse())
						.tittel(varselInfo.getVarslingstittel())
						.tekst(varselInfo.getVarslingstekst())
						.varslingstidspunkt(varselInfo.getVarslingstidspunkt())
						.build();
				varsel.setEpostVarsel(epostVarsel);
			} else if (MOBILTELEFON.equals(varselInfo.getVarslingKanal())) {
				Varsel.SmsVarsel smsVarsel = Varsel.SmsVarsel.builder()
						.telefonnummer(varselInfo.getMobiltelefonNummer())
						.tekst(varselInfo.getVarslingstekst())
						.varslingstidspunkt(varselInfo.getVarslingstidspunkt())
						.build();
				varsel.setSmsVarsel(smsVarsel);
			}
		});
		return varsel;
	}

	private DistribusjonKanalCode getDistribusjonKanal(DokumentInfo dokumentInfo) {
		return nonNull(dokumentInfo.getDistribusjonInfo()) ? dokumentInfo.getDistribusjonInfo().getDistribusjonKanal() : null;
	}

	public String convertDateTimeToString(LocalDateTime localDateTime) {
		final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
		return localDateTime == null ? StringUtils.EMPTY : localDateTime.format(DATE_TIME_FORMATTER);
	}
}
