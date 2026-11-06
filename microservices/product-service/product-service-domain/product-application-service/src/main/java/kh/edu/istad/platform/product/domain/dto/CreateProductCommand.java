package kh.edu.istad.platform.product.domain.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductCommand(UUID businessId, String sku, String name, String description,
                                   BigDecimal unitPrice, String currency, int availableQuantity) {
}
