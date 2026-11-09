package kh.edu.istad.iam.persistence.repository;

import java.util.List;
import java.util.Optional;
import kh.edu.istad.iam.persistence.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    List<UserRoleEntity> findAllByUser_Id(Long userId);
    Optional<UserRoleEntity> findFirstByUser_IdAndRole_IdOrderByIdDesc(Long userId, Long roleId);
}
