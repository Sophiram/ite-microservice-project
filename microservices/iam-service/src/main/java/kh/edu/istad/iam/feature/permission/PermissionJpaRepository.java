package kh.edu.istad.iam.feature.permission;

import kh.edu.istad.iam.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionJpaRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByPermission(String permission);

}
