package kh.edu.istad.iam.persistence.mapper;

import kh.edu.istad.iam.domain.entity.Role;
import kh.edu.istad.iam.persistence.entity.RoleEntity;

public final class RoleMapper {
    private RoleMapper() { }

    public static Role toDomain(RoleEntity entity) {
        Role role = new Role();
        role.setName(entity.getName());
        role.setDescription(entity.getDescription());
        role.setSystem(entity.isSystem());
        role.setActive(entity.isActive());
        PersistenceMapperSupport.copyBase(entity, role);
        role.setUuid(entity.getUuid());
        return role;
    }

    public static RoleEntity toEntity(Role domain, RoleEntity entity) {
        entity.setUuid(domain.getUuid());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setSystem(domain.isSystem());
        entity.setActive(domain.isActive());
        PersistenceMapperSupport.copyBase(domain, entity);
        return entity;
    }
}
