package no.nav.dokdistadmin.config;

import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.mock.oauth2.token.DefaultOAuth2TokenCallback;
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableMockOAuth2Server
public abstract class Oauth2Test {

	private static final String AZUREV2_ISSUER = "azurev2";
	private static final String AZP_NAME = "dev-fss:teamdokumenthandtering:dokdistadmin";


	@Autowired
	public MockOAuth2Server mockOAuth2Server;

	public String jwt() {
		return jwt(Map.ofEntries(Map.entry("azp_name", AZP_NAME)));

	}

	public String jwtWithoutAzpNameClaim() {
		return jwt(new HashMap<>());
	}

	private String jwt(Map<String, String> claims) {
		String audience = "gosys";
		return mockOAuth2Server.issueToken(
				AZUREV2_ISSUER,
				"gosys-clientid",
				new DefaultOAuth2TokenCallback(
						AZUREV2_ISSUER,
						"subject",
						"JWT",
						List.of(audience),
						claims,
						60
				)
		).serialize();
	}

}
