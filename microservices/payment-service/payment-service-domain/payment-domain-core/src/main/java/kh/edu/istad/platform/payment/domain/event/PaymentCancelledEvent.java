package kh.edu.istad.platform.payment.domain.event;

import kh.edu.istad.platform.payment.domain.entity.Payment;

import java.time.Instant;
import java.util.List;

public class PaymentCancelledEvent extends PaymentEvent {
    public PaymentCancelledEvent(Payment payment, Instant createdAt, List<String> failureMessages) {
        super(payment, createdAt, failureMessages);
    }
}
