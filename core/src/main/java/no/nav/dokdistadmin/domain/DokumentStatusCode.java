package no.nav.dokdistadmin.domain;

/**
 * Valid codes for DokumentStatus.
 *
 * @author Thomas Eugen Bjørge, Visma Consulting
 */
public enum DokumentStatusCode {

	OPPRETTET,
	OVERSENDT,
	EKSPEDERT,
	FEILET,
	RETURPOSTBEHANDLET,
	BEKREFTET,
	KLAR_FOR_DIST
}
