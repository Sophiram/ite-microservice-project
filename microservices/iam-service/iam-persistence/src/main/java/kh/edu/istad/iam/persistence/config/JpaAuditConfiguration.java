package kh.edu.istad.iam.persistence.config;

import java.util.Optional;
import kh.edu.istad.iam.application.port.out.CurrentActorProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditConfiguration {
    @Bean
    AuditorAware<String> auditorAware(ObjectProvider<CurrentActorProvider> actorProviders) {
        return () -> Optional.ofNullable(actorProviders.getIfAvailable(() -> () -> "system").currentActor());
    }
}
