package kh.edu.istad.iam.application.port.out;

import java.util.Optional;
import java.util.UUID;
import kh.edu.istad.iam.domain.entity.Role;

public interface RoleRepository {
    Role save(Role role);
    Optional<Role> findById(Long id);
    Optional<Role> findByUuid(UUID uuid);
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
    PageResult<Role> findAll(PageQuery query);
}
