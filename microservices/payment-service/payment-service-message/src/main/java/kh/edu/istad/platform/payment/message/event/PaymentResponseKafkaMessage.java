package kh.edu.istad.platform.payment.message.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PaymentResponseKafkaMessage(
        UUID paymentId,
        UUID orderId,
        UUID customerId,
        BigDecimal price,
        String paymentStatus,
        List<String> failureMessages,
        Instant createdAt
) {
}
