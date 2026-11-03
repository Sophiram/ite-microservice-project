package kh.edu.istad.platform.authserver.repository;

import java.util.Optional;

import kh.edu.istad.platform.authserver.domain.Client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByClientId(String clientId);
}
