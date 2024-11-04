package no.nav.dokdistadmin.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import no.nav.dokdistadmin.domain.ModusCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("dokdistadmin")
public class DokdistadminProperties {

	private final Database database = new Database();

	@NotNull
	private ModusCode modus;

	@Data
	@Validated
	public static class Database {
		/**
		 * Utregning av statisk pool-verdi for dokumentdistribusjon-databasen.
		 * 	Optimizing UCP behaviour https://docs.oracle.com/database/121/JJUCP/optimize.htm#JJUCP8143
		 * 	About Optimizing Real-World Performance with Static Connection Pools
		 * 	https://docs.oracle.com/en/database/oracle/oracle-database/19/jjucp/optimizing-real-world-performance.html
		 * select STAT_NAME, to_char(VALUE) as VALUE, COMMENTS from v$osstat where stat_name IN ('NUM_CPUS','NUM_CPU_CORES','NUM_CPU_SOCKETS');
		 * 	NUM_CPU i Dokumentdistribusjon-produksjon er 96.
		 * 	Anbefalt av Oracle: 1-10 koblinger / CPU.
		 * 	Max connections: 960
		 * Andre apper som bruker dokumentdistribusjon-db:
		 * 	Dokumentdistribusjon (https://github.com/navikt/dokumentdistribusjon/blob/master/dokdist-config/src/main/resources/app-config.xml)
		 * 	Reserverte koblinger: 50 + 50 (XADS) = 100
		 * Rest koblinger: 960 (max) - 100 (Dokumentdistribusjon) = 840
		 * Dokdistadmin statisk pool (denne appen) er max 840 koblinger.
		 * 	Dokdistadmin pods: 10 (naiserator.yaml)
		 * 	Dokdistadmin koblinger / pods = 840 / 10 = ~84. La oss si 80. (p-config.json)
		 * @see RepositoryConfig
		 */
		@Positive
		private int poolsize = 80;

		@NotEmpty
		private String schema;

		private String onshosts;
	}
}
