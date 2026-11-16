package kh.edu.istad.iam.persistence.repository;

import kh.edu.istad.iam.persistence.entity.SystemConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaSystemConfigRepository extends JpaRepository<SystemConfigEntity, String> {
}
