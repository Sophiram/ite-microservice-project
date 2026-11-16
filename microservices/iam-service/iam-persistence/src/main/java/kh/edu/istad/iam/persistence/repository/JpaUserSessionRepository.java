package kh.edu.istad.iam.persistence.repository;

import java.util.List;
import kh.edu.istad.iam.persistence.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaUserSessionRepository extends JpaRepository<UserSessionEntity, String> {
    List<UserSessionEntity> findByUsernameOrderByCreatedAtDesc(String username);
    List<UserSessionEntity> findAllByOrderByCreatedAtDesc();
    void deleteByUsername(String username);
}
