package kh.edu.istad.platform.customer.persistence.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Configuration
public class AuditorAwareConfig implements ReactiveAuditorAware<String> {
    @Override
    public Mono<String> getCurrentAuditor() {
        return Mono.just("SYSTEM");
    }
}
