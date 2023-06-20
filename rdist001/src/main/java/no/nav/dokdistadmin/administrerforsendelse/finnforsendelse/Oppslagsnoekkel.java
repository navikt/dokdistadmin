package no.nav.dokdistadmin.administrerforsendelse.finnforsendelse;

public enum Oppslagsnoekkel {
	KONVERSASJONSID("konversasjonsId"),
	BESTILLINGSID("bestillingsId"),
	JOURNALPOSTID("journalpostId");

	private final String value;

	Oppslagsnoekkel(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
