package kh.edu.istad.iam.infrastructure.config;

import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.iam.application.port.out.PasswordHasher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationPortAdapters {
    @Bean
    PasswordHasher passwordHasher(PasswordEncoder encoder) {
        return new PasswordHasher() {
            @Override
            public String hash(String rawPassword) {
                return encoder.encode(rawPassword);
            }

            @Override
            public boolean matches(String rawPassword, String encodedPassword) {
                return encodedPassword != null && encoder.matches(rawPassword, encodedPassword);
            }
        };
    }

    @Bean
    CurrentActorProvider currentActorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return "system";
            }
            if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal oauth) {
                Object username = oauth.getAttribute("iam_username");
                if (username != null && !username.toString().isBlank()) {
                    return username.toString();
                }
            }
            return authentication.getName();
        };
    }
}
