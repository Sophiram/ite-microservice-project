package kh.edu.istad.platform.payment.message.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderCreatedKafkaMessage(
        UUID orderId,
        UUID customerId,
        UUID businessId,
        BigDecimal totalAmount,
        Instant createdAt
) {
}
