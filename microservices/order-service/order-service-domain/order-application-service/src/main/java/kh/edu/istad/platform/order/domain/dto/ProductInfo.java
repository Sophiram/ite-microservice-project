package kh.edu.istad.platform.order.domain.dto;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.ProductId;

public record ProductInfo(ProductId id, BusinessId businessId, String sku, String name,
                          String description, Money unitPrice, String currency,
                          int availableQuantity, String status) {
}
