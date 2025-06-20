package no.nav.dokdistadmin.domain;

import no.nav.dokdistadmin.exception.functional.UgyldigInputException;

import static java.lang.String.format;

public enum DistribusjonKanalCode {
	PRINT,
	SDP,
	SDP_PRINT,
	E_HANDEL,
	PRINT_DITTNAV,
	DITTNAV,
	TRYGDERETTEN,
	DPVT,
	DPO;

	public static DistribusjonKanalCode fromString(String distribusjonkanal) {
		try {
			return DistribusjonKanalCode.valueOf(distribusjonkanal.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new UgyldigInputException(format("%s er ikke en gyldig distribusjonkanal", distribusjonkanal));
		}
	}
}
