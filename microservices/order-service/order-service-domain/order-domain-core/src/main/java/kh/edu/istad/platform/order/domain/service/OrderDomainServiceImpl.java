package kh.edu.istad.platform.order.domain.service;

import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.event.OrderCreatedEvent;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class OrderDomainServiceImpl implements OrderDomainService{
    @Override
    public OrderCreatedEvent initiateOrder(Order order) {
        order.initializeOrder();
        return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of("UTC")));
    }

}
