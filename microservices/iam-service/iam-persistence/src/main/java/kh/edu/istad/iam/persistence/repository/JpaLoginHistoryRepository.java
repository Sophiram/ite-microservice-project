package kh.edu.istad.iam.persistence.repository;

import java.util.List;
import kh.edu.istad.iam.persistence.entity.LoginHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLoginHistoryRepository extends JpaRepository<LoginHistoryEntity, Long> {
    List<LoginHistoryEntity> findByUsernameOrderByCreatedAtDesc(String username);
    Page<LoginHistoryEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<LoginHistoryEntity> findBySuspiciousTrueOrderByCreatedAtDesc(Pageable pageable);
    Page<LoginHistoryEntity> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
}
