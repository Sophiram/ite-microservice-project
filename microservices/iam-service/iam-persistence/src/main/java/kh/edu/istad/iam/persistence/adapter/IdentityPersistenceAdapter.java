package kh.edu.istad.iam.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kh.edu.istad.iam.application.port.out.IdentityRepository;
import kh.edu.istad.iam.domain.entity.Identity;
import kh.edu.istad.iam.domain.entity.Provider;
import kh.edu.istad.iam.persistence.entity.IdentityEntity;
import kh.edu.istad.iam.persistence.mapper.IdentityMapper;
import kh.edu.istad.iam.persistence.repository.JpaIdentityRepository;
import kh.edu.istad.iam.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class IdentityPersistenceAdapter implements IdentityRepository {
    private final JpaIdentityRepository repository;
    private final JpaUserRepository users;

    @Override
    public Identity save(Identity identity) {
        var user = users.findById(identity.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Identity user does not exist"));
        IdentityEntity entity = identity.getId() == null ? new IdentityEntity()
                : repository.findById(identity.getId()).orElseGet(IdentityEntity::new);
        return IdentityMapper.toDomain(repository.save(IdentityMapper.toEntity(identity, entity, user)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Identity> findByProviderAndSubject(Provider provider, String subject) {
        return repository.findByProviderAndProviderSubject(provider, subject).map(IdentityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Identity> findByUuid(UUID uuid) {
        return repository.findByUuid(uuid).map(IdentityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Identity> findByUserId(Long userId) {
        return repository.findAllByUser_Id(userId).stream().map(IdentityMapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndProvider(Long userId, Provider provider) {
        return repository.existsByUser_IdAndProvider(userId, provider);
    }
}
