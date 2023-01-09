package no.nav.dokdistadmin.utils;

import no.nav.dokdistadmin.config.ApplicationTestConfig;
import no.nav.dokdistadmin.config.Oauth2Test;
import no.nav.dokdistadmin.exception.functional.MissingClaimException;
import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.jwt.JwtToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ApplicationTestConfig.class})
@ActiveProfiles({"itest"})
class SporingInterceptorTest extends Oauth2Test {

	@Autowired
	private SporingInterceptor sporingInterceptor;

	@MockBean
	private TokenValidationContextHolder tokenValidationContextHolder;

	@Test
	void shouldGetUserId() {
		setupTokenValidationContext(jwt());

		assertEquals("dokdistadmin", sporingInterceptor.getUserId());
	}

	@Test
	void shouldThrowOnMissingAzpNameClaim() {
		setupTokenValidationContext(jwtWithoutAzpNameClaim());

		var result = assertThrows(MissingClaimException.class, () -> sporingInterceptor.getUserId());

		assertTrue(result.getMessage().contains("Azure-token mangler 'azp_name' claim"));
	}

	private void setupTokenValidationContext(String tokenAsString) {
		Map<String, JwtToken> tokenMap = new HashMap<>();
		JwtToken token = new JwtToken(tokenAsString);
		tokenMap.put("azurev2", token);
		TokenValidationContext context = new TokenValidationContext(tokenMap);
		when(tokenValidationContextHolder.getTokenValidationContext()).thenReturn(context);
	}

}