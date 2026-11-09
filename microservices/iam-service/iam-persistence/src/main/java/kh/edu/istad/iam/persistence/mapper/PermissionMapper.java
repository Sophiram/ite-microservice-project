package kh.edu.istad.iam.persistence.mapper;

import kh.edu.istad.iam.domain.entity.Permission;
import kh.edu.istad.iam.persistence.entity.PermissionEntity;

public final class PermissionMapper {
    private PermissionMapper() { }

    public static Permission toDomain(PermissionEntity entity) {
        Permission permission = new Permission();
        permission.setName(entity.getName());
        permission.setResource(entity.getResource());
        permission.setAction(entity.getAction());
        permission.setDescription(entity.getDescription());
        permission.setSystem(entity.isSystem());
        permission.setActive(entity.isActive());
        PersistenceMapperSupport.copyBase(entity, permission);
        permission.setUuid(entity.getUuid());
        return permission;
    }

    public static PermissionEntity toEntity(Permission domain, PermissionEntity entity) {
        entity.setUuid(domain.getUuid());
        entity.setName(domain.getName());
        entity.setResource(domain.getResource());
        entity.setAction(domain.getAction());
        entity.setDescription(domain.getDescription());
        entity.setSystem(domain.isSystem());
        entity.setActive(domain.isActive());
        PersistenceMapperSupport.copyBase(domain, entity);
        return entity;
    }
}
