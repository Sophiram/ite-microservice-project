package kh.edu.istad.platform.product.domain.exception;

import java.util.UUID;

public class StockReservationConflictException extends RuntimeException {
    public StockReservationConflictException(UUID orderId) {
        super("Order already has a reservation with different items: " + orderId);
    }
}
