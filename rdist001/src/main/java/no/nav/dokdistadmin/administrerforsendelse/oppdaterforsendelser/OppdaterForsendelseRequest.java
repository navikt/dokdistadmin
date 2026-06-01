package no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import no.nav.dokdistadmin.domain.ForsendelseMetadataTypeCode;
import no.nav.dokdistadmin.domain.VarselStatusCode;

import java.time.LocalDateTime;


@Value
@Builder
public class OppdaterForsendelseRequest {
	@NotNull(message = "forsendelseId må ha en verdi")
	Long forsendelseId;
	String forsendelseStatus;
	LocalDateTime ekspedertDato;
	String konversasjonId;
	VarselStatusCode varselStatus;
	String digitalLeverandoeradresse;
	String digitalPostkasseadresse;
	byte[] forsendelseMetadata;
	ForsendelseMetadataTypeCode forsendelseMetadataType;
}
