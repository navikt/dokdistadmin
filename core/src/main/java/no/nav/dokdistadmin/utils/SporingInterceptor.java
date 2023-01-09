package no.nav.dokdistadmin.utils;

import no.nav.dokdistadmin.exception.functional.MissingClaimException;
import no.nav.security.token.support.core.context.TokenValidationContextHolder;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static no.nav.dokdistadmin.utils.MDCConstants.USER_ID;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class SporingInterceptor implements HandlerInterceptor {

	private static final String ISSUER_AZUREV2 = "azurev2";
	private static final String AZURE_NAV_CUSTOM_CLAIM_AZP_NAME = "azp_name";
	private final TokenValidationContextHolder tokenValidationContextHolder;

	public SporingInterceptor(TokenValidationContextHolder tokenValidationContextHolder) {
		this.tokenValidationContextHolder = tokenValidationContextHolder;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		handleMdc();

		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		MDC.clear();
	}

	public void handleMdc() {
		//TODO Finn ut hva vi gjør her dersom azp_name claimet mangler i token,
		// eller dersom det er lengre enn 20 tegn (som er max for endret_av kolonnen)
		MDC.put(USER_ID, getUserId());
	}

	public String getUserId() {
		String azp_name_claim = tokenValidationContextHolder.getTokenValidationContext()
				.getJwtToken(ISSUER_AZUREV2)
				.getJwtTokenClaims()
				.getStringClaim(AZURE_NAV_CUSTOM_CLAIM_AZP_NAME);

		if (isBlank(azp_name_claim)) {
			throw new MissingClaimException("Azure-token mangler 'azp_name' claim");
		}

		return Arrays.stream(azp_name_claim.split(":"))
				.reduce((first, second) -> second)
				.orElseThrow();
	}

}
