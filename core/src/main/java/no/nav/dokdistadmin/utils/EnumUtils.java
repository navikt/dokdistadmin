package no.nav.dokdistadmin.utils;

import no.nav.dokdistadmin.exception.functional.UgyldigInputException;

import static java.lang.String.format;

public class EnumUtils {

	public static <E extends Enum<E>> E stringToEnum(Class<E> enumClass, String enumName) {
		try {
			return enumName == null ? null : Enum.valueOf(enumClass, enumName);
		} catch (IllegalArgumentException e) {
			throw new UgyldigInputException(format("Ugyldig input: %s er ikke en gyldig kodeverdi for %s", enumName, enumClass));
		}
	}

}
