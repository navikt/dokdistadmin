package no.nav.dokdistadmin.config;

import no.nav.dokdistadmin.CoreConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import({
		CoreConfig.class,
		JmsTestConfig.class
})
@Profile("itest")
@EnableAutoConfiguration
public class ApplicationTestConfig {
}
