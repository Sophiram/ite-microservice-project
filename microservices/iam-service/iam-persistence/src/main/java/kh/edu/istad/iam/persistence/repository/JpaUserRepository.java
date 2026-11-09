package kh.edu.istad.iam.persistence.repository;

import java.util.Optional;
import java.util.UUID;
import kh.edu.istad.iam.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUuid(UUID uuid);
    Optional<UserEntity> findFirstByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);
    Optional<UserEntity> findFirstByEmailIgnoreCase(String email);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);
    Page<UserEntity> findAll(Pageable pageable);
}
