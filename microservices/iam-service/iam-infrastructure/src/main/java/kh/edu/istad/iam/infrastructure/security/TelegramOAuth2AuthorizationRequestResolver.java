package kh.edu.istad.iam.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

public class TelegramOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
    private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;
    private final String configuredOrigin;

    public TelegramOAuth2AuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository,
            String configuredOrigin) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, "/oauth2/authorization");
        this.configuredOrigin = configuredOrigin;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
        return customize(req, request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request, clientRegistrationId);
        return customize(req, request);
    }

    private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest req, HttpServletRequest request) {
        if (req == null) {
            return null;
        }
        String regId = req.getAttribute(OAuth2ParameterNames.REGISTRATION_ID);
        if ("telegram".equalsIgnoreCase(regId)) {
            String origin = (configuredOrigin != null && !configuredOrigin.isBlank()) ? configuredOrigin : null;
            if (origin == null) {
                if (req.getRedirectUri() != null && !req.getRedirectUri().isBlank()) {
                    URI uri = URI.create(req.getRedirectUri());
                    origin = uri.getScheme() + "://" + uri.getAuthority();
                } else {
                    origin = request.getScheme() + "://" + request.getHeader("host");
                }
            }
            Map<String, Object> additional = new LinkedHashMap<>(req.getAdditionalParameters());
            additional.put("origin", origin);
            additional.put("bot_id", req.getClientId());

            return OAuth2AuthorizationRequest.from(req)
                    .additionalParameters(additional)
                    .build();
        }
        return req;
    }
}
