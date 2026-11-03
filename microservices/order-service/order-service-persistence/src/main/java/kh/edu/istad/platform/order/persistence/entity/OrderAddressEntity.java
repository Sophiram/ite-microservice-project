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
@Table("order_addresses")
public class OrderAddressEntity {
    @Id
    private UUID id;
    private String street;
    private String postalCode;
    private String city;
}
