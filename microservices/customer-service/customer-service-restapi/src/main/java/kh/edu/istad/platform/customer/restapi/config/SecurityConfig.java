package kh.edu.istad.platform.customer.restapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFlux resource-server security for customer-service.
 * All GET /api/customers/** require the SCOPE_customer.read authority (JWT scope).
 * Actuator health and springdoc/swagger endpoints are public.
 * 401 and 403 responses are returned as JSON (ProblemDetail-compatible).
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * The issuer-uri is injected from application.yaml so we can point it at the
     * auth server running in any environment without code changes.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Public: actuator health for service-discovery health checks
                        .pathMatchers("/actuator/health/**", "/actuator/info").permitAll()
                        // Public: springdoc / swagger-ui
                        .pathMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**"
                        ).permitAll()
                        // Protected customer endpoints – require scope claim
                        .pathMatchers("/api/customers/**").hasAuthority("SCOPE_customer.read")
                        // Everything else requires authentication
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {})          // issuer-uri / jwk-set-uri from application.yaml
                        // 401 – respond with JSON instead of a redirect
                        .authenticationEntryPoint((exchange, ex) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            String body = """
                                    {"status":401,"title":"Unauthorized","detail":"Full authentication is required to access this resource"}""";
                            var buffer = exchange.getResponse().bufferFactory()
                                    .wrap(body.getBytes());
                            return exchange.getResponse().writeWith(Mono.just(buffer));
                        })
                        // 403 – respond with JSON
                        .accessDeniedHandler((exchange, ex) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            String body = """
                                    {"status":403,"title":"Forbidden","detail":"Insufficient scope to access this resource"}""";
                            var buffer = exchange.getResponse().bufferFactory()
                                    .wrap(body.getBytes());
                            return exchange.getResponse().writeWith(Mono.just(buffer));
                        })
                )
                .build();
    }
}
