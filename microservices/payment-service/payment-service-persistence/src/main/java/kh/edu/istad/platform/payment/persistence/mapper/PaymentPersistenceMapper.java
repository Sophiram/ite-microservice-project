package kh.edu.istad.platform.payment.persistence.mapper;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentId;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentStatus;
import kh.edu.istad.platform.payment.persistence.entity.PaymentEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class PaymentPersistenceMapper {

    private static final String FAILURE_MESSAGE_DELIMITER = "###";

    private PaymentPersistenceMapper() {
    }

    public static PaymentEntity toEntity(Payment payment) {
        if (payment == null) {
            return null;
        }

        String failureMessagesStr = null;
        if (payment.getFailureMessages() != null && !payment.getFailureMessages().isEmpty()) {
            failureMessagesStr = String.join(FAILURE_MESSAGE_DELIMITER, payment.getFailureMessages());
        }

        return PaymentEntity.builder()
                .id(payment.getId() != null ? payment.getId().value() : null)
                .orderId(payment.getOrderId() != null ? payment.getOrderId().value() : null)
                .customerId(payment.getCustomerId() != null ? payment.getCustomerId().value() : null)
                .price(payment.getPrice() != null ? payment.getPrice().amount() : null)
                .status(payment.getPaymentStatus() != null ? payment.getPaymentStatus().name() : null)
                .createdAt(payment.getCreatedAt())
                .failureMessages(failureMessagesStr)
                .build();
    }

    public static Payment toDomain(PaymentEntity entity) {
        if (entity == null) {
            return null;
        }

        List<String> failureMessages = Collections.emptyList();
        if (entity.getFailureMessages() != null && !entity.getFailureMessages().isBlank()) {
            failureMessages = Arrays.asList(entity.getFailureMessages().split(FAILURE_MESSAGE_DELIMITER));
        }

        PaymentStatus status = null;
        if (entity.getStatus() != null) {
            try {
                status = PaymentStatus.valueOf(entity.getStatus());
            } catch (IllegalArgumentException ignored) {
            }
        }

        return Payment.builder()
                .paymentId(entity.getId() != null ? new PaymentId(entity.getId()) : null)
                .orderId(entity.getOrderId() != null ? new OrderId(entity.getOrderId()) : null)
                .customerId(entity.getCustomerId() != null ? new CustomerId(entity.getCustomerId()) : null)
                .price(entity.getPrice() != null ? new Money(entity.getPrice()) : null)
                .paymentStatus(status)
                .createdAt(entity.getCreatedAt())
                .failureMessages(failureMessages)
                .build();
    }
}
