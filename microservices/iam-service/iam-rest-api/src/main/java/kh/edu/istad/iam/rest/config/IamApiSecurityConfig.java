package kh.edu.istad.iam.rest.config;

import kh.edu.istad.iam.rest.convert.IamJwtAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class IamApiSecurityConfig {

    @Bean
    @Order(2)
    SecurityFilterChain iamApiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .securityContext(context -> context.securityContextRepository(
                        new org.springframework.security.web.context.DelegatingSecurityContextRepository(
                                new org.springframework.security.web.context.RequestAttributeSecurityContextRepository(),
                                new org.springframework.security.web.context.HttpSessionSecurityContextRepository()
                        )))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(jwt ->
                        jwt.jwtAuthenticationConverter(new IamJwtAuthenticationConverter())));
        return http.build();
    }
}
