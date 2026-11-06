package kh.edu.istad.platform.product.persistence.repository;

import jakarta.persistence.LockModeType;
import kh.edu.istad.platform.product.persistence.entity.StockReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StockReservationJpaRepository extends JpaRepository<StockReservationEntity, UUID> {
    Optional<StockReservationEntity> findByOrderId(UUID orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from StockReservationEntity r left join fetch r.items where r.orderId = :orderId")
    Optional<StockReservationEntity> findByOrderIdForUpdate(@Param("orderId") UUID orderId);
}
