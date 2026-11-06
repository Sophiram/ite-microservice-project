package kh.edu.istad.platform.product.domain.entity;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.product.domain.exception.InsufficientStockException;
import kh.edu.istad.platform.product.domain.exception.ProductNotAvailableException;
import kh.edu.istad.platform.product.domain.valueobject.ProductStatus;

import java.util.Objects;

public class Product {
    private final ProductId id;
    private final BusinessId businessId;
    private String sku;
    private String name;
    private String description;
    private Money unitPrice;
    private String currency;
    private int availableQuantity;
    private ProductStatus status;

    public Product(ProductId id, BusinessId businessId, String sku, String name, String description,
                   Money unitPrice, String currency, int availableQuantity, ProductStatus status) {
        this.id = Objects.requireNonNull(id);
        this.businessId = Objects.requireNonNull(businessId);
        this.sku = requireText(sku, "sku");
        this.name = requireText(name, "name");
        this.description = description == null ? "" : description;
        this.unitPrice = Objects.requireNonNull(unitPrice);
        this.currency = requireText(currency, "currency");
        if (unitPrice.amount() == null || unitPrice.amount().signum() < 0 || availableQuantity < 0) {
            throw new IllegalArgumentException("Price and available quantity must not be negative");
        }
        this.availableQuantity = availableQuantity;
        this.status = Objects.requireNonNull(status);
    }

    public void update(String sku, String name, String description, Money unitPrice, String currency,
                       int availableQuantity, ProductStatus status) {
        this.sku = requireText(sku, "sku");
        this.name = requireText(name, "name");
        this.description = description == null ? "" : description;
        this.unitPrice = Objects.requireNonNull(unitPrice);
        if (unitPrice.amount() == null || unitPrice.amount().signum() < 0 || availableQuantity < 0) {
            throw new IllegalArgumentException("Price and available quantity must not be negative");
        }
        this.currency = requireText(currency, "currency");
        this.availableQuantity = availableQuantity;
        this.status = Objects.requireNonNull(status);
    }

    public void reserve(int quantity) {
        if (status != ProductStatus.ACTIVE) {
            throw new ProductNotAvailableException(id.value());
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Reservation quantity must be positive");
        }
        if (quantity > availableQuantity) {
            throw new InsufficientStockException(id.value(), availableQuantity, quantity);
        }
        availableQuantity -= quantity;
    }

    public void release(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Release quantity must be positive");
        }
        availableQuantity = Math.addExact(availableQuantity, quantity);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }

    public ProductId id() { return id; }
    public BusinessId businessId() { return businessId; }
    public String sku() { return sku; }
    public String name() { return name; }
    public String description() { return description; }
    public Money unitPrice() { return unitPrice; }
    public String currency() { return currency; }
    public int availableQuantity() { return availableQuantity; }
    public ProductStatus status() { return status; }
}
