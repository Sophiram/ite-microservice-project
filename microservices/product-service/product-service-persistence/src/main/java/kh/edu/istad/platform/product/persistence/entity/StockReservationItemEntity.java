package kh.edu.istad.platform.product.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "stock_reservation_items")
public class StockReservationItemEntity {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private StockReservationEntity reservation;
    @Column(name = "product_id", nullable = false)
    private UUID productId;
    @Column(nullable = false)
    private int quantity;

    protected StockReservationItemEntity() {
    }

    public StockReservationItemEntity(UUID id, UUID productId, int quantity) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
    }

    public void setReservation(StockReservationEntity reservation) { this.reservation = reservation; }
    public UUID getProductId() { return productId; }
    public int getQuantity() { return quantity; }
}
