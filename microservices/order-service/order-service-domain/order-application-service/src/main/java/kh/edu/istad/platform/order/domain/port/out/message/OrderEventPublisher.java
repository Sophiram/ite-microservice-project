package kh.edu.istad.platform.order.domain.port.out.message;

import kh.edu.istad.platform.order.domain.event.OrderCreatedEvent;
import reactor.core.publisher.Mono;

public interface OrderEventPublisher {
    Mono<Void> publishOrderCreated(OrderCreatedEvent event);
}
