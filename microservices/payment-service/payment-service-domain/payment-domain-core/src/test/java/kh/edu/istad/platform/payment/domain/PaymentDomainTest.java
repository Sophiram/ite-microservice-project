package kh.edu.istad.platform.payment.domain;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.event.PaymentCompletedEvent;
import kh.edu.istad.platform.payment.domain.event.PaymentFailedEvent;
import kh.edu.istad.platform.payment.domain.exception.PaymentDomainException;
import kh.edu.istad.platform.payment.domain.service.PaymentDomainService;
import kh.edu.istad.platform.payment.domain.service.PaymentDomainServiceImpl;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentDomainTest {

    private PaymentDomainService paymentDomainService;

    @BeforeEach
    void setUp() {
        paymentDomainService = new PaymentDomainServiceImpl();
    }

    @Test
    void testValidateAndInitiatePayment_Success() {
        Payment payment = Payment.builder()
                .orderId(new OrderId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.randomUUID()))
                .price(new Money(new BigDecimal("99.99")))
                .build();

        PaymentCompletedEvent event = paymentDomainService.validateAndInitiatePayment(payment, new ArrayList<>());

        assertNotNull(event);
        assertNotNull(payment.getId());
        assertEquals(PaymentStatus.COMPLETED, payment.getPaymentStatus());
        assertNotNull(event.getCreatedAt());
    }

    @Test
    void testValidateAndInitiatePayment_ZeroPrice_ThrowsException() {
        Payment payment = Payment.builder()
                .orderId(new OrderId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.randomUUID()))
                .price(Money.ZERO)
                .build();

        assertThrows(PaymentDomainException.class, () ->
                paymentDomainService.validateAndInitiatePayment(payment, new ArrayList<>())
        );
    }

    @Test
    void testValidateAndFailPayment_Success() {
        Payment payment = Payment.builder()
                .orderId(new OrderId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.randomUUID()))
                .price(new Money(new BigDecimal("50.00")))
                .build();

        List<String> failures = List.of("Insufficient customer funds");
        PaymentFailedEvent event = paymentDomainService.validateAndFailPayment(payment, failures);

        assertNotNull(event);
        assertEquals(PaymentStatus.FAILED, payment.getPaymentStatus());
        assertEquals(1, payment.getFailureMessages().size());
        assertEquals("Insufficient customer funds", payment.getFailureMessages().get(0));
    }
}
