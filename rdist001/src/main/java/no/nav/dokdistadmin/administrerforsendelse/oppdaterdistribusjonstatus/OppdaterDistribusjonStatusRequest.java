package no.nav.dokdistadmin.administrerforsendelse.oppdaterdistribusjonstatus;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import static org.apache.commons.lang3.StringUtils.strip;
import static org.apache.commons.lang3.StringUtils.truncate;

@Builder
public record OppdaterDistribusjonStatusRequest(
		@NotBlank(message = "distribusjonId må ha en verdi")
		String distribusjonId,

		@NotBlank(message = "distribusjonstatus må ha en verdi")
		String distribusjonstatus,

		@NotBlank(message = "dokumentstatus må ha en verdi")
		String dokumentstatus,

		@NotBlank(message = "kilde må ha en verdi")
		String kilde
) {
	public OppdaterDistribusjonStatusRequest {
		kilde = truncate(strip(kilde), 20);
	}
}
