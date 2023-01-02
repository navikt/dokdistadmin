package no.nav.dokdistadmin.administrerforsendelse.map;

import no.nav.dokdistadmin.administrerforsendelse.EkspederteForsendelse;
import no.nav.dokdistadmin.administrerforsendelse.HentEkspederteForsendelserResponse;
import no.nav.dokdistadmin.domain.DistribusjonKanalCode;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.ADRESSELINJE_1;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.ADRESSELINJE_2;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.ADRESSELINJE_3;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.ARKIV_KODE;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.ARKIV_KODE_2;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.DIGITALPOSTKASSE_ADRESSE;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.DIGITAL_DISTRIBUTOR_ID;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.DOKUMENTINFO_ID;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.DOKUMENTINFO_ID_2;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.LANDKODE;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.POSTNUMMER;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.POSTSTED;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.createEkspederteForsendelser;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.DITTNAV;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HentEkspederteForsendelserMapperTest {

	private static final String DIGITAL_KONTAKTINFORMASJON = "{\"epost\":\"epostaddress0@nav.no\",\"sms\":\"11111111\"}";
	private static final String VARSELTEKST = "{\"epost\":\"Du har fått brev fra NAV\",\"sms\":\"Du har fått brev fra NAV\"}";

	private final HentEkspederteForsendelserMapper mapper = new HentEkspederteForsendelserMapper();

	@Test
	public void shouldMapVarselWhenDistribusjonKodeIsDITTNAV() {
		HentEkspederteForsendelserResponse ekspederteForsendelserResponse = mapper.map(createEkspederteForsendelser());

		EkspederteForsendelse forsendelse = ekspederteForsendelserResponse.forsendelser().get(0);
		assertEquals(DOKUMENTINFO_ID_2, forsendelse.getForsendelseId());
		assertEquals(ARKIV_KODE_2, forsendelse.getJournalpostId());
		assertEquals(DITTNAV, forsendelse.getDistribusjonsKanal());
		assertNull(forsendelse.getDigitalpostkasse());
		assertEquals(DIGITAL_KONTAKTINFORMASJON, forsendelse.getVarsel().getDigitalkontaktinformasjon());
		assertEquals(VARSELTEKST, forsendelse.getVarsel().getVarseltekst());
	}

	@Test
	public void shouldMapDigitalpostkasseWhenDistribusjonKanalIsSDP() {
		HentEkspederteForsendelserResponse ekspederteForsendelserResponse = mapper.map(createEkspederteForsendelser());

		EkspederteForsendelse forsendelse = ekspederteForsendelserResponse.forsendelser().get(1);
		assertEquals(DOKUMENTINFO_ID, forsendelse.getForsendelseId());
		assertEquals(ARKIV_KODE, forsendelse.getJournalpostId());
		assertEquals(DistribusjonKanalCode.SDP, forsendelse.getDistribusjonsKanal());
		assertEquals(DIGITALPOSTKASSE_ADRESSE, forsendelse.getDigitalpostkasse().getDigitalpostkasseadresse());
		assertEquals(DIGITAL_DISTRIBUTOR_ID, forsendelse.getDigitalpostkasse().getDigitalpostkasseleverandor());
		assertNull(forsendelse.getVarsel());
		assertNull(forsendelse.getPostadresse());
	}

	@Test
	public void shouldMapPostadresseWhenDistribusjonKanalIsPRINT() {
		HentEkspederteForsendelserResponse ekspederteForsendelserResponse = mapper.map(createEkspederteForsendelser());

		EkspederteForsendelse forsendelse = ekspederteForsendelserResponse.forsendelser().get(2);
		assertEquals(DOKUMENTINFO_ID, forsendelse.getForsendelseId());
		assertEquals(ARKIV_KODE, forsendelse.getJournalpostId());
		assertEquals(PRINT, forsendelse.getDistribusjonsKanal());
		assertEquals(ADRESSELINJE_1, forsendelse.getPostadresse().getAdresselinje1());
		assertEquals(ADRESSELINJE_2, forsendelse.getPostadresse().getAdresselinje2());
		assertEquals(ADRESSELINJE_3, forsendelse.getPostadresse().getAdresselinje3());
		assertEquals(POSTNUMMER, forsendelse.getPostadresse().getPostnummer());
		assertEquals(POSTSTED, forsendelse.getPostadresse().getPoststed());
		assertEquals(LANDKODE, forsendelse.getPostadresse().getLandkode());
	}

	@Test
	public void shouldMapNullPostadresseWhenDistribusjonKanalIsPRINT() {
		HentEkspederteForsendelserResponse ekspederteForsendelserResponse = mapper.map(
				createEkspederteForsendelser().stream().peek(f -> f.setPostadresse(null)).toList());

		EkspederteForsendelse forsendelse = ekspederteForsendelserResponse.forsendelser().get(2);
		assertEquals(DOKUMENTINFO_ID, forsendelse.getForsendelseId());
		assertEquals(ARKIV_KODE, forsendelse.getJournalpostId());
		assertEquals(PRINT, forsendelse.getDistribusjonsKanal());
		assertNull(forsendelse.getPostadresse());
	}

	@Test
	public void shouldMapToEmptyListWhenEmptyInput() {
		HentEkspederteForsendelserResponse ekspederteForsendelserResponse = mapper.map(Collections.emptyList());

		assertTrue(ekspederteForsendelserResponse.forsendelser().isEmpty());
	}
}