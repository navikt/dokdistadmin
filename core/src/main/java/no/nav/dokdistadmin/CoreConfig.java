package no.nav.dokdistadmin;

import no.nav.dokdistadmin.config.DokdistadminProperties;
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
		DokdistadminProperties.class
})
@EnableJwtTokenValidation(ignore = {"org.springframework", "org.springdoc"})
public class CoreConfig {
}
