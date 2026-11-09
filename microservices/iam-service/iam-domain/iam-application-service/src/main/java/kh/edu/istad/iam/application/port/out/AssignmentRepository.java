package kh.edu.istad.iam.application.port.out;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kh.edu.istad.iam.domain.entity.Effect;
import kh.edu.istad.iam.domain.entity.RolePermission;
import kh.edu.istad.iam.domain.entity.UserPermission;
import kh.edu.istad.iam.domain.entity.UserRole;

public interface AssignmentRepository {
    UserRole save(UserRole assignment);
    RolePermission save(RolePermission assignment);
    UserPermission save(UserPermission assignment);
    List<UserRole> findUserRoles(Long userId);
    List<RolePermission> findRolePermissions(Collection<Long> roleIds);
    List<UserPermission> findUserPermissions(Long userId);
    Optional<UserRole> findUserRole(Long userId, Long roleId);
    Optional<RolePermission> findRolePermission(Long roleId, Long permissionId);
    Optional<UserPermission> findUserPermission(Long userId, Long permissionId, Effect effect);
}
