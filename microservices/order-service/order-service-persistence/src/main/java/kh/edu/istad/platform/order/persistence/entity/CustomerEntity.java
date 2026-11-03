package kh.edu.istad.platform.order.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("customers")
public class CustomerEntity {
    @Id
    private UUID customerId;
    private String familyName;
    private String givenName;
    private String username;

}
