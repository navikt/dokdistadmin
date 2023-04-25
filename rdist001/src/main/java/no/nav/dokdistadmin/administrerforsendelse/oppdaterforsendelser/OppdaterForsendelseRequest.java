package no.nav.dokdistadmin.administrerforsendelse.oppdaterforsendelser;

import lombok.Builder;
import lombok.Data;
import no.nav.dokdistadmin.domain.VarselStatusCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;


@Data
@Builder
@NotNull
public class OppdaterForsendelseRequest {
	@PositiveOrZero(message = "forsendelseId må ha en verdi")
	private Long forsendelseId;
	private String forsendelseStatus;
	private String konversasjonId;
	private VarselStatusCode varselStatus;
	private String digitalLeverandoeradresse;
	private String digitalPostkasseadresse;
}
