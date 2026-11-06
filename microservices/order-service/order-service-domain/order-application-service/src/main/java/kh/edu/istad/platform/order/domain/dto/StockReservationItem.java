package kh.edu.istad.platform.order.domain.dto;

import kh.edu.istad.common.domain.valueobject.ProductId;

public record StockReservationItem(ProductId productId, int quantity) {
    public StockReservationItem {
        if (productId == null || quantity <= 0) {
            throw new IllegalArgumentException("A product and positive quantity are required");
        }
    }
}
