package kh.edu.istad.platform.product.domain.service;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.product.domain.dto.ProductResult;
import kh.edu.istad.platform.product.domain.dto.ReserveStockCommand;
import kh.edu.istad.platform.product.domain.dto.StockReservationResult;
import kh.edu.istad.platform.product.domain.entity.StockReservation;
import kh.edu.istad.platform.product.domain.exception.ProductNotFoundException;
import kh.edu.istad.platform.product.domain.exception.StockReservationConflictException;
import kh.edu.istad.platform.product.domain.port.out.ProductRepository;
import kh.edu.istad.platform.product.domain.port.out.StockReservationRepository;
import kh.edu.istad.platform.product.domain.service.ProductDomainService;
import kh.edu.istad.platform.product.domain.usecase.ReserveStockUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReserveStockService implements ReserveStockUseCase {
    private final ProductRepository products;
    private final StockReservationRepository reservations;
    private final ProductDomainService domainService;

    public ReserveStockService(ProductRepository products, StockReservationRepository reservations,
                               ProductDomainService domainService) {
        this.products = products;
        this.reservations = reservations;
        this.domainService = domainService;
    }

    @Override
    @Transactional
    public StockReservationResult execute(ReserveStockCommand command) {
        validate(command);
        List<StockReservation.Item> items = command.items().stream()
                .collect(Collectors.groupingBy(ReserveStockCommand.Item::productId,
                        Collectors.summingInt(ReserveStockCommand.Item::quantity)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new StockReservation.Item(new ProductId(entry.getKey()), entry.getValue()))
                .toList();

        var existing = reservations.findByOrderIdForUpdate(command.orderId());
        if (existing.isPresent()) {
            return replay(existing.get(), items, command.businessId());
        }

        List<UUID> ids = items.stream().map(item -> item.productId().value()).toList();
        var locked = products.findAllByIdsForUpdate(ids);
        existing = reservations.findByOrderIdForUpdate(command.orderId());
        if (existing.isPresent()) {
            return replay(existing.get(), items, command.businessId());
        }
        if (locked.size() != ids.size()) {
            var found = locked.stream().map(product -> product.id().value()).collect(Collectors.toSet());
            UUID missing = ids.stream().filter(id -> !found.contains(id)).findFirst().orElseThrow();
            throw new ProductNotFoundException(missing);
        }

        var byId = locked.stream().collect(Collectors.toMap(product -> product.id().value(), Function.identity()));
        for (StockReservation.Item item : items) {
            var product = byId.get(item.productId().value());
            if (!product.businessId().equals(new BusinessId(command.businessId()))) {
                throw new IllegalArgumentException("Product does not belong to the requested business");
            }
            domainService.reserve(product, item.quantity());
            products.save(product);
        }
        var reservation = reservations.save(StockReservation.reserve(new OrderId(command.orderId()), items));
        return new StockReservationResult(reservation.id(), locked.stream().map(ProductResult::from).toList());
    }

    private StockReservationResult replay(StockReservation reservation, List<StockReservation.Item> items,
                                          UUID businessId) {
        if (!reservation.items().equals(items)) {
            throw new StockReservationConflictException(reservation.orderId().value());
        }
        var current = products.findAllByIdsForUpdate(items.stream()
                .map(item -> item.productId().value()).sorted(Comparator.naturalOrder()).toList());
        if (current.size() != items.size()) {
            throw new ProductNotFoundException(items.stream()
                    .map(item -> item.productId().value())
                    .filter(id -> current.stream().noneMatch(product -> product.id().value().equals(id)))
                    .findFirst().orElseThrow());
        }
        if (current.stream().anyMatch(product -> !product.businessId().value().equals(businessId))) {
            throw new IllegalArgumentException("Product does not belong to the requested business");
        }
        return new StockReservationResult(reservation.id(), current.stream().map(ProductResult::from).toList());
    }

    private void validate(ReserveStockCommand command) {
        if (command.orderId() == null || command.businessId() == null || command.items().isEmpty()) {
            throw new IllegalArgumentException("orderId, businessId, and at least one item are required");
        }
        if (command.items().stream().anyMatch(item -> item.productId() == null || item.quantity() <= 0)) {
            throw new IllegalArgumentException("Every item must have a productId and a positive quantity");
        }
    }
}
