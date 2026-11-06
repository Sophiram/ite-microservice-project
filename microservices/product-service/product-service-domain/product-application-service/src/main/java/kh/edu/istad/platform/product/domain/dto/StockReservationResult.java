package kh.edu.istad.platform.product.domain.dto;

import java.util.List;
import java.util.UUID;

public record StockReservationResult(UUID reservationId, List<ProductResult> products) {
    public StockReservationResult {
        products = List.copyOf(products);
    }
}
