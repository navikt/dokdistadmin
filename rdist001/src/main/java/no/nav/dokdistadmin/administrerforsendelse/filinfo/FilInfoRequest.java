package no.nav.dokdistadmin.administrerforsendelse.filinfo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record FilInfoRequest(
		Long filInfoId,
		@Size(max = 255, message = "filnavn kan ikke være lengre enn 255 tegn")
		String filnavn,
		String filtype,
		@NotBlank(message = "status må ha en verdi")
		String status,
		@NotBlank
		@Size(max = 20, message = "kilde kan ikke være lengre enn 20 tegn")
		String kilde
) {
}
