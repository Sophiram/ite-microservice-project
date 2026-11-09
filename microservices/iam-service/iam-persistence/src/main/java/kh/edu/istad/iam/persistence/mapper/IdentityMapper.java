package kh.edu.istad.iam.persistence.mapper;

import java.util.HashMap;
import kh.edu.istad.iam.domain.entity.Identity;
import kh.edu.istad.iam.persistence.entity.IdentityEntity;
import kh.edu.istad.iam.persistence.entity.UserEntity;

public final class IdentityMapper {
    private IdentityMapper() { }

    public static Identity toDomain(IdentityEntity entity) {
        Identity identity = new Identity();
        identity.setUserId(entity.getUser().getId());
        identity.setProvider(entity.getProvider());
        identity.setProviderSubject(entity.getProviderSubject());
        identity.setEmail(entity.getEmail());
        identity.setEmailVerified(entity.isEmailVerified());
        identity.setDisplayName(entity.getDisplayName());
        identity.setUsername(entity.getUsername());
        identity.setPictureUrl(entity.getPictureUrl());
        identity.setAttributes(entity.getAttributes());
        identity.setLinkedAt(entity.getLinkedAt());
        identity.setLastLoginAt(entity.getLastLoginAt());
        PersistenceMapperSupport.copyBase(entity, identity);
        identity.setUuid(entity.getUuid());
        return identity;
    }

    public static IdentityEntity toEntity(Identity domain, IdentityEntity entity, UserEntity user) {
        entity.setUuid(domain.getUuid());
        entity.setUser(user);
        entity.setProvider(domain.getProvider());
        entity.setProviderSubject(domain.getProviderSubject());
        entity.setEmail(domain.getEmail());
        entity.setEmailVerified(domain.isEmailVerified());
        entity.setDisplayName(domain.getDisplayName());
        entity.setUsername(domain.getUsername());
        entity.setPictureUrl(domain.getPictureUrl());
        entity.setAttributes(new HashMap<>(domain.getAttributes()));
        entity.setLinkedAt(domain.getLinkedAt());
        entity.setLastLoginAt(domain.getLastLoginAt());
        PersistenceMapperSupport.copyBase(domain, entity);
        return entity;
    }
}
