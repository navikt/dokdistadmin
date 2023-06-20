package no.nav.dokdistadmin.domain;

import no.nav.dokdistadmin.exception.functional.UgyldigInputException;

import static java.lang.String.format;

public enum Oppslagsnoekkel {
	KONVERSASJONSID("konversasjonsId"),
	BESTILLINGSID("bestillingsId"),
	JOURNALPOSTID("journalpostId");

	public final String value;

	Oppslagsnoekkel(String value) {
		this.value = value;
	}

	public static Oppslagsnoekkel fromString(String oppslagsnoekkel) {
		try {
			return Oppslagsnoekkel.valueOf(oppslagsnoekkel.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new UgyldigInputException(format("%s er ikke en gyldig oppslagsnøkkel", oppslagsnoekkel));
		}
	}

}
