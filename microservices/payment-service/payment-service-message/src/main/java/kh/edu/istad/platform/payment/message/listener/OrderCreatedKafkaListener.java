package kh.edu.istad.platform.payment.message.listener;

import kh.edu.istad.platform.payment.domain.dto.ProcessPaymentCommand;
import kh.edu.istad.platform.payment.domain.usecase.ProcessPaymentUseCase;
import kh.edu.istad.platform.payment.message.config.KafkaPaymentConfig;
import kh.edu.istad.platform.payment.message.event.OrderCreatedKafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedKafkaListener {

    private final ProcessPaymentUseCase processPaymentUseCase;

    @KafkaListener(
            topics = KafkaPaymentConfig.TOPIC_ORDER_CREATED,
            groupId = KafkaPaymentConfig.PAYMENT_CONSUMER_GROUP,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderCreated(OrderCreatedKafkaMessage message) {
        if (message == null) {
            log.warn("Received empty or null message from topic {}", KafkaPaymentConfig.TOPIC_ORDER_CREATED);
            return;
        }

        log.info("Received OrderCreatedKafkaMessage: orderId={}, customerId={}, amount={}",
                message.orderId(), message.customerId(), message.totalAmount());

        ProcessPaymentCommand command = new ProcessPaymentCommand(
                message.orderId(),
                message.customerId(),
                message.totalAmount()
        );

        processPaymentUseCase.execute(command)
                .doOnSuccess(response -> log.info("Successfully processed payment for orderId: {}, status: {}",
                        response.orderId(), response.paymentStatus()))
                .doOnError(error -> log.error("Error processing payment for orderId={}: {}",
                        message.orderId(), error.getMessage(), error))
                .subscribe();
    }
}
