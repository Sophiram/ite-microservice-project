package kh.edu.istad.iam.persistence.repository;

import java.util.Optional;
import java.util.UUID;
import kh.edu.istad.iam.persistence.entity.PermissionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPermissionRepository extends JpaRepository<PermissionEntity, Long> {
    Optional<PermissionEntity> findByUuid(UUID uuid);
    Optional<PermissionEntity> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    Page<PermissionEntity> findAll(Pageable pageable);
}
