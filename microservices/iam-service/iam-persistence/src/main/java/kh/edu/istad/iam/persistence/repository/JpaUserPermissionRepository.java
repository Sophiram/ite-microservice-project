package kh.edu.istad.iam.persistence.repository;

import java.util.List;
import java.util.Optional;
import kh.edu.istad.iam.domain.entity.Effect;
import kh.edu.istad.iam.persistence.entity.UserPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserPermissionRepository extends JpaRepository<UserPermissionEntity, Long> {
    List<UserPermissionEntity> findAllByUser_Id(Long userId);
    Optional<UserPermissionEntity> findFirstByUser_IdAndPermission_IdAndEffectOrderByIdDesc(
            Long userId, Long permissionId, Effect effect);
}
