package kh.edu.istad.iam.infrastructure.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import kh.edu.istad.iam.infrastructure.security.TelegramOAuth2AuthorizationRequestResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcClientRegistrationAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcClientRegistrationAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcClientRegistrationAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcClientRegistrationAuthenticationValidator;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {
    @Bean
    @Order(1)
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServer = new OAuth2AuthorizationServerConfigurer();
        http.securityMatcher(authorizationServer.getEndpointsMatcher())
                .with(authorizationServer, server -> server
                        .authorizationEndpoint(endpoint -> endpoint.consentPage("/oauth2/consent"))
                        .oidc(oidc -> oidc.clientRegistrationEndpoint(endpoint ->
                                endpoint.authenticationProviders(AuthorizationServerConfig::configureDcrValidators))))
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()))
                .exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint("/login"),
                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)));
        return http.build();
    }

    @Bean
    @Order(3)
    SecurityFilterChain iamWebSecurityFilterChain(HttpSecurity http,
            org.springframework.security.oauth2.client.registration.ClientRegistrationRepository clientRegistrationRepository,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oauthUserService,
            OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService,
            @Value("${TELEGRAM_ORIGIN:}") String telegramOrigin) throws Exception {
        TelegramOAuth2AuthorizationRequestResolver resolver =
                new TelegramOAuth2AuthorizationRequestResolver(clientRegistrationRepository, telegramOrigin);

        org.springframework.security.web.savedrequest.HttpSessionRequestCache requestCache =
                new org.springframework.security.web.savedrequest.HttpSessionRequestCache();
        requestCache.setRequestMatcher(request -> !request.getRequestURI().startsWith("/error"));
        http.requestCache(cache -> cache.requestCache(requestCache));

        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/logout", "/oauth2/**", "/connect/register", "/login", "/register"));

        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health/**", "/actuator/info", "/login", "/login/**", "/logout", "/register", "/account-suspended", "/assets/**", "/index.html", "/favicon.svg", "/icons.svg", "/css/**", "/favicon.ico", "/error").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureHandler((request, response, exception) -> {
                            if (exception instanceof org.springframework.security.authentication.LockedException) {
                                response.sendRedirect("/account-suspended?reason=account_locked");
                            } else if (exception instanceof org.springframework.security.authentication.DisabledException) {
                                response.sendRedirect("/account-suspended?reason=account_disabled");
                            } else {
                                response.sendRedirect("/login?error=true");
                            }
                        })
                        .permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(request -> "/logout".equals(request.getRequestURI()))
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "SESSION", "remember-me")
                        .permitAll())
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureHandler((request, response, exception) -> {
                            String reason = "account_disabled";
                            if (exception instanceof org.springframework.security.oauth2.core.OAuth2AuthenticationException oauthEx) {
                                String code = oauthEx.getError().getErrorCode();
                                if ("account_locked".equalsIgnoreCase(code) || "account_disabled".equalsIgnoreCase(code)) {
                                    reason = code;
                                }
                            }
                            response.sendRedirect("/account-suspended?reason=" + reason);
                        })
                        .authorizationEndpoint(authorization -> authorization.authorizationRequestResolver(resolver))
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oauthUserService)
                                .oidcUserService(oidcUserService)));
        return http.build();
    }

    @Bean
    PasswordEncoder iamPasswordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    RegisteredClientRepository registeredClientRepository(JdbcOperations jdbc) {
        return new JdbcRegisteredClientRepository(jdbc);
    }

    @Bean
    OAuth2AuthorizationService authorizationService(JdbcOperations jdbc,
                                                     RegisteredClientRepository clients) {
        return new JdbcOAuth2AuthorizationService(jdbc, clients);
    }

    @Bean
    OAuth2AuthorizationConsentService authorizationConsentService(JdbcOperations jdbc,
                                                                   RegisteredClientRepository clients) {
        return new JdbcOAuth2AuthorizationConsentService(jdbc, clients);
    }

    @Bean
    JWKSource<SecurityContext> jwkSource() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) pair.getPublic())
                    .privateKey((RSAPrivateKey) pair.getPrivate()).keyID(UUID.randomUUID().toString()).build();
            return new ImmutableJWKSet<>(new JWKSet(rsaKey));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to create authorization-server signing key", exception);
        }
    }

    @Bean
    JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    AuthorizationServerSettings authorizationServerSettings(
            @Value("${spring.security.oauth2.authorizationserver.issuer}") String issuer) {
        return AuthorizationServerSettings.builder()
                .issuer(issuer)
                .oidcClientRegistrationEndpoint("/connect/register")
                .build();
    }

    private static void configureDcrValidators(List<AuthenticationProvider> providers) {
        providers.stream()
                .filter(OidcClientRegistrationAuthenticationProvider.class::isInstance)
                .map(OidcClientRegistrationAuthenticationProvider.class::cast)
                .forEach(provider -> provider.setAuthenticationValidator(
                        OidcClientRegistrationAuthenticationValidator.DEFAULT_REDIRECT_URI_VALIDATOR
                                .andThen(OidcClientRegistrationAuthenticationValidator.DEFAULT_POST_LOGOUT_REDIRECT_URI_VALIDATOR)
                                .andThen(OidcClientRegistrationAuthenticationValidator.DEFAULT_JWK_SET_URI_VALIDATOR)
                                .andThen(AuthorizationServerConfig::validateDcrScopes)));
    }

    private static void validateDcrScopes(OidcClientRegistrationAuthenticationContext context) {
        OidcClientRegistrationAuthenticationToken authentication = context.getAuthentication();
        if (authentication.getClientRegistration() == null) return;
        Set<String> allowedScopes = Set.of("openid", "profile", "email", "address", "phone", "offline_access");
        boolean invalidScope = authentication.getClientRegistration().getScopes().stream()
                .anyMatch(scope -> !allowedScopes.contains(scope));
        if (invalidScope) {
            throw new OAuth2AuthenticationException(new OAuth2Error(
                    OAuth2ErrorCodes.INVALID_SCOPE, "Scope is not enabled for dynamic registration", null));
        }
    }
}
