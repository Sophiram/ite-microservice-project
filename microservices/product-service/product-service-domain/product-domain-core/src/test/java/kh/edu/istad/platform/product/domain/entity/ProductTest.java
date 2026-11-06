package kh.edu.istad.platform.product.domain.entity;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.product.domain.exception.InsufficientStockException;
import kh.edu.istad.platform.product.domain.exception.ProductNotAvailableException;
import kh.edu.istad.platform.product.domain.valueobject.ProductStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductTest {
    private Product product(ProductStatus status) {
        return new Product(new ProductId(UUID.randomUUID()), new BusinessId(UUID.randomUUID()), "SKU-1",
                "Product", "", new Money(new BigDecimal("10.00")), "USD", 5, status);
    }

    @Test
    void reserveAndReleaseAdjustAvailableQuantity() {
        Product product = product(ProductStatus.ACTIVE);

        product.reserve(3);
        assertEquals(2, product.availableQuantity());
        product.release(3);
        assertEquals(5, product.availableQuantity());
    }

    @Test
    void reserveRejectsInactiveAndInsufficientStock() {
        assertThrows(ProductNotAvailableException.class, () -> product(ProductStatus.INACTIVE).reserve(1));
        Product product = product(ProductStatus.ACTIVE);
        assertThrows(InsufficientStockException.class, () -> product.reserve(6));
        assertEquals(5, product.availableQuantity());
    }

    @Test
    void releasingReservationIsIdempotent() {
        StockReservation reservation = StockReservation.reserve(
                new kh.edu.istad.common.domain.valueobject.OrderId(UUID.randomUUID()),
                java.util.List.of(new StockReservation.Item(new ProductId(UUID.randomUUID()), 2)));

        assertTrue(reservation.release());
        assertFalse(reservation.release());
        assertEquals(kh.edu.istad.platform.product.domain.valueobject.ReservationStatus.RELEASED,
                reservation.status());
    }
}
