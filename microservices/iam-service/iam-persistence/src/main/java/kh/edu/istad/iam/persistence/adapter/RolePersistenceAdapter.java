package kh.edu.istad.iam.persistence.adapter;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import kh.edu.istad.iam.application.port.out.PageQuery;
import kh.edu.istad.iam.application.port.out.PageResult;
import kh.edu.istad.iam.application.port.out.RoleRepository;
import kh.edu.istad.iam.domain.entity.Role;
import kh.edu.istad.iam.persistence.entity.RoleEntity;
import kh.edu.istad.iam.persistence.mapper.RoleMapper;
import kh.edu.istad.iam.persistence.repository.JpaRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class RolePersistenceAdapter implements RoleRepository {
    private static final Set<String> SORTS = Set.of("id", "name", "createdAt", "updatedAt");
    private final JpaRoleRepository repository;

    @Override
    public Role save(Role role) {
        RoleEntity entity = role.getId() == null ? new RoleEntity()
                : repository.findById(role.getId()).orElseGet(RoleEntity::new);
        return RoleMapper.toDomain(repository.save(RoleMapper.toEntity(role, entity)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findById(Long id) {
        return repository.findById(id).map(RoleMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findByUuid(UUID uuid) {
        return repository.findByUuid(uuid).map(RoleMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findByName(String name) {
        return repository.findByNameIgnoreCase(name).map(RoleMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return repository.existsByNameIgnoreCase(name);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Role> findAll(PageQuery query) {
        Page<RoleEntity> page = repository.findAll(PersistenceSupport.pageable(query, SORTS));
        return PersistenceSupport.page(page, RoleMapper::toDomain);
    }
}
