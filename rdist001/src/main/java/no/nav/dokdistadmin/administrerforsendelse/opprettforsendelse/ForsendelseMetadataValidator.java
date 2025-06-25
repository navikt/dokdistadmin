package no.nav.dokdistadmin.administrerforsendelse.opprettforsendelse;

import jakarta.validation.ConstraintValidator;
import no.nav.dokdistadmin.administrerforsendelse.forsendelser.OpprettForsendelseRequest;

public class ForsendelseMetadataValidator implements ConstraintValidator<ValiderForsendelseMetadata, OpprettForsendelseRequest> {

	@Override
	public boolean isValid(OpprettForsendelseRequest request, jakarta.validation.ConstraintValidatorContext context) {
		return isNullForsendelseMetadataAndType(request) || isNotNullForsendelseMetadataAndType(request);
	}

	private static boolean isNotNullForsendelseMetadataAndType(OpprettForsendelseRequest request) {
		return request.getForsendelseMetadata() != null && request.getForsendelseMetadataType() != null;
	}

	private static boolean isNullForsendelseMetadataAndType(OpprettForsendelseRequest request) {
		return request.getForsendelseMetadata() == null && request.getForsendelseMetadataType() == null;
	}
}
