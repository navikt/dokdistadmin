package no.nav.dokdistadmin.azure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Data
@Validated
@ConfigurationProperties("azure")
public class Azureproperties {
	@NotEmpty
	private String openidConfigTokenEndpoint;
	@NotEmpty
	private String appClientId;
	@NotEmpty
	private String appClientSecret;
}
