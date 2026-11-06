package kh.edu.istad.platform.order.domain.dto;

import java.util.List;
import java.util.UUID;

public record StockReservationResult(UUID reservationId, List<ProductInfo> products) {
    public StockReservationResult {
        products = List.copyOf(products);
    }
}
