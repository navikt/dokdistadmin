package no.nav.dokdistadmin.administrerforsendelse.forsendelser;

import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequest.ArkivInformasjon;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequest.Dokument;
import no.nav.dokdistadmin.domain.ArkivSystemCode;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentReferanse;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.ModusCode;
import no.nav.dokdistadmin.domain.Postadresse;

import java.time.LocalDateTime;
import java.util.Arrays;

import static java.lang.String.format;
import static no.nav.dokdistadmin.domain.ArkivSystemCode.INGEN;
import static no.nav.dokdistadmin.domain.ModusCode.T;

public class OpprettForsendelseRequestMapper {

	public static DistribusjonInfo mapToDistribusjonInfo(OpprettForsendelseRequest request, ModusCode... modus) {
		final ModusCode modusCode = Arrays.stream(modus)
				.findFirst()
				.orElse(T);

		DistribusjonInfo distribusjonInfo = DistribusjonInfo.builder()
				.distribusjonId(request.getBestillingsId())
				.distribusjonKanal(request.getDistribusjonsKanal())
				.distribusjonStatus(DistribusjonStatusCode.OPPRETTET)
				.modus(modusCode)
				.distribusjonDato(LocalDateTime.now())
				.originalDistribusjonId(request.getOriginalDistribusjonId())
				.distribusjonstype(request.getDistribusjonstype())
				.distribusjonstidspunkt(request.getDistribusjonstidspunkt())
				.build();

		distribusjonInfo.addDokumentInfo(mapToDokumentInfo(request));

		return distribusjonInfo;
	}

	private static DokumentInfo mapToDokumentInfo(OpprettForsendelseRequest request) {
		validerForsendelseMetadata(request);
		DokumentInfo dokumentInfo = DokumentInfo.builder()
				.dokumentId(request.getBestillingsId())
				.dokumentStatus(DokumentStatusCode.OPPRETTET)
				.fagomrade(request.getTema())
				.brevProduksjonApplikasjon(request.getDokumentProdApp())
				.bestillendeFagsystem(request.getBestillendeFagsystem())
				.mottakerId(request.getMottaker().getMottakerId())
				.mottakerIdType(request.getMottaker().getMottakerType())
				.mottakerNavn(request.getMottaker().getMottakerNavn())
				.arkivSystem(getArkivSystem(request.getArkivInformasjon()))
				.arkivkode(request.getArkivInformasjon() == null ? null : request.getArkivInformasjon().getArkivId())
				.forsendelseTittel(request.getForsendelseTittel())
				.batchId(request.getBatchId())
				.postadresse(mapPostadresse(request.getPostadresse()))
				.forsendelseMetadata(request.getForsendelseMetadata() == null ? null : new String(request.getForsendelseMetadata()))
				.forsendelseMetadataType(request.getForsendelseMetadataType())
				.build();

		request.getDokumenter().forEach(dokument ->
				dokumentInfo.addDokumentReferanse(mapDokumentReferanse(dokument, dokumentInfo))
		);

		return dokumentInfo;
	}

	private static DokumentReferanse mapDokumentReferanse(Dokument dokument, DokumentInfo dokumentInfo) {
		return DokumentReferanse.builder()
				.refererTil(dokument.getTilknyttetSom())
				.dokumentUri(dokument.getDokumentObjektReferanse())
				.rekkefolge(dokument.getRekkefolge())
				.arkivDokumentInfoId(dokument.getArkivDokumentInfoId())
				.dokumenttypeId(dokument.getDokumenttypeId())
				.dokumentInfo(dokumentInfo)
				.build();
	}

	private static Postadresse mapPostadresse(OpprettForsendelseRequest.Postadresse postadresse) {
		if (postadresse == null) {
			return null;
		}

		return Postadresse.builder()
				.adresselinje1(postadresse.getAdresselinje1())
				.adresselinje2(postadresse.getAdresselinje2())
				.adresselinje3(postadresse.getAdresselinje3())
				.postnummer(postadresse.getPostnummer())
				.poststed(postadresse.getPoststed())
				.landkode(postadresse.getLandkode())
				.build();
	}

	private static ArkivSystemCode getArkivSystem(ArkivInformasjon arkivInformasjon) {
		if (arkivInformasjon == null) {
			return INGEN;
		}
		return arkivInformasjon.getArkivSystem() == null ? INGEN : arkivInformasjon.getArkivSystem();
	}

	private static void validerForsendelseMetadata(OpprettForsendelseRequest request) {
		if ((request.getForsendelseMetadata() == null) != (request.getForsendelseMetadataType() == null)) {
			throw new IllegalArgumentException(format("Forsendelsesmetadata og -type må enten begge være satt, eller begge være null. " +
							"forsendelseMetadata=%s, forsendelseMetadataType=%s", request.getForsendelseMetadata(), request.getForsendelseMetadataType()));
		}
	}

}
