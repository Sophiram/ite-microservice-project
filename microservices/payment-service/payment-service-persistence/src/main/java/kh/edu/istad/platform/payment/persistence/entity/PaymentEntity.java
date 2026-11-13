package kh.edu.istad.platform.payment.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("payments")
public class PaymentEntity {

    @Id
    private UUID id;
    private UUID orderId;
    private UUID customerId;
    private BigDecimal price;
    private String status;
    private Instant createdAt;
    private String failureMessages;
}
