package kh.edu.istad.platform.payment.message.adapter;

import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.event.PaymentCancelledEvent;
import kh.edu.istad.platform.payment.domain.event.PaymentCompletedEvent;
import kh.edu.istad.platform.payment.domain.event.PaymentFailedEvent;
import kh.edu.istad.platform.payment.domain.port.out.message.PaymentResponseMessagePublisher;
import kh.edu.istad.platform.payment.message.event.PaymentResponseKafkaMessage;
import kh.edu.istad.platform.payment.message.producer.PaymentEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaMessagePublisherAdapter implements PaymentResponseMessagePublisher {

    private final PaymentEventProducer paymentEventProducer;

    @Override
    public Mono<Void> publishPaymentCompleted(PaymentCompletedEvent event) {
        return Mono.fromFuture(() -> {
            Payment payment = event.getPayment();
            PaymentResponseKafkaMessage message = new PaymentResponseKafkaMessage(
                    payment.getId() != null ? payment.getId().value() : null,
                    payment.getOrderId() != null ? payment.getOrderId().value() : null,
                    payment.getCustomerId() != null ? payment.getCustomerId().value() : null,
                    payment.getPrice() != null ? payment.getPrice().amount() : null,
                    payment.getPaymentStatus() != null ? payment.getPaymentStatus().name() : null,
                    event.getFailureMessages(),
                    event.getCreatedAt() != null ? event.getCreatedAt() : Instant.now()
            );

            // Publish to both payment.response and payment.completed
            return paymentEventProducer.publishPaymentResponse(message)
                    .thenCompose(r -> paymentEventProducer.publishPaymentCompleted(message));
        }).then();
    }

    @Override
    public Mono<Void> publishPaymentFailed(PaymentFailedEvent event) {
        return Mono.fromFuture(() -> {
            Payment payment = event.getPayment();
            PaymentResponseKafkaMessage message = new PaymentResponseKafkaMessage(
                    payment.getId() != null ? payment.getId().value() : null,
                    payment.getOrderId() != null ? payment.getOrderId().value() : null,
                    payment.getCustomerId() != null ? payment.getCustomerId().value() : null,
                    payment.getPrice() != null ? payment.getPrice().amount() : null,
                    payment.getPaymentStatus() != null ? payment.getPaymentStatus().name() : null,
                    event.getFailureMessages(),
                    event.getCreatedAt() != null ? event.getCreatedAt() : Instant.now()
            );

            // Publish to both payment.response and payment.failed
            return paymentEventProducer.publishPaymentResponse(message)
                    .thenCompose(r -> paymentEventProducer.publishPaymentFailed(message));
        }).then();
    }

    @Override
    public Mono<Void> publishPaymentCancelled(PaymentCancelledEvent event) {
        return Mono.fromFuture(() -> {
            Payment payment = event.getPayment();
            PaymentResponseKafkaMessage message = new PaymentResponseKafkaMessage(
                    payment.getId() != null ? payment.getId().value() : null,
                    payment.getOrderId() != null ? payment.getOrderId().value() : null,
                    payment.getCustomerId() != null ? payment.getCustomerId().value() : null,
                    payment.getPrice() != null ? payment.getPrice().amount() : null,
                    payment.getPaymentStatus() != null ? payment.getPaymentStatus().name() : null,
                    event.getFailureMessages(),
                    event.getCreatedAt() != null ? event.getCreatedAt() : Instant.now()
            );

            return paymentEventProducer.publishPaymentResponse(message);
        }).then();
    }
}
