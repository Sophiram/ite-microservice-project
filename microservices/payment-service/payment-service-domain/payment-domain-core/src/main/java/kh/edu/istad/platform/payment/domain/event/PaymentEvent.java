package kh.edu.istad.platform.payment.domain.event;

import kh.edu.istad.common.domain.event.DomainEvent;
import kh.edu.istad.platform.payment.domain.entity.Payment;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public abstract class PaymentEvent implements DomainEvent<Payment> {
    private final Payment payment;
    private final Instant createdAt;
    private final List<String> failureMessages;

    public PaymentEvent(Payment payment, Instant createdAt, List<String> failureMessages) {
        this.payment = payment;
        this.createdAt = createdAt;
        this.failureMessages = failureMessages != null ? failureMessages : Collections.emptyList();
    }

    public Payment getPayment() {
        return payment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }
}
