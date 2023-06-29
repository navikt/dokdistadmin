package no.nav.dokdistadmin.administrerforsendelse;

import no.nav.dokdistadmin.exception.functional.ValideringFeiletException;

import java.util.List;
import java.util.function.Function;

public class AdministrertForsendelseUtil {
	static <U> List<U> mapListToEnumValues(String paramname, Function<String, U> enumMapper, List<String> inputs) {
		return inputs.stream()
				.map(type -> safelyMapToEnum(paramname, enumMapper, type))
				.toList();
	}

	static <U> U safelyMapToEnum(String paramname, Function<String, U> enumMapper, String input) {
		try {
			return enumMapper.apply(input.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new ValideringFeiletException("\"" + input + "\" er ikke en gyldig verdi for parameteret \"" + paramname + "\"");
		}
	}
}