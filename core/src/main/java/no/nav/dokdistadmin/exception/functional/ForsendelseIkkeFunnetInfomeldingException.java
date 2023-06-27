package no.nav.dokdistadmin.exception.functional;

// Exception skal kun bli brukt når dokdistdittnav kaller finnForsendelse med journalpostId som nøkkel, og ikke får treff i databasen.
// Dette er forventet oppførsel når distribusjoner er opprettet av bdist001, og journalposten er opprettet i dokarkiv av bjoark017.
public class ForsendelseIkkeFunnetInfomeldingException extends DokdistadminFunctionalException {

	public ForsendelseIkkeFunnetInfomeldingException(String message) {
		super(message);
	}
}
