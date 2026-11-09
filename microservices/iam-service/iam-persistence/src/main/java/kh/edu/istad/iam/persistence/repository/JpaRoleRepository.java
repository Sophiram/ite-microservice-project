package kh.edu.istad.iam.persistence.repository;

import java.util.Optional;
import java.util.UUID;
import kh.edu.istad.iam.persistence.entity.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByUuid(UUID uuid);
    Optional<RoleEntity> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    Page<RoleEntity> findAll(Pageable pageable);
}
