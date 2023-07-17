package no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser;

import lombok.Builder;
import lombok.Data;
import no.nav.dokdistadmin.domain.VarselStatusCode;

import jakarta.validation.constraints.NotNull;


@Data
@Builder
public class OppdaterForsendelseRequest {
	@NotNull(message = "forsendelseId må ha en verdi")
	private Long forsendelseId;
	private String forsendelseStatus;
	private String konversasjonId;
	private VarselStatusCode varselStatus;
	private String digitalLeverandoeradresse;
	private String digitalPostkasseadresse;
}
