package kh.edu.istad.platform.product.domain.entity;

import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.product.domain.valueobject.ReservationStatus;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StockReservation {
    private final UUID id;
    private final OrderId orderId;
    private final List<Item> items;
    private ReservationStatus status;

    public StockReservation(UUID id, OrderId orderId, List<Item> items, ReservationStatus status) {
        this.id = Objects.requireNonNull(id);
        this.orderId = Objects.requireNonNull(orderId);
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Reservation items must not be empty");
        }
        this.items = List.copyOf(items);
        this.status = Objects.requireNonNull(status);
    }

    public static StockReservation reserve(OrderId orderId, List<Item> items) {
        return new StockReservation(UUID.randomUUID(), orderId, items, ReservationStatus.RESERVED);
    }

    public boolean release() {
        if (status == ReservationStatus.RELEASED) {
            return false;
        }
        status = ReservationStatus.RELEASED;
        return true;
    }

    public UUID id() { return id; }
    public OrderId orderId() { return orderId; }
    public List<Item> items() { return items; }
    public ReservationStatus status() { return status; }

    public record Item(ProductId productId, int quantity) {
        public Item {
            Objects.requireNonNull(productId);
            if (quantity <= 0) {
                throw new IllegalArgumentException("Reservation quantity must be positive");
            }
        }
    }
}
