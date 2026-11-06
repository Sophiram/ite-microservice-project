package kh.edu.istad.iam.feature.role;

import kh.edu.istad.iam.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleJpaRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(String role);
}
