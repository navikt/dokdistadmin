package no.nav.dokdistadmin.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "serviceuser")
public class ServiceuserProperties {
	@NotEmpty
	private String username;
	@NotEmpty
	private String password;
}
