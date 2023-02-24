package no.nav.dokdistadmin.administrerforsendelse.ekspederteforsendelser;

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
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.EPOSTADDRESS;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.FIRST_VARSEL_SENDT_DATO;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.LANDKODE;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.POSTNUMMER;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.POSTSTED;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.TELEFONNUMMER;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.VARSLINGSTEKST;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.VARSLINGSTITTEL;
import static no.nav.dokdistadmin.administrerforsendelse.TestUtils.createEkspederteForsendelser;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.DITTNAV;
import static no.nav.dokdistadmin.domain.DistribusjonKanalCode.PRINT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HentEkspederteForsendelserMapperTest {

	private final HentEkspederteForsendelserMapper mapper = new HentEkspederteForsendelserMapper();

	@Test
	public void shouldMapVarselWhenDistribusjonKodeIsDITTNAV() {
		HentEkspederteForsendelserResponse ekspederteForsendelserResponse = mapper.map(createEkspederteForsendelser());

		EkspederteForsendelse forsendelse = ekspederteForsendelserResponse.forsendelser().get(0);
		assertEquals(DOKUMENTINFO_ID_2, forsendelse.getForsendelseId());
		assertEquals(ARKIV_KODE_2, forsendelse.getJournalpostId());
		assertEquals(DITTNAV, forsendelse.getDistribusjonsKanal());
		assertNull(forsendelse.getDigitalpostkasse());
		assertEquals(EPOSTADDRESS, forsendelse.getVarsel().getEpostVarsel().getAdresse());
		assertEquals(VARSLINGSTITTEL, forsendelse.getVarsel().getEpostVarsel().getTittel());
		assertEquals(VARSLINGSTEKST, forsendelse.getVarsel().getEpostVarsel().getTekst());
		assertEquals(FIRST_VARSEL_SENDT_DATO, forsendelse.getVarsel().getEpostVarsel().getVarslingstidspunkt());


		assertEquals(TELEFONNUMMER, forsendelse.getVarsel().getSmsVarsel().getTelefonnummer());
		assertEquals(VARSLINGSTEKST, forsendelse.getVarsel().getSmsVarsel().getTekst());
		assertEquals(FIRST_VARSEL_SENDT_DATO, forsendelse.getVarsel().getSmsVarsel().getVarslingstidspunkt());

	}

	@Test
	public void shouldMapVarselInfoWhenDistribusjonKanalIsSDP() {
		HentEkspederteForsendelserResponse ekspederteForsendelserResponse = mapper.map(createEkspederteForsendelser());

		EkspederteForsendelse forsendelse = ekspederteForsendelserResponse.forsendelser().get(1);
		assertEquals(DOKUMENTINFO_ID, forsendelse.getForsendelseId());
		assertEquals(ARKIV_KODE, forsendelse.getJournalpostId());
		assertEquals(DistribusjonKanalCode.SDP, forsendelse.getDistribusjonsKanal());
		assertEquals(DIGITALPOSTKASSE_ADRESSE, forsendelse.getDigitalpostkasse().getDigitalpostkasseadresse());
		assertEquals(DIGITAL_DISTRIBUTOR_ID, forsendelse.getDigitalpostkasse().getDigitalpostkasseleverandor());
		assertEquals(EPOSTADDRESS, forsendelse.getVarsel().getEpostVarsel().getAdresse());
		assertEquals(VARSLINGSTITTEL, forsendelse.getVarsel().getEpostVarsel().getTittel());
		assertEquals(VARSLINGSTEKST, forsendelse.getVarsel().getEpostVarsel().getTekst());
		assertEquals(FIRST_VARSEL_SENDT_DATO, forsendelse.getVarsel().getEpostVarsel().getVarslingstidspunkt());

		assertEquals(TELEFONNUMMER, forsendelse.getVarsel().getSmsVarsel().getTelefonnummer());
		assertEquals(VARSLINGSTEKST, forsendelse.getVarsel().getSmsVarsel().getTekst());
		assertEquals(FIRST_VARSEL_SENDT_DATO, forsendelse.getVarsel().getSmsVarsel().getVarslingstidspunkt());
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