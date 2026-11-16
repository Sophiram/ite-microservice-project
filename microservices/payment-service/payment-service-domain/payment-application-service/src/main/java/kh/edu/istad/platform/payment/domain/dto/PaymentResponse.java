package kh.edu.istad.platform.payment.domain.dto;

import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PaymentResponse(
        UUID paymentId,
        UUID orderId,
        UUID customerId,
        BigDecimal price,
        PaymentStatus paymentStatus,
        Instant createdAt,
        List<String> failureMessages,
        String message
) {
    public static PaymentResponse from(Payment payment, String message) {
        return new PaymentResponse(
                payment.getId() != null ? payment.getId().value() : null,
                payment.getOrderId() != null ? payment.getOrderId().value() : null,
                payment.getCustomerId() != null ? payment.getCustomerId().value() : null,
                payment.getPrice() != null ? payment.getPrice().amount() : null,
                payment.getPaymentStatus(),
                payment.getCreatedAt(),
                payment.getFailureMessages(),
                message
        );
    }
}
