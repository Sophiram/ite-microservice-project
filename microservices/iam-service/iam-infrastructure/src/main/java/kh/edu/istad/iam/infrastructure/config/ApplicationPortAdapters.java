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
            return authentication == null || !authentication.isAuthenticated()
                    ? "system" : authentication.getName();
        };
    }
}
