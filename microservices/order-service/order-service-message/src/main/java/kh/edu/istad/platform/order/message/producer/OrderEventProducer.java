package kh.edu.istad.platform.order.message.producer;

import kh.edu.istad.platform.order.message.config.KafkaProducerConfig;
import kh.edu.istad.platform.order.message.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CompletableFuture<SendResult<String, Object>> publish(OrderCreatedEvent event) {
        log.info("Publishing OrderCreatedEvent to Kafka: orderId={}, topic={}",
                event.orderId(), KafkaProducerConfig.TOPIC_ORDER_CREATED);

        return kafkaTemplate.send(
                KafkaProducerConfig.TOPIC_ORDER_CREATED,
                event.orderId().toString(),
                event
        ).whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("Failed to publish OrderCreatedEvent to Kafka for orderId={}: {}",
                        event.orderId(), exception.getMessage(), exception);
            } else {
                log.info("Order event successfully published to Kafka: topic={}, partition={}, offset={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
