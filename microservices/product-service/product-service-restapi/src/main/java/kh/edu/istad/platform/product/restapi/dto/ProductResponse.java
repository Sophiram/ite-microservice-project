package kh.edu.istad.platform.product.restapi.dto;

import kh.edu.istad.platform.product.domain.dto.ProductResult;
import kh.edu.istad.platform.product.domain.valueobject.ProductStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(UUID id, UUID businessId, String sku, String name, String description,
                              BigDecimal unitPrice, String currency, int availableQuantity,
                              ProductStatus status) {
    public static ProductResponse from(ProductResult product) {
        return new ProductResponse(product.id(), product.businessId(), product.sku(), product.name(),
                product.description(), product.unitPrice(), product.currency(),
                product.availableQuantity(), product.status());
    }
}
