package kh.edu.istad.platform.payment.message.producer;

import kh.edu.istad.platform.payment.message.config.KafkaPaymentConfig;
import kh.edu.istad.platform.payment.message.event.PaymentResponseKafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CompletableFuture<SendResult<String, Object>> publishToTopic(String topic, PaymentResponseKafkaMessage message) {
        String key = message.orderId() != null ? message.orderId().toString() : "";
        log.info("Publishing payment event to Kafka topic={}, key={}, orderId={}, status={}",
                topic, key, message.orderId(), message.paymentStatus());

        return kafkaTemplate.send(topic, key, message)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Failed to publish payment event to topic={} for orderId={}: {}",
                                topic, message.orderId(), exception.getMessage(), exception);
                    } else {
                        log.info("Payment event successfully published to topic={}: partition={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }

    public CompletableFuture<SendResult<String, Object>> publishPaymentResponse(PaymentResponseKafkaMessage message) {
        return publishToTopic(KafkaPaymentConfig.TOPIC_PAYMENT_RESPONSE, message);
    }

    public CompletableFuture<SendResult<String, Object>> publishPaymentCompleted(PaymentResponseKafkaMessage message) {
        return publishToTopic(KafkaPaymentConfig.TOPIC_PAYMENT_COMPLETED, message);
    }

    public CompletableFuture<SendResult<String, Object>> publishPaymentFailed(PaymentResponseKafkaMessage message) {
        return publishToTopic(KafkaPaymentConfig.TOPIC_PAYMENT_FAILED, message);
    }
}
