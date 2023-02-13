package no.nav.dokdistadmin.administrerforsendelse.varselinfo;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
@Builder
public class OppdaterVarselInfoRequest {

	@Positive(message = "forsendelseId må være et positivt tall")
	private Long forsendelseId;

	@NotEmpty(message = "notifikasjonList må innehold minst en notifikasjon")
	private List<Notifikasjon> notifikasjonList;

}
