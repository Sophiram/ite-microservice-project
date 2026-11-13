package kh.edu.istad.platform.order.domain.dto;

import kh.edu.istad.common.domain.valueobject.OrderStatus;
import kh.edu.istad.platform.order.domain.entity.Order;

import java.util.UUID;

public record InitialOrderResult(
        UUID orderId,
        UUID trackingId,
        OrderStatus orderStatus,
        String message
) {
    public static InitialOrderResult from(Order order, String message) {
        return new InitialOrderResult(
                order.getId() != null ? order.getId().value() : null,
                order.getTrackingId() != null ? order.getTrackingId().value() : null,
                order.getOrderStatus(),
                message
        );
    }
}