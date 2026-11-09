package kh.edu.istad.iam.persistence.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kh.edu.istad.iam.persistence.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRolePermissionRepository extends JpaRepository<RolePermissionEntity, Long> {
    List<RolePermissionEntity> findAllByRole_IdIn(Collection<Long> roleIds);
    Optional<RolePermissionEntity> findFirstByRole_IdAndPermission_IdOrderByIdDesc(Long roleId, Long permissionId);
}
