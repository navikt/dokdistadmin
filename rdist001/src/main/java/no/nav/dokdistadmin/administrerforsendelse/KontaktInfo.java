package no.nav.dokdistadmin.administrerforsendelse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KontaktInfo {
	private String tekstMelding;
	private String digitalKontakt;
}
