package no.nav.dokdistadmin.domain;

/**
 * Valid codes for ArkivSystem.
 *
 * @author Thomas Eugen Bjørge, Visma Consulting
 */
public enum ArkivSystemCode {

	JOARK,
	MIDL_BREVLAGER,
	INGEN;


	public static ArkivSystemCode convertStringToArkivSystemCode(String value) {

		if (value == null || value.trim().isEmpty()) {
			return INGEN;
		}
		return ArkivSystemCode.valueOf(value);

	}
}
