package kh.edu.istad.platform.payment.domain.service;

import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.event.PaymentCancelledEvent;
import kh.edu.istad.platform.payment.domain.event.PaymentCompletedEvent;
import kh.edu.istad.platform.payment.domain.event.PaymentFailedEvent;

import java.util.List;

public interface PaymentDomainService {

    PaymentCompletedEvent validateAndInitiatePayment(Payment payment, List<String> failureMessages);

    PaymentCancelledEvent validateAndCancelPayment(Payment payment, List<String> failureMessages);

    PaymentFailedEvent validateAndFailPayment(Payment payment, List<String> failureMessages);
}
