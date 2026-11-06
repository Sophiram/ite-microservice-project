package kh.edu.istad.platform.product.domain.dto;

import kh.edu.istad.platform.product.domain.entity.Product;
import kh.edu.istad.platform.product.domain.valueobject.ProductStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResult(UUID id, UUID businessId, String sku, String name, String description,
                            BigDecimal unitPrice, String currency, int availableQuantity,
                            ProductStatus status) {
    public static ProductResult from(Product product) {
        return new ProductResult(product.id().value(), product.businessId().value(), product.sku(),
                product.name(), product.description(), product.unitPrice().amount(),
                product.currency(), product.availableQuantity(), product.status());
    }
}
