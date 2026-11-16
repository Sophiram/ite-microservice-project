package kh.edu.istad.iam.persistence.repository;

import java.util.List;
import kh.edu.istad.iam.persistence.entity.AuditLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
    Page<AuditLogEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<AuditLogEntity> findByActionOrderByCreatedAtDesc(String action, Pageable pageable);
    List<AuditLogEntity> findByActorOrTargetOrderByCreatedAtDesc(String actor, String target);
}
