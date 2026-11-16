package kh.edu.istad.platform.order.message.adapter;

import kh.edu.istad.platform.order.domain.event.OrderCreatedEvent;
import kh.edu.istad.platform.order.domain.port.out.message.OrderEventPublisher;
import kh.edu.istad.platform.order.message.producer.OrderEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaMessagePublisherAdapter implements OrderEventPublisher {

    private final OrderEventProducer orderEventProducer;

    @Override
    public Mono<Void> publishOrderCreated(OrderCreatedEvent domainEvent) {
        return Mono.fromFuture(() -> {
            var order = domainEvent.getOrder();
            var messageEvent = new kh.edu.istad.platform.order.message.event.OrderCreatedEvent(
                    order.getId() != null ? order.getId().value() : null,
                    order.getCustomerId() != null ? order.getCustomerId().value() : null,
                    order.getBusinessId() != null ? order.getBusinessId().value() : null,
                    order.getPrice() != null ? order.getPrice().amount() : null,
                    domainEvent.getCreatedAt() != null
                            ? domainEvent.getCreatedAt().toInstant()
                            : Instant.now()
            );
            return orderEventProducer.publish(messageEvent);
        }).then();
    }
}
