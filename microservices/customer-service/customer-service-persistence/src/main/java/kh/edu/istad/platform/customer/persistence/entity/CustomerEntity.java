package kh.edu.istad.platform.customer.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "customers")
public class CustomerEntity {
    @Id
    private UUID id;
    @Column(unique = true, nullable = false)
    private String username;
    private String familyName;
    private String givenName;
    private String email;
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private CustomerStatus status;
}
