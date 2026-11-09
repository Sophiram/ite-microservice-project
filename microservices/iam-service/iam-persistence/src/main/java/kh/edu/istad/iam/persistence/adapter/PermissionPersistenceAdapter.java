package kh.edu.istad.iam.persistence.adapter;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import kh.edu.istad.iam.application.port.out.PageQuery;
import kh.edu.istad.iam.application.port.out.PageResult;
import kh.edu.istad.iam.application.port.out.PermissionRepository;
import kh.edu.istad.iam.domain.entity.Permission;
import kh.edu.istad.iam.persistence.entity.PermissionEntity;
import kh.edu.istad.iam.persistence.mapper.PermissionMapper;
import kh.edu.istad.iam.persistence.repository.JpaPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class PermissionPersistenceAdapter implements PermissionRepository {
    private static final Set<String> SORTS = Set.of("id", "name", "resource", "action", "createdAt", "updatedAt");
    private final JpaPermissionRepository repository;

    @Override
    public Permission save(Permission permission) {
        PermissionEntity entity = permission.getId() == null ? new PermissionEntity()
                : repository.findById(permission.getId()).orElseGet(PermissionEntity::new);
        return PermissionMapper.toDomain(repository.save(PermissionMapper.toEntity(permission, entity)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Permission> findById(Long id) {
        return repository.findById(id).map(PermissionMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Permission> findByUuid(UUID uuid) {
        return repository.findByUuid(uuid).map(PermissionMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Permission> findByName(String name) {
        return repository.findByNameIgnoreCase(name).map(PermissionMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return repository.existsByNameIgnoreCase(name);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Permission> findAll(PageQuery query) {
        Page<PermissionEntity> page = repository.findAll(PersistenceSupport.pageable(query, SORTS));
        return PersistenceSupport.page(page, PermissionMapper::toDomain);
    }
}
