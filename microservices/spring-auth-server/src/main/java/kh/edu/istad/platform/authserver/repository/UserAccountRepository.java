package kh.edu.istad.platform.authserver.repository;

import java.util.Optional;

import kh.edu.istad.platform.authserver.domain.UserAccount;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
}
