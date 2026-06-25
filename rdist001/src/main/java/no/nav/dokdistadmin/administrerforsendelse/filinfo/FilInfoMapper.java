package no.nav.dokdistadmin.administrerforsendelse.filinfo;

import no.nav.dokdistadmin.domain.FilInfo;
import no.nav.dokdistadmin.domain.FilStatusCode;
import no.nav.dokdistadmin.domain.FilTypeCode;
import no.nav.dokdistadmin.domain.KommunikasjonRetningCode;
import no.nav.dokdistadmin.exception.functional.UgyldigInputException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import static no.nav.dokdistadmin.domain.FilTypeCode.BEST_BEKR_PRINT;
import static no.nav.dokdistadmin.domain.FilTypeCode.BEST_INFO_PRINT;
import static no.nav.dokdistadmin.domain.FilTypeCode.DOK_RAPP_PRINT;
import static no.nav.dokdistadmin.domain.FilTypeCode.PRINTFIL;
import static no.nav.dokdistadmin.domain.KildeTypeCode.SITS;
import static no.nav.dokdistadmin.domain.KommunikasjonRetningCode.INNGAENDE;
import static no.nav.dokdistadmin.domain.KommunikasjonRetningCode.UTGAENDE;

public class FilInfoMapper {

	public static final Set<FilTypeCode> INNGAENDE_FILTYPER = EnumSet.of(DOK_RAPP_PRINT, BEST_BEKR_PRINT);
	public static final Set<FilTypeCode> UTGAENDE_FILTYPER = EnumSet.of(BEST_INFO_PRINT, PRINTFIL);

	public static FilInfo mapTilFilInfo(FilInfoRequest request) {
		FilTypeCode filType = FilTypeCode.valueOf(request.filtype());

		return FilInfo.builder()
				.filnavn(request.filnavn())
				.mottattDato(erInngaendeFilType(filType) ? LocalDateTime.now() : null)
				.sendtDato(erUtgaendeFilType(filType) ? LocalDateTime.now() : null)
				.filType(filType)
				.kommunikasjonRetning(resolveKommunikasjonRetning(filType))
				.filStatus(FilStatusCode.valueOf(request.status()))
				.kildeType(SITS)
				.build();
	}

	private static boolean erInngaendeFilType(FilTypeCode filType) {
		return INNGAENDE_FILTYPER.contains(filType);
	}

	private static boolean erUtgaendeFilType(FilTypeCode filType) {
		return UTGAENDE_FILTYPER.contains(filType);
	}

	private static KommunikasjonRetningCode resolveKommunikasjonRetning(FilTypeCode filType) {
		if (INNGAENDE_FILTYPER.contains(filType)) {
			return INNGAENDE;
		}

		if (UTGAENDE_FILTYPER.contains(filType)) {
			return UTGAENDE;
		}

		throw new UgyldigInputException("filtype må være en av %s".formatted(Arrays.toString(FilTypeCode.values())));
	}

	private FilInfoMapper() {
	}
}
