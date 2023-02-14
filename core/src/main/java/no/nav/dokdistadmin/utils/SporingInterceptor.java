package no.nav.dokdistadmin.utils;

import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import no.nav.security.token.support.core.jwt.JwtTokenClaims;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;

import static no.nav.dokdistadmin.utils.MDCConstants.CALL_ID;
import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static no.nav.dokdistadmin.utils.NavHeaders.NAV_CALLID;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.truncate;

@Component
public class SporingInterceptor implements HandlerInterceptor {

	private static final String ISSUER_AZUREV2 = "azurev2";
	private static final String AZURE_NAV_CUSTOM_CLAIM_AZP_NAME = "azp_name";
	private static final String CLAIM_OID = "oid";
	private final TokenValidationContextHolder tokenValidationContextHolder;

	public SporingInterceptor(TokenValidationContextHolder tokenValidationContextHolder) {
		this.tokenValidationContextHolder = tokenValidationContextHolder;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		populateCallId(request);
		populateUserId();

		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		MDC.clear();
	}

	private void populateCallId(HttpServletRequest request) {
		final String navCallId = request.getHeader(NAV_CALLID);

		if (isNotBlank(navCallId)) {
			MDC.put(CALL_ID, navCallId);
		} else {
			MDC.put(CALL_ID, UUID.randomUUID().toString());
		}
	}

	private void populateUserId() {

		MDC.put(USER_ID, truncate(getUserId(), 20));
	}

	String getUserId() {
		JwtTokenClaims claims = tokenValidationContextHolder.getTokenValidationContext()
				.getJwtToken(ISSUER_AZUREV2)
				.getJwtTokenClaims();

		String azpNameClaim = claims.getStringClaim(AZURE_NAV_CUSTOM_CLAIM_AZP_NAME);

		if (isBlank(azpNameClaim)) {
			return claims.getStringClaim(CLAIM_OID);
		}

		return Arrays.stream(azpNameClaim.split(":"))
				.reduce((first, second) -> second)
				.orElseThrow();
	}

}
