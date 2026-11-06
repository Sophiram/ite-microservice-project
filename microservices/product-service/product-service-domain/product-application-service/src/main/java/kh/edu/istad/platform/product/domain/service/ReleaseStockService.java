package kh.edu.istad.platform.product.domain.service;

import kh.edu.istad.platform.product.domain.dto.ReleaseStockCommand;
import kh.edu.istad.platform.product.domain.port.out.ProductRepository;
import kh.edu.istad.platform.product.domain.port.out.StockReservationRepository;
import kh.edu.istad.platform.product.domain.usecase.ReleaseStockUseCase;
import kh.edu.istad.platform.product.domain.valueobject.ReservationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReleaseStockService implements ReleaseStockUseCase {
    private final ProductRepository products;
    private final StockReservationRepository reservations;
    private final ProductDomainService domainService;

    public ReleaseStockService(ProductRepository products, StockReservationRepository reservations,
                               ProductDomainService domainService) {
        this.products = products;
        this.reservations = reservations;
        this.domainService = domainService;
    }

    @Override
    @Transactional
    public boolean execute(ReleaseStockCommand command) {
        if (command.orderId() == null) {
            throw new IllegalArgumentException("orderId is required");
        }
        var reservation = reservations.findByOrderIdForUpdate(command.orderId()).orElse(null);
        if (reservation == null) {
            return false;
        }
        if (reservation.status() == ReservationStatus.RELEASED) {
            return false;
        }
        var locked = products.findAllByIdsForUpdate(reservation.items().stream()
                .map(item -> item.productId().value()).sorted().toList());
        var byId = locked.stream().collect(java.util.stream.Collectors.toMap(
                product -> product.id().value(), java.util.function.Function.identity()));
        for (var item : reservation.items()) {
            var product = byId.get(item.productId().value());
            if (product == null) {
                throw new IllegalStateException("Product missing while releasing stock: " + item.productId().value());
            }
            domainService.release(product, item.quantity());
            products.save(product);
        }
        reservation.release();
        reservations.save(reservation);
        return true;
    }
}
