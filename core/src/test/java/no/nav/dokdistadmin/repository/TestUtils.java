package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import no.nav.dokdistadmin.domain.builder.DistribusjonInfoBuilder;
import no.nav.dokdistadmin.domain.builder.DokumentInfoBuilder;

import java.time.LocalDateTime;

import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static no.nav.dokdistadmin.domain.ModusCode.P;

public class TestUtils {

	public static final String DISTRIBUSJON_ID = "123";
	public static final String DOKUMENT_ID_1 = "1111";
	public static final String DOKUMENT_ID_2 = "2222";
	public static final String DOKUMENT_ID_3 = "3333";
	public static final String KONVERSASJONSID = "879";

	public static DistribusjonInfoBuilder createDistribusjonInfo() {
		return createDistribusjonInfo("");
	}

	public static DistribusjonInfoBuilder createDistribusjonInfoWithDokumentInfo() {
		return createDistribusjonInfo("").dokumentInfos(createDokumentInfo().build());
	}

	public static DistribusjonInfoBuilder createDistribusjonInfo(String idPadding) {
		return DistribusjonInfoBuilder.with()
				.distribusjonId(DISTRIBUSJON_ID + idPadding)
				.distribusjonDato(LocalDateTime.now())
				.distribusjonKanal(PRINT)
				.distribusjonStatus(DistribusjonStatusCode.OPPRETTET)
				.modus(P);
	}

	public static DokumentInfoBuilder createDokumentInfo() {
		return createDokumentInfo(DOKUMENT_ID_1);
	}

	public static DokumentInfoBuilder createDokumentInfo(String dokumentId) {
		return DokumentInfoBuilder.with()
				.dokumentId(dokumentId)
				.dokumentStatus(DokumentStatusCode.OPPRETTET)
				.konversasjonsId(KONVERSASJONSID);
	}
}
