package kh.edu.istad.iam.application.port.out;

import java.util.Optional;
import java.util.UUID;
import kh.edu.istad.iam.domain.entity.Permission;

public interface PermissionRepository {
    Permission save(Permission permission);
    Optional<Permission> findById(Long id);
    Optional<Permission> findByUuid(UUID uuid);
    Optional<Permission> findByName(String name);
    boolean existsByName(String name);
    PageResult<Permission> findAll(PageQuery query);
}
