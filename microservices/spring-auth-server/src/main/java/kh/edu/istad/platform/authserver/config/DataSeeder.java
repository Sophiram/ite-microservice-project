package kh.edu.istad.platform.authserver.config;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import kh.edu.istad.platform.authserver.domain.UserAccount;
import kh.edu.istad.platform.authserver.repository.UserAccountRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

/**
 * Inserts demo users and OAuth2 clients into PostgreSQL on first start (skipped when they exist).
 * Replace this with your own admin tooling for anything beyond local development.
 */
@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserAccountRepository users;
    private final RegisteredClientRepository clients;
    private final PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${order.client.secret:${ORDER_CLIENT_SECRET:order-secret}}")
    private String orderClientSecret;

    public DataSeeder(UserAccountRepository users, RegisteredClientRepository clients,
            PasswordEncoder passwordEncoder) {
        this.users = users;
        this.clients = clients;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedUser("user", "password", "user@example.com", Set.of("USER"));
        seedUser("admin", "password", "admin@example.com", Set.of("USER", "ADMIN"));

        seedPostmanClient();
        seedServiceClient();
        seedNextJsClient();
        seedOrderServiceClient();
    }

    private void seedUser(String username, String rawPassword, String email, Set<String> roles) {
        if (users.findByUsername(username).isPresent()) {
            return;
        }
        UserAccount account = new UserAccount();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(rawPassword));
        account.setEmail(email);
        account.setRoles(roles);
        users.save(account);
        log.info("Seeded user '{}' with roles {}", username, roles);
    }

    /** Authorization Code + PKCE (+ refresh token) for testing in Postman. */
    private void seedPostmanClient() {
        if (clients.findByClientId("postman-client") != null) {
            return;
        }
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("postman-client")
                .clientName("Postman")
                .clientSecret(passwordEncoder.encode("postman-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("https://oauth.pstmn.io/v1/callback")
                .redirectUri("https://oauth.pstmn.io/v1/browser-callback")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("api.read")
                .scope("api.write")
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(true)                  // PKCE required
                        .requireAuthorizationConsent(false)     // set true to see the consent page
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(30))
                        .refreshTokenTimeToLive(Duration.ofDays(7))
                        .reuseRefreshTokens(false)              // refresh-token rotation
                        .build())
                .build();
        clients.save(client);
        log.info("Seeded client 'postman-client' (authorization_code + refresh_token, PKCE)");
    }

    /** Client Credentials (machine-to-machine). */
    private void seedServiceClient() {
        if (clients.findByClientId("service-client") != null) {
            return;
        }
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("service-client")
                .clientName("Backend Service")
                .clientSecret(passwordEncoder.encode("service-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("api.read")
                .scope("api.write")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(1))
                        .build())
                .build();
        clients.save(client);
        log.info("Seeded client 'service-client' (client_credentials)");
    }

    /** Public client (no secret) for a Next.js app using Authorization Code + PKCE. */
    private void seedNextJsClient() {
        if (clients.findByClientId("nextjs-client") != null) {
            return;
        }
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("nextjs-client")
                .clientName("Next.js App")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:3000/api/auth/callback/spring")
                .postLogoutRedirectUri("http://localhost:3000")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("api.read")
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(true)
                        .requireAuthorizationConsent(false)
                        .build())
                .build();
        clients.save(client);
        log.info("Seeded client 'nextjs-client' (public, PKCE)");
    }

    /**
     * Client Credentials for order-service calling customer-service and business-service.
     * Secret is read from the environment variable ORDER_CLIENT_SECRET (default: order-secret)
     * so it can be rotated via Vault without code changes.
     */
    private void seedOrderServiceClient() {
        if (clients.findByClientId("order-service") != null) {
            return;
        }
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("order-service")
                .clientName("Order Service")
                .clientSecret(passwordEncoder.encode(orderClientSecret != null ? orderClientSecret : "order-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("customer.read")
                .scope("business.read")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(30))
                        .build())
                .build();
        clients.save(client);
        log.info("Seeded client 'order-service' (client_credentials, scopes: customer.read, business.read)");
    }
}
