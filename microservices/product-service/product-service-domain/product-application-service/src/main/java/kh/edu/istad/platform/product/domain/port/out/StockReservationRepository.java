package kh.edu.istad.platform.product.domain.port.out;

import kh.edu.istad.platform.product.domain.entity.StockReservation;

import java.util.Optional;
import java.util.UUID;

public interface StockReservationRepository {
    Optional<StockReservation> findByOrderId(UUID orderId);
    Optional<StockReservation> findByOrderIdForUpdate(UUID orderId);
    StockReservation save(StockReservation reservation);
}
