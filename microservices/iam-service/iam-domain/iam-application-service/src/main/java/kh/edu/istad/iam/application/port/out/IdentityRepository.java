package kh.edu.istad.iam.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kh.edu.istad.iam.domain.entity.Identity;
import kh.edu.istad.iam.domain.entity.Provider;

public interface IdentityRepository {
    Identity save(Identity identity);
    Optional<Identity> findByProviderAndSubject(Provider provider, String subject);
    Optional<Identity> findByUuid(UUID uuid);
    List<Identity> findByUserId(Long userId);
    boolean existsByUserIdAndProvider(Long userId, Provider provider);
}
