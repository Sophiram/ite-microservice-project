package kh.edu.istad.platform.order.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("order_items")
public class OrderItemEntity {
    @Id
    @Embedded.Nullable
    private OrderItemEntityId orderItemEntityId;
    private UUID productId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subTotal;
}
