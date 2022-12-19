package no.nav.dokdistadmin.domain.builder;

import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.FilInfo;
import no.nav.dokdistadmin.domain.FilStatusCode;
import no.nav.dokdistadmin.domain.FilTypeCode;
import no.nav.dokdistadmin.domain.KildeTypeCode;
import no.nav.dokdistadmin.domain.KommunikasjonRetningCode;

import java.time.LocalDateTime;

public class FilInfoBuilder extends Builder<FilInfo> {

	private FilInfoBuilder() {
	}

	public static FilInfoBuilder with() {
		return new FilInfoBuilder();
	}

	private Long filInfoId;
	private String filnavn;
	private LocalDateTime mottattDato;
	private LocalDateTime sendtDato;
	private FilTypeCode filType;
	private KommunikasjonRetningCode kommunikasjonRetning;
	private FilStatusCode filStatus;
	private KildeTypeCode kildeType;
	private DokumentInfo dokumentInfo;

	@Override
	public FilInfo build() {
		FilInfo filInfo = new FilInfo(filInfoId, 1L);
		filInfo.setFilnavn(filnavn);
		filInfo.setMottattDato(mottattDato);
		filInfo.setSendtDato(sendtDato);
		filInfo.setFilType(filType);
		filInfo.setKommunikasjonRetning(kommunikasjonRetning);
		filInfo.setFilStatus(filStatus);
		filInfo.setKildeType(kildeType);
		filInfo.addDokumentInfo(dokumentInfo);
		return filInfo;
	}

	public FilInfoBuilder filInfoId(Long filInfoId) {
		this.filInfoId = filInfoId;
		return this;
	}

	public FilInfoBuilder filnavn(String filnavn) {
		this.filnavn = filnavn;
		return this;
	}

	public FilInfoBuilder mottattDato(LocalDateTime mottattDato) {
		this.mottattDato = mottattDato;
		return this;
	}

	public FilInfoBuilder sendtDato(LocalDateTime sendtDato) {
		this.sendtDato = sendtDato;
		return this;
	}

	public FilInfoBuilder filType(FilTypeCode filType) {
		this.filType = filType;
		return this;
	}

	public FilInfoBuilder kommunikasjonRetning(KommunikasjonRetningCode kommunikasjonRetning) {
		this.kommunikasjonRetning = kommunikasjonRetning;
		return this;
	}

	public FilInfoBuilder filStatus(FilStatusCode filStatus) {
		this.filStatus = filStatus;
		return this;
	}

	public FilInfoBuilder kildeType(KildeTypeCode kildeType) {
		this.kildeType = kildeType;
		return this;
	}

	public FilInfoBuilder dokumentInfo(DokumentInfo dokumentInfo) {
		this.dokumentInfo = dokumentInfo;
		return this;
	}

}

