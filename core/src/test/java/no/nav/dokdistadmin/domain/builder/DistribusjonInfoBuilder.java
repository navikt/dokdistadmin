package no.nav.dokdistadmin.domain.builder;

import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DistribusjonsTypeKode;
import no.nav.dokdistadmin.domain.DistribusjonstidspunktKode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.FilInfo;
import no.nav.dokdistadmin.domain.KanalBehandlingCode;
import no.nav.dokdistadmin.domain.ModusCode;
import no.nav.dokdistadmin.domain.PostDestinasjonCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DistribusjonInfoBuilder extends Builder<DistribusjonInfo> {

	private DistribusjonInfoBuilder() {
	}

	public static DistribusjonInfoBuilder with() {
		return new DistribusjonInfoBuilder();
	}

	private Long distribusjonInfoId;
	private String distribusjonId;
	private String avtaleReferanse;
	private LocalDateTime produksjonDato;
	private LocalDateTime distribusjonDato;
	private LocalDate effektuerTidligstDato;
	private LocalDate effektuerSenestDato;
	private LocalDateTime bekreftetMottattDato;
	private String originalDistribusjonId;
	private String resendingDistribusjonId;
	private DistribusjonStatusCode distribusjonStatus;
	private ModusCode modus;
	private KanalBehandlingCode kanalBehandling;
	private PostDestinasjonCode postDestinasjon;
	private DistribusjonKanalCode distribusjonKanal;
	private Set<DokumentInfo> dokumentInfos = new HashSet<>();
	private Set<FilInfo> filInfos = new HashSet<>();
	private DistribusjonsTypeKode distribusjonstype;
	private DistribusjonstidspunktKode distribusjonstidspunkt;

	@Override
	public DistribusjonInfo build() {
		DistribusjonInfo distribusjonInfo = new DistribusjonInfo(distribusjonInfoId, 1);
		distribusjonInfo.setDistribusjonId(distribusjonId);
		distribusjonInfo.setAvtaleReferanse(avtaleReferanse);
		distribusjonInfo.setProduksjonDato(produksjonDato);
		distribusjonInfo.setDistribusjonDato(distribusjonDato);
		distribusjonInfo.setEffektuerTidligstDato(effektuerTidligstDato);
		distribusjonInfo.setEffektuerSenestDato(effektuerSenestDato);
		distribusjonInfo.setBekreftetMottattDato(bekreftetMottattDato);
		distribusjonInfo.setOriginalDistribusjonId(originalDistribusjonId);
		distribusjonInfo.setResendingDistribusjonId(resendingDistribusjonId);
		distribusjonInfo.setDistribusjonStatus(distribusjonStatus);
		distribusjonInfo.setModus(modus);
		distribusjonInfo.setKanalBehandling(kanalBehandling);
		distribusjonInfo.setPostDestinasjon(postDestinasjon);
		distribusjonInfo.setDistribusjonKanal(distribusjonKanal);
		distribusjonInfo.setDistribusjonstype(distribusjonstype);
		distribusjonInfo.setDistribusjonstidspunkt(distribusjonstidspunkt);
		for (DokumentInfo dokumentInfo : dokumentInfos) {
			distribusjonInfo.addDokumentInfo(dokumentInfo);
		}
		for (FilInfo filInfo : filInfos) {
			distribusjonInfo.addFilInfo(filInfo);
		}
		return distribusjonInfo;
	}

	public DistribusjonInfoBuilder distribusjonInfoId(Long distribusjonInfoId) {
		this.distribusjonInfoId = distribusjonInfoId;
		return this;
	}

	public DistribusjonInfoBuilder distribusjonstype(DistribusjonsTypeKode distribusjonstype) {
		this.distribusjonstype = distribusjonstype;
		return this;
	}

	public DistribusjonInfoBuilder distribusjonstidspunkt(DistribusjonstidspunktKode distribusjonstidspunkt) {
		this.distribusjonstidspunkt = distribusjonstidspunkt;
		return this;
	}

	public DistribusjonInfoBuilder distribusjonId(String distribusjonId) {
		this.distribusjonId = distribusjonId;
		return this;
	}

	public DistribusjonInfoBuilder avtaleReferanse(String avtaleReferanse) {
		this.avtaleReferanse = avtaleReferanse;
		return this;
	}

	public DistribusjonInfoBuilder produksjonDato(LocalDateTime produksjonDato) {
		this.produksjonDato = produksjonDato;
		return this;
	}

	public DistribusjonInfoBuilder distribusjonDato(LocalDateTime distribusjonDato) {
		this.distribusjonDato = distribusjonDato;
		return this;
	}

	public DistribusjonInfoBuilder effektuerTidligstDato(LocalDate effektuerTidligstDato) {
		this.effektuerTidligstDato = effektuerTidligstDato;
		return this;
	}

	public DistribusjonInfoBuilder effektuerSenestDato(LocalDate effektuerSenestDato) {
		this.effektuerSenestDato = effektuerSenestDato;
		return this;
	}

	public DistribusjonInfoBuilder bekreftetMottattDato(LocalDateTime bekreftetMottattDato) {
		this.bekreftetMottattDato = bekreftetMottattDato;
		return this;
	}

	public DistribusjonInfoBuilder originalDistribusjonId(String originalDistribusjonId) {
		this.originalDistribusjonId = originalDistribusjonId;
		return this;
	}

	public DistribusjonInfoBuilder resendingDistribusjonId(String resendingDistribusjonId) {
		this.resendingDistribusjonId = resendingDistribusjonId;
		return this;
	}

	public DistribusjonInfoBuilder distribusjonStatus(DistribusjonStatusCode distribusjonStatus) {
		this.distribusjonStatus = distribusjonStatus;
		return this;
	}

	public DistribusjonInfoBuilder modus(ModusCode modus) {
		this.modus = modus;
		return this;
	}

	public DistribusjonInfoBuilder kanalBehandling(KanalBehandlingCode kanalBehandling) {
		this.kanalBehandling = kanalBehandling;
		return this;
	}

	public DistribusjonInfoBuilder postDestinasjon(PostDestinasjonCode postDestinasjon) {
		this.postDestinasjon = postDestinasjon;
		return this;
	}

	public DistribusjonInfoBuilder distribusjonKanal(DistribusjonKanalCode distribusjonKanal) {
		this.distribusjonKanal = distribusjonKanal;
		return this;
	}

	public DistribusjonInfoBuilder dokumentInfos(DokumentInfo... dokumentInfos) {
		this.dokumentInfos.addAll(Arrays.asList(dokumentInfos));
		return this;
	}

	public DistribusjonInfoBuilder filInfos(FilInfo... filInfos) {
		this.filInfos.addAll(Arrays.asList(filInfos));
		return this;
	}

}
