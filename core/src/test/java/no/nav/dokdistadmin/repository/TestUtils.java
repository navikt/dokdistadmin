package no.nav.dokdistadmin.repository;

import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;

import java.time.LocalDateTime;

import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static no.nav.dokdistadmin.domain.ModusCode.P;

public class TestUtils {

	public static final String DISTRIBUSJON_ID = "123";
	public static final String DOKUMENT_ID_1 = "1111";
	public static final String DOKUMENT_ID_2 = "2222";
	public static final String KONVERSASJONSID = "879";

	public static DistribusjonInfo createDistribusjonInfo() {
		return DistribusjonInfo.builder()
				.distribusjonId(DISTRIBUSJON_ID)
				.distribusjonDato(LocalDateTime.now())
				.distribusjonKanal(PRINT)
				.distribusjonStatus(DistribusjonStatusCode.OPPRETTET)
				.modus(P)
				.build();
	}

	public static DokumentInfo createDokumentInfo() {
		return createDokumentInfo(DOKUMENT_ID_1);
	}

	public static DokumentInfo createDokumentInfo(String dokumentId) {
		return DokumentInfo.builder()
				.dokumentId(dokumentId)
				.dokumentStatus(DokumentStatusCode.OPPRETTET)
				.konversasjonId(KONVERSASJONSID)
				.build();
	}
}
