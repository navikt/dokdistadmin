package no.nav.dokdistadmin.config;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties("dokdistadmin")
public class DokdistadminProperties {

	private final Serviceuser serviceuser = new Serviceuser();
	private final AzureEndpoint mqgateway = new AzureEndpoint();
	private final Database database = new Database();


	@Data
	@Validated
	public static class Serviceuser {
		@NotEmpty
		private String username;
		@NotEmpty
		@ToString.Exclude
		private String password;
	}

	@Data
	@Validated
	public static class AzureEndpoint {
		@NotEmpty
		private String url;
		@NotEmpty
		private String scope;
	}

	@Data
	@Validated
	public static class Database {
		/**
		 * Statisk pool verdi for dokkat databasen.
		 * <p>
		 * Optimizing UCP behaviour https://docs.oracle.com/database/121/JJUCP/optimize.htm#JJUCP8143
		 * About Optimizing Real-World Performance with Static Connection Pools
		 * https://docs.oracle.com/en/database/oracle/oracle-database/19/jjucp/optimizing-real-world-performance.html
		 * select STAT_NAME, to_char(VALUE) as VALUE, COMMENTS from v$osstat where stat_name IN ('NUM_CPUS','NUM_CPU_CORES','NUM_CPU_SOCKETS');
		 * Dokmet har et tak på 500 tilkoblinger
		 * Poolsize * max_pods må altså ikke overstige 500
		 * Current er satt til max 60 * 4 = 240
		 * @see no.nav.dokdistadmin.config.RepositoryConfig
		 */
		@Positive
		private int poolsize = 60;
	}
}
