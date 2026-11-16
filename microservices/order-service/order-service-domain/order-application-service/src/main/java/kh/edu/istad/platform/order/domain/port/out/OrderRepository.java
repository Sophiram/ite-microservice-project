package kh.edu.istad.platform.order.domain.port.out;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.order.domain.entity.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository {
    Mono<Order> save(Order order);
    Flux<Order> findAll();

    Mono<Order> findById(OrderId orderId);

    Mono<Void> deleteById(OrderId orderId);

    Flux<Order> query();

}

