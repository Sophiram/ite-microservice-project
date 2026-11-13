package kh.edu.istad.platform.order.persistence.mapper;

import kh.edu.istad.common.domain.valueobject.*;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.entity.OrderItem;
import kh.edu.istad.platform.order.domain.entity.Product;
import kh.edu.istad.platform.order.domain.valueobject.OrderItemId;
import kh.edu.istad.platform.order.domain.valueobject.StreetAddress;
import kh.edu.istad.platform.order.domain.valueobject.TrackingId;
import kh.edu.istad.platform.order.persistence.entity.OrderAddressEntity;
import kh.edu.istad.platform.order.persistence.entity.OrderEntity;
import kh.edu.istad.platform.order.persistence.entity.OrderItemEntity;
import kh.edu.istad.platform.order.persistence.entity.OrderItemEntityId;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class OrderPersistenceMapper {

    public static OrderAddressEntity toAddressEntity(StreetAddress address) {
        if (address == null) {
            return null;
        }
        return OrderAddressEntity.builder()
                .id(address.id())
                .street(address.street())
                .postalCode(address.postalCode())
                .city(address.city())
                .build();
    }

    public static OrderEntity toOrderEntity(Order order) {
        if (order == null) {
            return null;
        }
        String failureMessages = (order.getFailureMessages() != null && !order.getFailureMessages().isEmpty())
                ? String.join(";", order.getFailureMessages())
                : null;

        return OrderEntity.builder()
                .id(order.getId() != null ? order.getId().value() : null)
                .businessId(order.getBusinessId() != null ? order.getBusinessId().value() : null)
                .customerId(order.getCustomerId() != null ? order.getCustomerId().value() : null)
                .failureMessages(failureMessages)
                .orderStatus(order.getOrderStatus() != null ? order.getOrderStatus().name() : null)
                .price(order.getPrice() != null ? order.getPrice().amount() : null)
                .trackingId(order.getTrackingId() != null ? order.getTrackingId().value() : null)
                .orderAddressId(order.getDeliveryAddress() != null ? order.getDeliveryAddress().id() : null)
                .build();
    }

    public static List<OrderItemEntity> toOrderItemEntities(Order order) {
        if (order == null || order.getItems() == null) {
            return List.of();
        }
        UUID orderId = order.getId() != null ? order.getId().value() : null;
        return order.getItems().stream().map(item -> OrderItemEntity.builder()
                .orderItemEntityId(new OrderItemEntityId(item.getId() != null ? item.getId().value() : null, orderId))
                .productId(item.getProduct() != null && item.getProduct().getId() != null ? item.getProduct().getId().value() : null)
                .quantity(item.getQuantity())
                .price(item.getPrice() != null ? item.getPrice().amount() : null)
                .subTotal(item.getSubTotal() != null ? item.getSubTotal().amount() : null)
                .build()
        ).toList();
    }

    public static Order toDomain(OrderEntity orderEntity, OrderAddressEntity addressEntity, List<OrderItemEntity> itemEntities) {
        if (orderEntity == null) {
            return null;
        }

        OrderId orderId = orderEntity.getId() != null ? new OrderId(orderEntity.getId()) : null;
        CustomerId customerId = orderEntity.getCustomerId() != null ? new CustomerId(orderEntity.getCustomerId()) : null;
        BusinessId businessId = orderEntity.getBusinessId() != null ? new BusinessId(orderEntity.getBusinessId()) : null;
        TrackingId trackingId = orderEntity.getTrackingId() != null ? new TrackingId(orderEntity.getTrackingId()) : null;
        OrderStatus orderStatus = orderEntity.getOrderStatus() != null ? OrderStatus.valueOf(orderEntity.getOrderStatus()) : null;
        Money price = orderEntity.getPrice() != null ? new Money(orderEntity.getPrice().setScale(2, RoundingMode.HALF_EVEN)) : null;

        StreetAddress deliveryAddress = null;
        if (addressEntity != null && addressEntity.getId() != null) {
            deliveryAddress = new StreetAddress(
                    addressEntity.getId(),
                    addressEntity.getStreet(),
                    addressEntity.getPostalCode(),
                    addressEntity.getCity()
            );
        }

        List<OrderItem> items = itemEntities != null ? itemEntities.stream().map(itemEntity -> {
            Product product = Product.Builder.builder()
                    .id(itemEntity.getProductId() != null ? new ProductId(itemEntity.getProductId()) : null)
                    .price(itemEntity.getPrice() != null ? new Money(itemEntity.getPrice().setScale(2, RoundingMode.HALF_EVEN)) : null)
                    .build();

            OrderItemId itemId = itemEntity.getOrderItemEntityId() != null && itemEntity.getOrderItemEntityId().id() != null
                    ? new OrderItemId(itemEntity.getOrderItemEntityId().id())
                    : null;

            return OrderItem.Builder.builder()
                    .id(itemId)
                    .orderId(orderId)
                    .product(product)
                    .quantity(itemEntity.getQuantity())
                    .price(itemEntity.getPrice() != null ? new Money(itemEntity.getPrice().setScale(2, RoundingMode.HALF_EVEN)) : null)
                    .subTotal(itemEntity.getSubTotal() != null ? new Money(itemEntity.getSubTotal().setScale(2, RoundingMode.HALF_EVEN)) : null)
                    .build();
        }).toList() : List.of();

        List<String> failureMessages = (orderEntity.getFailureMessages() != null && !orderEntity.getFailureMessages().isBlank())
                ? new ArrayList<>(Arrays.asList(orderEntity.getFailureMessages().split(";")))
                : new ArrayList<>();

        return Order.Builder.builder()
                .id(orderId)
                .customerId(customerId)
                .businessId(businessId)
                .deliveryAddress(deliveryAddress)
                .price(price)
                .items(items)
                .trackingId(trackingId)
                .orderStatus(orderStatus)
                .failureMessages(failureMessages)
                .build();
    }
}
