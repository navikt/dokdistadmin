package no.nav.dokdistadmin.administrerforsendelse.varselinfo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class OppdaterVarselInfoRequest {

	@Positive(message = "forsendelseId må være et positivt tall")
	Long forsendelseId;

	@Valid
	@NotEmpty(message = "notifikasjoner må inneholde minst en notifikasjon")
	List<Notifikasjon> notifikasjoner;

}
