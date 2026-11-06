package kh.edu.istad.platform.product.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kh.edu.istad.platform.product.domain.valueobject.ProductStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products", uniqueConstraints = @UniqueConstraint(
        name = "uq_products_business_sku", columnNames = {"business_id", "sku"}))
public class ProductEntity {
    @Id
    private UUID id;
    @Column(name = "business_id", nullable = false)
    private UUID businessId;
    @Column(nullable = false)
    private String sku;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, columnDefinition = "text")
    private String description;
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPrice;
    @Column(nullable = false, length = 3)
    private String currency;
    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;

    protected ProductEntity() {
    }

    public ProductEntity(UUID id, UUID businessId, String sku, String name, String description,
                         BigDecimal unitPrice, String currency, int availableQuantity,
                         ProductStatus status) {
        this.id = id;
        this.businessId = businessId;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.currency = currency;
        this.availableQuantity = availableQuantity;
        this.status = status;
    }

    public UUID getId() { return id; }
    public UUID getBusinessId() { return businessId; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public String getCurrency() { return currency; }
    public int getAvailableQuantity() { return availableQuantity; }
    public ProductStatus getStatus() { return status; }
}
