package no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser;

import no.nav.dokdistadmin.domain.DokumentInfo;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.ADRESSELINJE_1;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.ADRESSELINJE_2;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.ADRESSELINJE_3;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.ARKIV_KODE;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.DIGITALPOSTKASSE_ADRESSE;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.DIGITAL_DISTRIBUTOR_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.DOKUMENTINFO_ID;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.EPOSTADDRESS;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.LANDKODE;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.MELDING;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.POSTNUMMER;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.POSTSTED;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.TELEFONNUMMER;
import static no.nav.dokdistadmin.administrerforsendelse.Rdist001TestUtils.createDokumentInfoWithDistribusjonKanal;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.DITTNAV;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.SDP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HentEkspederteForsendelserMapperTest {

	private static final String DIGITAL_KONTAKTINFORMASJON = String.format("{\"epost\":\"%s\",\"sms\":\"%s\"}", EPOSTADDRESS, TELEFONNUMMER);
	private static final String VARSELTEKST = String.format("{\"epost\":\"%s\",\"sms\":\"%s\"}", MELDING, MELDING);

	private final DokumentInfo DOKUMENTINFO_DITTNAV = createDokumentInfoWithDistribusjonKanal(DITTNAV);
	private final DokumentInfo DOKUMENTINFO_SDP = createDokumentInfoWithDistribusjonKanal(SDP);
	private final DokumentInfo DOKUMENTINFO_PRINT = createDokumentInfoWithDistribusjonKanal(PRINT);

	private final HentEkspederteForsendelserMapper mapper = new HentEkspederteForsendelserMapper();

	@Test
	public void shouldMapVarselWhenDistribusjonKodeIsDITTNAV() {
		HentEkspederteForsendelserResponse ekspederteForsendelserResponse = mapper.map(List.of(DOKUMENTINFO_DITTNAV));

		assertThat(ekspederteForsendelserResponse.forsendelser())
				.hasSize(1)
				.allSatisfy(forsendelse -> {
					assertThat(forsendelse.getForsendelseId()).isEqualTo(DOKUMENTINFO_ID);
					assertThat(forsendelse.getJournalpostId()).isEqualTo(ARKIV_KODE);
					assertThat(forsendelse.getDistribusjonsKanal()).isEqualTo(DITTNAV);
					assertThat(forsendelse.getVarsel().getDigitalkontaktinformasjon()).isEqualTo(DIGITAL_KONTAKTINFORMASJON);
					assertThat(forsendelse.getVarsel().getVarseltekst()).isEqualTo(VARSELTEKST);

					assertNull(forsendelse.getDigitalpostkasse());
				});
	}

	@Test
	public void shouldMapDigitalpostkasseWhenDistribusjonKanalIsSDP() {
		HentEkspederteForsendelserResponse ekspederteForsendelserResponse = mapper.map(List.of(DOKUMENTINFO_SDP));

		assertThat(ekspederteForsendelserResponse.forsendelser())
				.hasSize(1)
				.allSatisfy(forsendelse -> {
					assertThat(forsendelse.getForsendelseId()).isEqualTo(DOKUMENTINFO_ID);
					assertThat(forsendelse.getJournalpostId()).isEqualTo(ARKIV_KODE);
					assertThat(forsendelse.getDistribusjonsKanal()).isEqualTo(SDP);
					assertThat(forsendelse.getDigitalpostkasse().getDigitalpostkasseadresse()).isEqualTo(DIGITALPOSTKASSE_ADRESSE);
					assertThat(forsendelse.getDigitalpostkasse().getDigitalpostkasseleverandor()).isEqualTo(DIGITAL_DISTRIBUTOR_ID);

					assertNull(forsendelse.getVarsel());
					assertNull(forsendelse.getPostadresse());
				});
	}

	@Test
	public void shouldMapPostadresseWhenDistribusjonKanalIsPRINT() {
		HentEkspederteForsendelserResponse ekspederteForsendelserResponse = mapper.map(List.of(DOKUMENTINFO_PRINT));

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

		HentEkspederteForsendelserResponse ekspederteForsendelserResponse = mapper.map(List.of(dokumentInfo));

		assertThat(ekspederteForsendelserResponse.forsendelser())
				.hasSize(1)
				.allSatisfy(forsendelse -> {
					assertThat(forsendelse.getForsendelseId()).isEqualTo(DOKUMENTINFO_ID);
					assertThat(forsendelse.getJournalpostId()).isEqualTo(ARKIV_KODE);
					assertThat(forsendelse.getDistribusjonsKanal()).isEqualTo(PRINT);

					assertNull(forsendelse.getPostadresse());
				});
	}

	@Test
	public void shouldMapToEmptyListWhenEmptyInput() {
		HentEkspederteForsendelserResponse ekspederteForsendelserResponse = mapper.map(Collections.emptyList());

		assertThat(ekspederteForsendelserResponse.forsendelser()).isEmpty();
	}
}