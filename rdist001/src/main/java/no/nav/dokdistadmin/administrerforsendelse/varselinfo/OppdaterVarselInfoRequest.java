package no.nav.dokdistadmin.administrerforsendelse.varselinfo;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;

@Data
@Builder
public class OppdaterVarselInfoRequest {

	@Positive(message = "forsendelseId må være et positivt tall")
	private Long forsendelseId;

	@Valid
	@NotEmpty(message = "notifikasjoner må inneholde minst en notifikasjon")
	private List<Notifikasjon> notifikasjoner;

}
