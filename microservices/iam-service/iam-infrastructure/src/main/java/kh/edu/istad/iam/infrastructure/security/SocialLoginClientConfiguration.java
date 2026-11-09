package kh.edu.istad.iam.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Configuration(proxyBeanMethods = false)
public class SocialLoginClientConfiguration {
    private static final String CALLBACK = "{baseUrl}/login/oauth2/code/{registrationId}";

    @Bean
    ClientRegistrationRepository iamClientRegistrationRepository(
            @Value("${GOOGLE_CLIENT_ID:iam-google-client-id-not-configured}") String googleClientId,
            @Value("${GOOGLE_CLIENT_SECRET:iam-google-client-secret-not-configured}") String googleClientSecret,
            @Value("${GITHUB_CLIENT_ID:iam-github-client-id-not-configured}") String githubClientId,
            @Value("${GITHUB_CLIENT_SECRET:iam-github-client-secret-not-configured}") String githubClientSecret,
            @Value("${FACEBOOK_CLIENT_ID:iam-facebook-client-id-not-configured}") String facebookClientId,
            @Value("${FACEBOOK_CLIENT_SECRET:iam-facebook-client-secret-not-configured}") String facebookClientSecret,
            @Value("${FACEBOOK_GRAPH_API_VERSION:v26.0}") String facebookGraphVersion,
            @Value("${TELEGRAM_CLIENT_ID:iam-telegram-client-id-not-configured}") String telegramClientId,
            @Value("${TELEGRAM_CLIENT_SECRET:iam-telegram-client-secret-not-configured}") String telegramClientSecret) {
        ClientRegistration google = oidc("google", googleClientId, googleClientSecret, "Google",
                "https://accounts.google.com", "https://accounts.google.com/o/oauth2/v2/auth",
                "https://oauth2.googleapis.com/token", "https://www.googleapis.com/oauth2/v3/certs",
                "https://openidconnect.googleapis.com/v1/userinfo", "sub", "openid", "profile", "email");

        ClientRegistration github = oauth2("github", githubClientId, githubClientSecret, "GitHub",
                "https://github.com/login/oauth/authorize", "https://github.com/login/oauth/access_token",
                "https://api.github.com/user", "login", "read:user", "user:email");

        String graph = "https://graph.facebook.com/" + facebookGraphVersion;
        ClientRegistration facebook = ClientRegistration.withRegistrationId("facebook")
                .clientId(facebookClientId)
                .clientSecret(facebookClientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(CALLBACK)
                .clientName("Facebook")
                .authorizationUri("https://www.facebook.com/" + facebookGraphVersion + "/dialog/oauth")
                .tokenUri(graph + "/oauth/access_token")
                .userInfoUri(graph + "/me?fields=id,name,email,picture.type(large)")
                .userNameAttributeName("id")
                .scope("email", "public_profile")
                .build();

        ClientRegistration telegram = oidc("telegram", telegramClientId, telegramClientSecret, "Telegram",
                "https://oauth.telegram.org", "https://oauth.telegram.org/auth", "https://oauth.telegram.org/token",
                "https://oauth.telegram.org/.well-known/jwks.json", null, "sub", "openid", "profile", "phone");

        return new InMemoryClientRegistrationRepository(google, github, facebook, telegram);
    }

    private static ClientRegistration oidc(String id, String clientId, String secret, String name, String issuer,
            String authorizationUri, String tokenUri, String jwkSetUri, String userInfoUri, String nameAttribute,
            String... scopes) {
        ClientRegistration.Builder builder = base(id, clientId, secret, name)
                .issuerUri(issuer)
                .authorizationUri(authorizationUri)
                .tokenUri(tokenUri)
                .jwkSetUri(jwkSetUri)
                .userNameAttributeName(nameAttribute)
                .scope(scopes);
        if (userInfoUri != null) builder.userInfoUri(userInfoUri);
        return builder.build();
    }

    private static ClientRegistration oauth2(String id, String clientId, String secret, String name,
            String authorizationUri, String tokenUri, String userInfoUri, String nameAttribute, String... scopes) {
        return base(id, clientId, secret, name)
                .authorizationUri(authorizationUri)
                .tokenUri(tokenUri)
                .userInfoUri(userInfoUri)
                .userNameAttributeName(nameAttribute)
                .scope(scopes)
                .build();
    }

    private static ClientRegistration.Builder base(String id, String clientId, String secret, String name) {
        return ClientRegistration.withRegistrationId(id)
                .clientId(clientId)
                .clientSecret(secret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(CALLBACK)
                .clientName(name);
    }
}
