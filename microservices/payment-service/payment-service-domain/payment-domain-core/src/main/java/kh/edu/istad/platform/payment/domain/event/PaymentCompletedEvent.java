package kh.edu.istad.platform.payment.domain.event;

import kh.edu.istad.platform.payment.domain.entity.Payment;

import java.time.Instant;
import java.util.Collections;

public class PaymentCompletedEvent extends PaymentEvent {
    public PaymentCompletedEvent(Payment payment, Instant createdAt) {
        super(payment, createdAt, Collections.emptyList());
    }
}
