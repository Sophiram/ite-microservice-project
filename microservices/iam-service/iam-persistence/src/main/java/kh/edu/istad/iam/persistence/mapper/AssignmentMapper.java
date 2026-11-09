package kh.edu.istad.iam.persistence.mapper;

import kh.edu.istad.iam.domain.entity.RolePermission;
import kh.edu.istad.iam.domain.entity.UserPermission;
import kh.edu.istad.iam.domain.entity.UserRole;
import kh.edu.istad.iam.persistence.entity.PermissionEntity;
import kh.edu.istad.iam.persistence.entity.RoleEntity;
import kh.edu.istad.iam.persistence.entity.RolePermissionEntity;
import kh.edu.istad.iam.persistence.entity.UserEntity;
import kh.edu.istad.iam.persistence.entity.UserPermissionEntity;
import kh.edu.istad.iam.persistence.entity.UserRoleEntity;

public final class AssignmentMapper {
    private AssignmentMapper() { }

    public static UserRole toDomain(UserRoleEntity entity) {
        UserRole domain = new UserRole();
        domain.setUserId(entity.getUser().getId());
        domain.setRole(RoleMapper.toDomain(entity.getRole()));
        PersistenceMapperSupport.copyAssignment(entity, domain);
        return domain;
    }

    public static UserRoleEntity toEntity(UserRole domain, UserRoleEntity entity,
                                          UserEntity user, RoleEntity role) {
        entity.setUser(user);
        entity.setRole(role);
        PersistenceMapperSupport.copyAssignment(domain, entity);
        return entity;
    }

    public static RolePermission toDomain(RolePermissionEntity entity) {
        RolePermission domain = new RolePermission();
        domain.setRole(RoleMapper.toDomain(entity.getRole()));
        domain.setPermission(PermissionMapper.toDomain(entity.getPermission()));
        PersistenceMapperSupport.copyAssignment(entity, domain);
        return domain;
    }

    public static RolePermissionEntity toEntity(RolePermission domain, RolePermissionEntity entity,
                                                RoleEntity role, PermissionEntity permission) {
        entity.setRole(role);
        entity.setPermission(permission);
        PersistenceMapperSupport.copyAssignment(domain, entity);
        return entity;
    }

    public static UserPermission toDomain(UserPermissionEntity entity) {
        UserPermission domain = new UserPermission();
        domain.setUserId(entity.getUser().getId());
        domain.setPermission(PermissionMapper.toDomain(entity.getPermission()));
        domain.setEffect(entity.getEffect());
        PersistenceMapperSupport.copyAssignment(entity, domain);
        return domain;
    }

    public static UserPermissionEntity toEntity(UserPermission domain, UserPermissionEntity entity,
                                                UserEntity user, PermissionEntity permission) {
        entity.setUser(user);
        entity.setPermission(permission);
        entity.setEffect(domain.getEffect());
        PersistenceMapperSupport.copyAssignment(domain, entity);
        return entity;
    }
}
