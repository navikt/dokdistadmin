package no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser;

import no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils;
import no.nav.dokdistadmin.domain.DokumentInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.ADRESSELINJE_1;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.ADRESSELINJE_2;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.ADRESSELINJE_3;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.ARKIV_KODE;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.DIGITALPOSTKASSE_ADRESSE;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.DIGITAL_DISTRIBUTOR_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.DOKUMENTINFO_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.FIRST_VARSEL_SENDT_DATO;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.LANDKODE;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.MELDING;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.POSTNUMMER;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.POSTSTED;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.TELEFONNUMMER;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.VARSELTITTEL;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfoWithDistribusjonKanal;
import static no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser.HentEkspederteForsendelserMapper.mapForsendelse;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.DITTNAV;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.SDP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HentEkspederteForsendelserMapperTest {

	private final DokumentInfo DOKUMENTINFO_DITTNAV = createDokumentInfoWithDistribusjonKanal(DITTNAV);
	private final DokumentInfo DOKUMENTINFO_SDP = createDokumentInfoWithDistribusjonKanal(SDP);
	private final DokumentInfo DOKUMENTINFO_PRINT = createDokumentInfoWithDistribusjonKanal(PRINT);

	@Test
	public void shouldMapVarselWhenDistribusjonKodeIsDITTNAV() {
		HentEkspederteForsendelserResponse ekspederteForsendelserResponse = new HentEkspederteForsendelserResponse(
				List.of(mapForsendelse(DOKUMENTINFO_DITTNAV))
		);

		assertThat(ekspederteForsendelserResponse.forsendelser())
				.hasSize(1)
				.allSatisfy(forsendelse -> {
					assertThat(forsendelse.getForsendelseId()).isEqualTo(DOKUMENTINFO_ID);
					assertThat(forsendelse.getJournalpostId()).isEqualTo(ARKIV_KODE);
					assertThat(forsendelse.getDistribusjonsKanal()).isEqualTo(DITTNAV);
					assertThat(forsendelse.getVarsel().getEpostVarsel()).hasSize(1);
					assertThat(forsendelse.getVarsel().getEpostVarsel().get(0).getAdresse()).isEqualTo(Rdist001TestUtils.EPOSTADDRESS);
					assertThat(forsendelse.getVarsel().getEpostVarsel().get(0).getTittel()).isEqualTo(VARSELTITTEL);
					assertThat(forsendelse.getVarsel().getEpostVarsel().get(0).getTekst()).isEqualTo(MELDING);
					assertThat(forsendelse.getVarsel().getEpostVarsel().get(0).getVarslingstidspunkt()).isEqualTo(FIRST_VARSEL_SENDT_DATO);

					assertThat(forsendelse.getVarsel().getSmsVarsel()).hasSize(1);
					assertThat(forsendelse.getVarsel().getSmsVarsel().get(0).getTelefonnummer()).isEqualTo(TELEFONNUMMER);
					assertThat(forsendelse.getVarsel().getSmsVarsel().get(0).getTekst()).isEqualTo(MELDING);
					assertThat(forsendelse.getVarsel().getSmsVarsel().get(0).getVarslingstidspunkt()).isEqualTo(FIRST_VARSEL_SENDT_DATO);

					assertNull(forsendelse.getDigitalpostkasse());
				});
	}

	@Test
	public void shouldMapDigitalpostkasseWhenDistribusjonKanalIsSDP() {
		HentEkspederteForsendelserResponse ekspederteForsendelserResponse = new HentEkspederteForsendelserResponse(
				List.of(mapForsendelse(DOKUMENTINFO_SDP))
		);

		assertThat(ekspederteForsendelserResponse.forsendelser())
				.hasSize(1)
				.allSatisfy(forsendelse -> {
					assertThat(forsendelse.getForsendelseId()).isEqualTo(DOKUMENTINFO_ID);
					assertThat(forsendelse.getJournalpostId()).isEqualTo(ARKIV_KODE);
					assertThat(forsendelse.getDistribusjonsKanal()).isEqualTo(SDP);
					assertThat(forsendelse.getDigitalpostkasse().getDigitalpostkasseadresse()).isEqualTo(DIGITALPOSTKASSE_ADRESSE);
					assertThat(forsendelse.getDigitalpostkasse().getDigitalpostkasseleverandor()).isEqualTo(DIGITAL_DISTRIBUTOR_ID);
					assertThat(forsendelse.getVarsel().getEpostVarsel()).hasSize(1);
					assertThat(forsendelse.getVarsel().getEpostVarsel().get(0).getAdresse()).isEqualTo(Rdist001TestUtils.EPOSTADDRESS);
					assertThat(forsendelse.getVarsel().getEpostVarsel().get(0).getTittel()).isEqualTo(VARSELTITTEL);
					assertThat(forsendelse.getVarsel().getEpostVarsel().get(0).getTekst()).isEqualTo(MELDING);
					assertThat(forsendelse.getVarsel().getEpostVarsel().get(0).getVarslingstidspunkt()).isEqualTo(FIRST_VARSEL_SENDT_DATO);

					assertThat(forsendelse.getVarsel().getSmsVarsel()).hasSize(1);
					assertThat(forsendelse.getVarsel().getSmsVarsel().get(0).getTelefonnummer()).isEqualTo(TELEFONNUMMER);
					assertThat(forsendelse.getVarsel().getSmsVarsel().get(0).getTekst()).isEqualTo(MELDING);
					assertThat(forsendelse.getVarsel().getSmsVarsel().get(0).getVarslingstidspunkt()).isEqualTo(FIRST_VARSEL_SENDT_DATO);


					assertNull(forsendelse.getPostadresse());
				});
	}

	@Test
	public void shouldMapPostadresseWhenDistribusjonKanalIsPRINT() {
		HentEkspederteForsendelserResponse ekspederteForsendelserResponse = new HentEkspederteForsendelserResponse(
				List.of(mapForsendelse(DOKUMENTINFO_PRINT))
		);

		assertThat(ekspederteForsendelserResponse.forsendelser())
				.hasSize(1)
				.allSatisfy(forsendelse -> {
					assertThat(forsendelse.getForsendelseId()).isEqualTo(DOKUMENTINFO_ID);
					assertThat(forsendelse.getJournalpostId()).isEqualTo(ARKIV_KODE);
					assertThat(forsendelse.getDistribusjonsKanal()).isEqualTo(PRINT);
					assertThat(forsendelse.getPostadresse().getAdresselinje1()).isEqualTo(ADRESSELINJE_1);
					assertThat(forsendelse.getPostadresse().getAdresselinje2()).isEqualTo(ADRESSELINJE_2);
					assertThat(forsendelse.getPostadresse().getAdresselinje3()).isEqualTo(ADRESSELINJE_3);
					assertThat(forsendelse.getPostadresse().getPostnummer()).isEqualTo(POSTNUMMER);
					assertThat(forsendelse.getPostadresse().getPoststed()).isEqualTo(POSTSTED);
					assertThat(forsendelse.getPostadresse().getLandkode()).isEqualTo(LANDKODE);
				});
	}

	@Test
	public void shouldMapNullPostadresseWhenDistribusjonKanalIsPRINT() {
		DokumentInfo dokumentInfo = DOKUMENTINFO_PRINT;
		dokumentInfo.setPostadresse(null);

		HentEkspederteForsendelserResponse ekspederteForsendelserResponse = new HentEkspederteForsendelserResponse(
				List.of(mapForsendelse(dokumentInfo))
		);

		assertThat(ekspederteForsendelserResponse.forsendelser())
				.hasSize(1)
				.allSatisfy(forsendelse -> {
					assertThat(forsendelse.getForsendelseId()).isEqualTo(DOKUMENTINFO_ID);
					assertThat(forsendelse.getJournalpostId()).isEqualTo(ARKIV_KODE);
					assertThat(forsendelse.getDistribusjonsKanal()).isEqualTo(PRINT);

					assertNull(forsendelse.getPostadresse());
				});
	}

}