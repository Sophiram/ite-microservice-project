package kh.edu.istad.iam.infrastructure.security;

import java.time.Duration;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

@Configuration(proxyBeanMethods = false)
public class RegisteredClientInitializer implements ApplicationRunner {
    private final RegisteredClientRepository clients;
    private final PasswordEncoder passwordEncoder;
    private final String dcrClientId;
    private final String dcrClientSecret;
    private final String browserClientId;
    private final String browserRedirectUri;

    public RegisteredClientInitializer(RegisteredClientRepository clients, PasswordEncoder passwordEncoder,
            @Value("${iam.authorization-server.dcr-client-id:iam-dcr}") String dcrClientId,
            @Value("${iam.authorization-server.dcr-client-secret:}") String dcrClientSecret,
            @Value("${iam.authorization-server.browser-client-id:iam-browser}") String browserClientId,
            @Value("${iam.authorization-server.browser-redirect-uri:https://oauth.pstmn.io/v1/callback}")
            String browserRedirectUri) {
        this.clients = clients;
        this.passwordEncoder = passwordEncoder;
        this.dcrClientId = dcrClientId;
        this.dcrClientSecret = dcrClientSecret;
        this.browserClientId = browserClientId;
        this.browserRedirectUri = browserRedirectUri;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedBrowserClient();
        seedFrontBffClient();
        if (dcrClientSecret != null && !dcrClientSecret.isBlank()) seedDcrClient();
    }

    private void seedFrontBffClient() {
        String clientId = "front-bff-client";
        if (clients.findByClientId(clientId) != null) return;

        RegisteredClient frontBff = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:9999/login/oauth2/code/front-bff-client")
                .redirectUri("http://localhost:9092/login/oauth2/code/front-bff-client")
                .redirectUri("http://localhost:8080/login/oauth2/code/front-bff-client")
                .postLogoutRedirectUri("http://localhost:9999/")
                .postLogoutRedirectUri("http://localhost:9092/")
                .postLogoutRedirectUri("http://localhost:8080/")

                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(true) // Required for public clients (method: none)
                        .requireAuthorizationConsent(false) // Set true if you want the consent screen
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(15))
                        .refreshTokenTimeToLive(Duration.ofDays(7))
                        .reuseRefreshTokens(false)
                        .build())
                .build();

        clients.save(frontBff);
    }



    private void seedBrowserClient() {
        if (clients.findByClientId(browserClientId) != null) return;
        RegisteredClient browser = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(browserClientId)
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri(browserRedirectUri)
                .postLogoutRedirectUri("http://localhost:9000/")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.EMAIL)
                .scope(OidcScopes.ADDRESS)
                .scope("phone")
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(true)
                        .requireAuthorizationConsent(true)
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(15))
                        .refreshTokenTimeToLive(Duration.ofDays(14))
                        .reuseRefreshTokens(false)
                        .build())
                .build();
        clients.save(browser);
    }

    private void seedDcrClient() {
        if (clients.findByClientId(dcrClientId) != null) return;
        RegisteredClient dcr = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(dcrClientId)
                .clientSecret(passwordEncoder.encode(dcrClientSecret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("client.create")
                .scope("client.read")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
                .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofMinutes(5)).build())
                .build();
        clients.save(dcr);
    }
}
