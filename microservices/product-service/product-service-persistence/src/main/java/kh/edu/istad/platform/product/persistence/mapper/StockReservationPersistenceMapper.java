package kh.edu.istad.platform.product.persistence.mapper;

import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.product.domain.entity.StockReservation;
import kh.edu.istad.platform.product.persistence.entity.StockReservationEntity;
import kh.edu.istad.platform.product.persistence.entity.StockReservationItemEntity;

import java.util.UUID;

public final class StockReservationPersistenceMapper {
    private StockReservationPersistenceMapper() {
    }

    public static StockReservation toDomain(StockReservationEntity entity) {
        return new StockReservation(entity.getId(), new OrderId(entity.getOrderId()),
                entity.getItems().stream()
                        .map(item -> new StockReservation.Item(new ProductId(item.getProductId()), item.getQuantity()))
                        .toList(),
                entity.getStatus());
    }

    public static StockReservationEntity toEntity(StockReservation reservation) {
        var entity = new StockReservationEntity(reservation.id(), reservation.orderId().value(), reservation.status());
        entity.setItems(reservation.items().stream()
                .map(item -> new StockReservationItemEntity(UUID.randomUUID(),
                        item.productId().value(), item.quantity()))
                .toList());
        return entity;
    }
}
