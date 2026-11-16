package kh.edu.istad.iam.persistence.repository;

import kh.edu.istad.iam.persistence.entity.AccountDeactivationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAccountDeactivationRepository extends JpaRepository<AccountDeactivationEntity, Long> {
}
