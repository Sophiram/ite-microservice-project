package kh.edu.istad.platform.order.domain.service;

import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.event.OrderCreatedEvent;

public interface OrderDomainService {
    OrderCreatedEvent initiateOrder(Order order);

}
