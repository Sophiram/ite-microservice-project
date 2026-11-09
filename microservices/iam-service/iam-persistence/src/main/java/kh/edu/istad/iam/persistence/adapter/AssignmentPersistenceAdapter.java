package kh.edu.istad.iam.persistence.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.domain.entity.Effect;
import kh.edu.istad.iam.domain.entity.RolePermission;
import kh.edu.istad.iam.domain.entity.UserPermission;
import kh.edu.istad.iam.domain.entity.UserRole;
import kh.edu.istad.iam.persistence.entity.RolePermissionEntity;
import kh.edu.istad.iam.persistence.entity.UserPermissionEntity;
import kh.edu.istad.iam.persistence.entity.UserRoleEntity;
import kh.edu.istad.iam.persistence.mapper.AssignmentMapper;
import kh.edu.istad.iam.persistence.repository.JpaPermissionRepository;
import kh.edu.istad.iam.persistence.repository.JpaRolePermissionRepository;
import kh.edu.istad.iam.persistence.repository.JpaRoleRepository;
import kh.edu.istad.iam.persistence.repository.JpaUserPermissionRepository;
import kh.edu.istad.iam.persistence.repository.JpaUserRepository;
import kh.edu.istad.iam.persistence.repository.JpaUserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class AssignmentPersistenceAdapter implements AssignmentRepository {
    private final JpaUserRoleRepository userRoles;
    private final JpaRolePermissionRepository rolePermissions;
    private final JpaUserPermissionRepository userPermissions;
    private final JpaUserRepository users;
    private final JpaRoleRepository roles;
    private final JpaPermissionRepository permissions;

    @Override
    public UserRole save(UserRole assignment) {
        var user = users.findById(assignment.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Assignment user does not exist"));
        var role = roles.findById(assignment.getRole().getId())
                .orElseThrow(() -> new IllegalArgumentException("Assignment role does not exist"));
        UserRoleEntity entity = assignment.getId() == null ? new UserRoleEntity()
                : userRoles.findById(assignment.getId()).orElseGet(UserRoleEntity::new);
        return AssignmentMapper.toDomain(userRoles.save(AssignmentMapper.toEntity(assignment, entity, user, role)));
    }

    @Override
    public RolePermission save(RolePermission assignment) {
        var role = roles.findById(assignment.getRole().getId())
                .orElseThrow(() -> new IllegalArgumentException("Assignment role does not exist"));
        var permission = permissions.findById(assignment.getPermission().getId())
                .orElseThrow(() -> new IllegalArgumentException("Assignment permission does not exist"));
        RolePermissionEntity entity = assignment.getId() == null ? new RolePermissionEntity()
                : rolePermissions.findById(assignment.getId()).orElseGet(RolePermissionEntity::new);
        return AssignmentMapper.toDomain(rolePermissions.save(
                AssignmentMapper.toEntity(assignment, entity, role, permission)));
    }

    @Override
    public UserPermission save(UserPermission assignment) {
        var user = users.findById(assignment.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Assignment user does not exist"));
        var permission = permissions.findById(assignment.getPermission().getId())
                .orElseThrow(() -> new IllegalArgumentException("Assignment permission does not exist"));
        UserPermissionEntity entity = assignment.getId() == null ? new UserPermissionEntity()
                : userPermissions.findById(assignment.getId()).orElseGet(UserPermissionEntity::new);
        return AssignmentMapper.toDomain(userPermissions.save(
                AssignmentMapper.toEntity(assignment, entity, user, permission)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRole> findUserRoles(Long userId) {
        return userRoles.findAllByUser_Id(userId).stream().map(AssignmentMapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolePermission> findRolePermissions(Collection<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        return rolePermissions.findAllByRole_IdIn(roleIds).stream().map(AssignmentMapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserPermission> findUserPermissions(Long userId) {
        return userPermissions.findAllByUser_Id(userId).stream().map(AssignmentMapper::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserRole> findUserRole(Long userId, Long roleId) {
        return userRoles.findFirstByUser_IdAndRole_IdOrderByIdDesc(userId, roleId).map(AssignmentMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RolePermission> findRolePermission(Long roleId, Long permissionId) {
        return rolePermissions.findFirstByRole_IdAndPermission_IdOrderByIdDesc(roleId, permissionId)
                .map(AssignmentMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserPermission> findUserPermission(Long userId, Long permissionId, Effect effect) {
        return userPermissions.findFirstByUser_IdAndPermission_IdAndEffectOrderByIdDesc(userId, permissionId, effect)
                .map(AssignmentMapper::toDomain);
    }
}
