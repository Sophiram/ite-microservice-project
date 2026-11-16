package kh.edu.istad.platform.order.message.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID customerId,
        UUID businessId,
        BigDecimal totalAmount,
        Instant createdAt
) {
    public OrderCreatedEvent(UUID orderId, UUID customerId, BigDecimal totalAmount, Instant createdAt) {
        this(orderId, customerId, null, totalAmount, createdAt);
    }
}
