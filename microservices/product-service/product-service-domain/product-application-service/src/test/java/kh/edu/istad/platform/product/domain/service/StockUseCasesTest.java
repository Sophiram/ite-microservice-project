package kh.edu.istad.platform.product.domain.service;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.product.domain.dto.ReserveStockCommand;
import kh.edu.istad.platform.product.domain.entity.Product;
import kh.edu.istad.platform.product.domain.entity.StockReservation;
import kh.edu.istad.platform.product.domain.port.out.ProductRepository;
import kh.edu.istad.platform.product.domain.port.out.StockReservationRepository;
import kh.edu.istad.platform.product.domain.valueobject.ProductStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StockUseCasesTest {
    @Test
    void reserveAndReleaseAreIdempotentByOrderId() {
        UUID productUuid = UUID.randomUUID();
        UUID businessUuid = UUID.randomUUID();
        UUID orderUuid = UUID.randomUUID();
        var product = new Product(new ProductId(productUuid), new BusinessId(businessUuid),
                "SKU-1", "Product", "", new Money(new BigDecimal("5.00")),
                "USD", 5, ProductStatus.ACTIVE);
        var products = new InMemoryProducts(product);
        var reservations = new InMemoryReservations();
        var domain = new ProductDomainService();
        var reserve = new ReserveStockService(products, reservations, domain);
        var release = new ReleaseStockService(products, reservations, domain);
        var command = new ReserveStockCommand(orderUuid, businessUuid,
                List.of(new ReserveStockCommand.Item(productUuid, 3)));

        var firstResult = reserve.execute(command);
        var repeatedResult = reserve.execute(command);
        assertEquals(firstResult.reservationId(), repeatedResult.reservationId());
        assertEquals(2, product.availableQuantity());

        assertTrue(release.execute(new kh.edu.istad.platform.product.domain.dto.ReleaseStockCommand(orderUuid, "test")));
        assertFalse(release.execute(new kh.edu.istad.platform.product.domain.dto.ReleaseStockCommand(orderUuid, "retry")));
        assertEquals(5, product.availableQuantity());
    }

    private static class InMemoryProducts implements ProductRepository {
        private final Map<UUID, Product> products = new HashMap<>();

        private InMemoryProducts(Product product) {
            products.put(product.id().value(), product);
        }

        @Override
        public Product save(Product product) {
            products.put(product.id().value(), product);
            return product;
        }

        @Override
        public Optional<Product> findById(UUID id) {
            return Optional.ofNullable(products.get(id));
        }

        @Override
        public List<Product> findAll() {
            return List.copyOf(products.values());
        }

        @Override
        public List<Product> findAllByIdsForUpdate(List<UUID> ids) {
            return ids.stream().map(products::get).filter(java.util.Objects::nonNull).toList();
        }
    }

    private static class InMemoryReservations implements StockReservationRepository {
        private final Map<UUID, StockReservation> byOrderId = new HashMap<>();

        @Override
        public Optional<StockReservation> findByOrderId(UUID orderId) {
            return Optional.ofNullable(byOrderId.get(orderId));
        }

        @Override
        public Optional<StockReservation> findByOrderIdForUpdate(UUID orderId) {
            return findByOrderId(orderId);
        }

        @Override
        public StockReservation save(StockReservation reservation) {
            byOrderId.put(reservation.orderId().value(), reservation);
            return reservation;
        }
    }
}
