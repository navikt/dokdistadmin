package no.nav.dokdistadmin.domain;

public enum SikkerhetsnivaCode {
	NIVA_3("3"),
	NIVA_4("4");

	private String sdpSikkerhetsnivaa;

	SikkerhetsnivaCode(String sdpSikkerhetsnivaa) {
		this.sdpSikkerhetsnivaa = sdpSikkerhetsnivaa;
	}

	public String toSdpCode() {
		return sdpSikkerhetsnivaa;
	}

	public static SikkerhetsnivaCode fromValue(String value) {
		for (SikkerhetsnivaCode sikkerhetsnivaCode : values()) {
			if (sikkerhetsnivaCode.toSdpCode().equals(value)) {
				return sikkerhetsnivaCode;
			}
		}
		throw new IllegalArgumentException("SikkerhetsnivaCode - value not supported: " + value);
	}


}
