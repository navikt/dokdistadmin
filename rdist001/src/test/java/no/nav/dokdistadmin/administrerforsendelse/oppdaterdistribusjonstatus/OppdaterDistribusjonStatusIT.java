package no.nav.dokdistadmin.administrerforsendelse.oppdaterdistribusjonstatus;

import no.nav.dokdistadmin.config.AbstractITest;
import no.nav.dokdistadmin.domain.DistribusjonInfo;
import no.nav.dokdistadmin.domain.DistribusjonStatusCode;
import no.nav.dokdistadmin.domain.DokumentInfo;
import no.nav.dokdistadmin.domain.DokumentStatusCode;
import org.junit.jupiter.api.Test;

import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDistribusjonInfo;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfoWithDokumentId;
import static org.assertj.core.api.Assertions.assertThat;

public class OppdaterDistribusjonStatusIT extends AbstractITest {

	private static final String URI = "/rest/v1/administrerforsendelse/oppdaterdistribusjonstatus";
	private static final String KILDE = "kilde";

	@Test
	void skalOppdatereDistribusjonOgDokumenter() {
		DistribusjonInfo distribusjon = dokumentDistribusjonRepository.persist(createDistribusjonInfo());
		DokumentInfo dokumentInfo1 = createDokumentInfoWithDokumentId("1");
		DokumentInfo dokumentInfo2 = createDokumentInfoWithDokumentId("2");
		distribusjon.addDokumentInfo(dokumentInfo1);
		distribusjon.addDokumentInfo(dokumentInfo2);
		dokumentDistribusjonRepository.persist(distribusjon);
		commitAndBeginNewTransaction();

		var request = OppdaterDistribusjonStatusRequest.builder()
				.distribusjonId(distribusjon.getDistribusjonId())
				.distribusjonstatus(DistribusjonStatusCode.EKSPEDERT.name())
				.dokumentstatus(DokumentStatusCode.EKSPEDERT.name())
				.kilde(KILDE)
				.build();

		webTestClient.put()
				.uri(URI)
				.headers(h -> h.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk();

		DistribusjonInfo oppdatertDistribusjon = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjon.getDistribusjonId());
		assertThat(oppdatertDistribusjon.getDistribusjonStatus()).isEqualTo(DistribusjonStatusCode.EKSPEDERT);
		assertThat(oppdatertDistribusjon.getChangeStamp().getEndretAv()).isEqualTo(KILDE);

		assertThat(oppdatertDistribusjon.getDokumentInfos())
				.hasSize(2)
				.allSatisfy(dok -> {
					assertThat(dok.getDokumentStatus()).isEqualTo(DokumentStatusCode.EKSPEDERT);
					assertThat(dok.getChangeStamp().getEndretAv()).isEqualTo(KILDE);
				});
	}

	@Test
	void skalReturnereNotFoundForUkjentDistribusjonId() {
		var request = OppdaterDistribusjonStatusRequest.builder()
				.distribusjonId("finnes-ikke")
				.distribusjonstatus(DistribusjonStatusCode.EKSPEDERT.name())
				.dokumentstatus(DokumentStatusCode.EKSPEDERT.name())
				.kilde(KILDE)
				.build();

		webTestClient.put()
				.uri(URI)
				.headers(h -> h.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody()
				.json("\"Distribusjon med distribusjonId=finnes-ikke ikke funnet i dokdistDb\"");
	}

	@Test
	void skalReturnereBadRequestForUgyldigDistribusjonstatus() {
		DistribusjonInfo distribusjon = dokumentDistribusjonRepository.persist(createDistribusjonInfo());
		commitAndBeginNewTransaction();

		var request = OppdaterDistribusjonStatusRequest.builder()
				.distribusjonId(distribusjon.getDistribusjonId())
				.distribusjonstatus("UGYLDIG")
				.dokumentstatus(DokumentStatusCode.EKSPEDERT.name())
				.kilde(KILDE)
				.build();

		webTestClient.put()
				.uri(URI)
				.headers(h -> h.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.json("\"Ugyldig input: UGYLDIG er ikke en gyldig kodeverdi for DistribusjonStatusCode\"");
	}

	@Test
	void skalReturnereOkForDistribusjonUtenDokumenter() {
		DistribusjonInfo distribusjon = dokumentDistribusjonRepository.persist(createDistribusjonInfo());
		commitAndBeginNewTransaction();

		var request = OppdaterDistribusjonStatusRequest.builder()
				.distribusjonId(distribusjon.getDistribusjonId())
				.distribusjonstatus(DistribusjonStatusCode.EKSPEDERT.name())
				.dokumentstatus(DokumentStatusCode.EKSPEDERT.name())
				.kilde(KILDE)
				.build();

		webTestClient.put()
				.uri(URI)
				.headers(h -> h.setBearerAuth(jwt()))
				.bodyValue(request)
				.exchange()
				.expectStatus().isOk();

		DistribusjonInfo oppdatertDistribusjon = dokumentDistribusjonRepository.getDistribusjonInfoByDistribusjonId(distribusjon.getDistribusjonId());
		assertThat(oppdatertDistribusjon.getDistribusjonStatus()).isEqualTo(DistribusjonStatusCode.EKSPEDERT);
		assertThat(oppdatertDistribusjon.getDokumentInfos()).isEmpty();
	}
}
