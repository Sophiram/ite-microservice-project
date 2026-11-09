package kh.edu.istad.iam.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kh.edu.istad.iam.domain.entity.Provider;
import kh.edu.istad.iam.persistence.entity.IdentityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaIdentityRepository extends JpaRepository<IdentityEntity, Long> {
    Optional<IdentityEntity> findByUuid(UUID uuid);
    Optional<IdentityEntity> findByProviderAndProviderSubject(Provider provider, String providerSubject);
    List<IdentityEntity> findAllByUser_Id(Long userId);
    boolean existsByUser_IdAndProvider(Long userId, Provider provider);
}
