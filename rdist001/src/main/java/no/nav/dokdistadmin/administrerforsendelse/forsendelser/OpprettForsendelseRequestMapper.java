package no.nav.dokdistadmin.administrerforsendelse.forsendelser;

import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequest.ArkivInformasjon;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequest.Dokument;
import no.nav.dokdistadmin.domain.ArkivSystemCode;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentReferanse;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.Postadresse;

import java.time.LocalDateTime;

import static no.nav.dokdistadmin.domain.ArkivSystemCode.INGEN;

public class OpprettForsendelseRequestMapper {

	public static DistribusjonInfo mapToDistribusjonInfo(OpprettForsendelseRequest request) {
		DistribusjonInfo distribusjonInfo = DistribusjonInfo.builder()
				.distribusjonId(request.getBestillingsId())
				.distribusjonKanal(request.getDistribusjonsKanal())
				.distribusjonStatus(DistribusjonStatusCode.OPPRETTET)
				.distribusjonDato(LocalDateTime.now())
				.originalDistribusjonId(request.getOriginalDistribusjonId())
				.distribusjonstype(request.getDistribusjonstype())
				.distribusjonstidspunkt(request.getDistribusjonstidspunkt())
				.build();

		distribusjonInfo.addDokumentInfo(mapToDokumentInfo(request));

		return distribusjonInfo;
	}

	private static DokumentInfo mapToDokumentInfo(OpprettForsendelseRequest request) {
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

}
