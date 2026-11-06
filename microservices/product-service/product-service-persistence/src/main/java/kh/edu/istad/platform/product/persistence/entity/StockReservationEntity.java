package kh.edu.istad.platform.product.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import kh.edu.istad.platform.product.domain.valueobject.ReservationStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "stock_reservations")
public class StockReservationEntity {
    @Id
    private UUID id;
    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "item_order")
    private List<StockReservationItemEntity> items = new ArrayList<>();

    protected StockReservationEntity() {
    }

    public StockReservationEntity(UUID id, UUID orderId, ReservationStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
    }

    public void setStatus(ReservationStatus status) { this.status = status; }
    public void setItems(List<StockReservationItemEntity> items) {
        this.items.clear();
        this.items.addAll(items);
        this.items.forEach(item -> item.setReservation(this));
    }
    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public ReservationStatus getStatus() { return status; }
    public List<StockReservationItemEntity> getItems() { return items; }
}
