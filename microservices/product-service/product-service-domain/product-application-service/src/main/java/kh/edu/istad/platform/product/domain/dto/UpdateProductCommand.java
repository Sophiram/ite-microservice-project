package kh.edu.istad.platform.product.domain.dto;

import kh.edu.istad.platform.product.domain.valueobject.ProductStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductCommand(UUID id, String sku, String name, String description, BigDecimal unitPrice,
                                   String currency, int availableQuantity, ProductStatus status) {
}
