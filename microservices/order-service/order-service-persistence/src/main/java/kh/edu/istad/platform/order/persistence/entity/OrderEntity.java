package kh.edu.istad.platform.order.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("orders")
public class OrderEntity {
    @Id
    private UUID id;
    private UUID businessId;
    private UUID customerId;
    private String failureMessages;
    private String orderStatus;
    private BigDecimal price;
    private UUID trackingId;
    private UUID orderAddressId;
}
