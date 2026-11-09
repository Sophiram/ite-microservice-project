package kh.edu.istad.iam.application.port.out;

import java.util.Optional;
import java.util.UUID;
import kh.edu.istad.iam.domain.entity.User;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUuid(UUID uuid);
    Optional<User> findByUsernameOrEmail(String login);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    PageResult<User> findAll(PageQuery query);
}
