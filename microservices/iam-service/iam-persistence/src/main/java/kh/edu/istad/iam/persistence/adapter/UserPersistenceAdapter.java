package kh.edu.istad.iam.persistence.adapter;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import kh.edu.istad.iam.application.port.out.PageQuery;
import kh.edu.istad.iam.application.port.out.PageResult;
import kh.edu.istad.iam.application.port.out.UserRepository;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.persistence.entity.UserEntity;
import kh.edu.istad.iam.persistence.mapper.UserMapper;
import kh.edu.istad.iam.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class UserPersistenceAdapter implements UserRepository {
    private static final Set<String> SORTS = Set.of("id", "username", "email", "createdAt", "updatedAt");
    private final JpaUserRepository repository;

    @Override
    public User save(User user) {
        UserEntity entity = user.getId() == null ? new UserEntity()
                : repository.findById(user.getId()).orElseGet(UserEntity::new);
        return UserMapper.toDomain(repository.save(UserMapper.toEntity(user, entity)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return repository.findById(id).map(UserMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUuid(UUID uuid) {
        return repository.findByUuid(uuid).map(UserMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsernameOrEmail(String login) {
        if (login == null) {
            return Optional.empty();
        }
        return repository.findFirstByUsernameIgnoreCaseOrEmailIgnoreCase(login, login)
                .map(UserMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return email == null ? Optional.empty()
                : repository.findFirstByEmailIgnoreCase(email).map(UserMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return repository.existsByUsernameIgnoreCase(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return email != null && repository.existsByEmailIgnoreCase(email);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<User> findAll(PageQuery query) {
        Page<UserEntity> page = repository.findAll(PersistenceSupport.pageable(query, SORTS));
        return PersistenceSupport.page(page, UserMapper::toDomain);
    }
}
