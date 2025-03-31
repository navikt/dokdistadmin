package no.nav.dokdistadmin.utils;

import no.nav.dokdistadmin.config.AbstractOauth2Test;
import no.nav.security.token.support.core.context.TokenValidationContext;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.jwt.JwtToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {SporingInterceptor.class})
@ActiveProfiles({"itest"})
class SporingInterceptorTest extends AbstractOauth2Test {

	@Autowired
	private SporingInterceptor sporingInterceptor;

	@MockitoBean
	private TokenValidationContextHolder tokenValidationContextHolder;

	@Test
	void shouldGetUserIdAsAzpName() {
		setupTokenValidationContext(jwt());

		assertEquals("dokdistadmin", sporingInterceptor.getUserId());
	}

	@Test
	void shouldGetUserIdAsOID() {
		setupTokenValidationContext(jwtWithoutAzpNameClaim());

		assertEquals(OID, sporingInterceptor.getUserId());
	}

	private void setupTokenValidationContext(String tokenAsString) {
		Map<String, JwtToken> tokenMap = new HashMap<>();
		JwtToken token = new JwtToken(tokenAsString);
		tokenMap.put("azurev2", token);
		TokenValidationContext context = new TokenValidationContext(tokenMap);
		when(tokenValidationContextHolder.getTokenValidationContext()).thenReturn(context);
	}

}