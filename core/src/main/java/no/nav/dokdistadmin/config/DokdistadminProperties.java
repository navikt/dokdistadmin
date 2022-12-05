package no.nav.dokdistadmin.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("dokdistadmin")
public class DokdistadminProperties {

	private final Serviceuser serviceuser = new Serviceuser();
	private final AzureEndpoint mqgateway = new AzureEndpoint();

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
}
