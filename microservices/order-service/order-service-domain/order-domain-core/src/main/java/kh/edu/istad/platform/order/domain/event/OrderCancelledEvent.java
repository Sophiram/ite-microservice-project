package kh.edu.istad.platform.order.domain.event;

import kh.edu.istad.common.domain.event.DomainEvent;
import kh.edu.istad.platform.order.domain.entity.Order;

import java.time.ZonedDateTime;

public record OrderCancelledEvent(
        Order order,
        ZonedDateTime createdAt
)
        implements DomainEvent<Order> {

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public Order getOrder() {
        return order;
    }
}
