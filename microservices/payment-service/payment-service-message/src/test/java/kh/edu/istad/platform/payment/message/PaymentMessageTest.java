package kh.edu.istad.platform.payment.message;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.payment.domain.dto.PaymentResponse;
import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentCommand;
import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.event.PaymentCompletedEvent;
import kh.edu.istad.platform.payment.domain.usecase.ProcessPaymentUseCase;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentId;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentStatus;
import kh.edu.istad.platform.payment.message.adapter.PaymentKafkaMessagePublisherAdapter;
import kh.edu.istad.platform.payment.message.event.OrderCreatedKafkaMessage;
import kh.edu.istad.platform.payment.message.listener.OrderCreatedKafkaListener;
import kh.edu.istad.platform.payment.message.producer.PaymentEventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.SendResult;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMessageTest {

    @Mock
    private PaymentEventProducer paymentEventProducer;

    @Mock
    private ProcessPaymentUseCase processPaymentUseCase;

    @InjectMocks
    private PaymentKafkaMessagePublisherAdapter publisherAdapter;

    @InjectMocks
    private OrderCreatedKafkaListener kafkaListener;

    @Test
    void testPublishPaymentCompleted() {
        Payment payment = Payment.builder()
                .paymentId(new PaymentId(UUID.randomUUID()))
                .orderId(new OrderId(UUID.randomUUID()))
                .customerId(new CustomerId(UUID.randomUUID()))
                .price(new Money(new BigDecimal("100.00")))
                .paymentStatus(PaymentStatus.COMPLETED)
                .createdAt(Instant.now())
                .build();

        PaymentCompletedEvent event = new PaymentCompletedEvent(payment, Instant.now());

        SendResult<String, Object> sendResult = mock(SendResult.class);
        when(paymentEventProducer.publishPaymentResponse(any())).thenReturn(CompletableFuture.completedFuture(sendResult));
        when(paymentEventProducer.publishPaymentCompleted(any())).thenReturn(CompletableFuture.completedFuture(sendResult));

        StepVerifier.create(publisherAdapter.publishPaymentCompleted(event))
                .verifyComplete();

        verify(paymentEventProducer, times(1)).publishPaymentResponse(any());
        verify(paymentEventProducer, times(1)).publishPaymentCompleted(any());
    }

    @Test
    void testHandleOrderCreated() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderCreatedKafkaMessage message = new OrderCreatedKafkaMessage(
                orderId,
                customerId,
                UUID.randomUUID(),
                new BigDecimal("75.50"),
                Instant.now()
        );

        PaymentResponse mockResponse = new PaymentResponse(
                UUID.randomUUID(),
                orderId,
                customerId,
                new BigDecimal("75.50"),
                PaymentStatus.COMPLETED,
                Instant.now(),
                Collections.emptyList(),
                "Success"
        );

        when(processPaymentUseCase.execute(any(ProcessPaymentCommand.class))).thenReturn(Mono.just(mockResponse));

        kafkaListener.handleOrderCreated(message);

        verify(processPaymentUseCase, times(1)).execute(any(ProcessPaymentCommand.class));
    }
}
