package kh.edu.istad.platform.payment.domain.service;

import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.event.PaymentCancelledEvent;
import kh.edu.istad.platform.payment.domain.event.PaymentCompletedEvent;
import kh.edu.istad.platform.payment.domain.event.PaymentFailedEvent;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

@Slf4j
public class PaymentDomainServiceImpl implements PaymentDomainService {

    @Override
    public PaymentCompletedEvent validateAndInitiatePayment(Payment payment, List<String> failureMessages) {
        payment.validatePayment();
        payment.initializePayment();
        log.info("Payment with id: {} is initiated", payment.getId().value());

        if (failureMessages != null && !failureMessages.isEmpty()) {
            payment.failPayment(failureMessages);
            log.warn("Payment with id: {} failed during initiation: {}", payment.getId().value(), failureMessages);
            return null; // or callers can use validateAndFailPayment
        }

        payment.completePayment();
        log.info("Payment with id: {} is completed successfully", payment.getId().value());
        return new PaymentCompletedEvent(payment, Instant.now());
    }

    @Override
    public PaymentCancelledEvent validateAndCancelPayment(Payment payment, List<String> failureMessages) {
        payment.cancelPayment(failureMessages);
        log.info("Payment with id: {} is cancelled", payment.getId().value());
        return new PaymentCancelledEvent(payment, Instant.now(), failureMessages);
    }

    @Override
    public PaymentFailedEvent validateAndFailPayment(Payment payment, List<String> failureMessages) {
        if (payment.getId() == null) {
            payment.initializePayment();
        }
        payment.failPayment(failureMessages);
        log.warn("Payment with id: {} is marked as failed", payment.getId().value());
        return new PaymentFailedEvent(payment, Instant.now(), failureMessages);
    }
}
