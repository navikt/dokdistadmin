package no.nav.dokdistadmin.administrerforsendelse.forsendelser;

import no.nav.dokdistadmin.administrerforsendelse.forsendelser.HentForsendelseResponse.ArkivInformasjon;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.HentForsendelseResponse.Dokument;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.HentForsendelseResponse.Postadresse;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentReferanse;
import no.nav.dokdistadmin.exception.functional.KanIkkeBestemmeDokumentrekkefoelgeException;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class HentForsendelseResponseMapper {

	public static HentForsendelseResponse map(DokumentInfo dokumentInfo) {
		final DistribusjonInfo distribusjonInfo = dokumentInfo.getDistribusjonInfo();
		final no.nav.dokdistadmin.domain.Postadresse postadresse = dokumentInfo.getPostadresse();
		final String arkivkode = dokumentInfo.getArkivkode();

		return HentForsendelseResponse.builder()
				.bestillingsId(dokumentInfo.getDokumentId())
				.originalBestillingsId(distribusjonInfo.getOriginalDistribusjonId())
				.konversasjonId(dokumentInfo.getKonversasjonId())
				.bestillendeFagsystem((dokumentInfo.getBestillendeFagsystem()))
				.modus(distribusjonInfo.getModus())
				.distribusjonstype(distribusjonInfo.getDistribusjonstype())
				.distribusjonstidspunkt(distribusjonInfo.getDistribusjonstidspunkt())
				.forsendelseStatus(dokumentInfo.getDokumentStatus() == null ? null : dokumentInfo.getDokumentStatus().name())
				.distribusjonKanal(distribusjonInfo.getDistribusjonKanal() == null ? null : distribusjonInfo.getDistribusjonKanal().name())
				.tema(dokumentInfo.getFagomrade())
				.forsendelseTittel(dokumentInfo.getForsendelseTittel())
				.batchId(dokumentInfo.getBatchId())
				.dokumentProdApp(dokumentInfo.getBrevProduksjonApplikasjon())
				.varselStatus(distribusjonInfo.getVarselStatus() == null ? null : distribusjonInfo.getVarselStatus().name())
				.mottaker(HentForsendelseResponse.Mottaker.builder()
						.mottakerId(dokumentInfo.getMottakerId())
						.mottakerType(dokumentInfo.getMottakerIdType() == null ? null : dokumentInfo.getMottakerIdType().name())
						.mottakerNavn(dokumentInfo.getMottakerNavn())
						.build())
				.arkivInformasjon(arkivkode == null ? null : ArkivInformasjon.builder()
						.arkivSystem(dokumentInfo.getArkivSystem())
						.arkivId(arkivkode)
						.build())
				.postadresse(postadresse == null ? null : Postadresse.builder()
						.adresselinje1(postadresse.getAdresselinje1())
						.adresselinje2(postadresse.getAdresselinje2())
						.adresselinje3(postadresse.getAdresselinje3())
						.landkode(postadresse.getLandkode())
						.postnummer(postadresse.getPostnummer())
						.poststed(postadresse.getPoststed())
						.build())
				.dokumenter(getDokumentList(dokumentInfo.getDokumentReferanses()))
				.build();
	}

	private static List<Dokument> getDokumentList(Set<DokumentReferanse> dokumentReferanseSet) {

		validerRekkefoelge(dokumentReferanseSet);

		return dokumentReferanseSet.stream()
				.sorted(Comparator.comparing(DokumentReferanse::getRekkefolge))
				.map(HentForsendelseResponseMapper::mapToDokument)
				.toList();
	}

	private static Dokument mapToDokument(DokumentReferanse dokumentReferanse) {
		return Dokument.builder()
				.dokumentObjektReferanse(dokumentReferanse.getDokumentUri())
				.dokumenttypeId(dokumentReferanse.getDokumenttypeId())
				.tilknyttetSom(dokumentReferanse.getRefererTil() == null ? null : dokumentReferanse.getRefererTil().name())
				.arkivDokumentInfoId(dokumentReferanse.getArkivDokumentInfoId())
				.build();
	}

	private static void validerRekkefoelge(Set<DokumentReferanse> dokumentReferanseSet) {
		var dokumentMedUgyldigRekkefoelge = dokumentReferanseSet.stream()
				.filter(dok -> dok.getRekkefolge() == null || dok.getRekkefolge() < 1)
				.toList();

		if (!dokumentMedUgyldigRekkefoelge.isEmpty()) {
			var feilmelding = dokumentMedUgyldigRekkefoelge.stream()
					.map(dok -> format("Dokument med dokumentReferanseId=%s har ugyldig rekkefølge=%s", dok.getDokumentReferanseId(), dok.getRekkefolge()))
					.collect(Collectors.joining(", "));

			throw new KanIkkeBestemmeDokumentrekkefoelgeException(format(
					"Kan ikke sortere dokumenter, da ett eller flere dokumenter har ugyldig verdi for feltet rekkefølge (rekkefølge må være større enn 0): %s", feilmelding));
		}
	}
}
