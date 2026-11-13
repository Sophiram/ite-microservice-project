package kh.edu.istad.platform.order.domain.usecase;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.order.domain.dto.InitialOrderCommand;
import kh.edu.istad.platform.order.domain.dto.InitialOrderResult;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.entity.OrderItem;
import kh.edu.istad.platform.order.domain.entity.Product;
import kh.edu.istad.platform.order.domain.event.OrderCreatedEvent;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.port.out.message.OrderEventPublisher;
import kh.edu.istad.platform.order.domain.service.OrderDomainService;
import kh.edu.istad.platform.order.domain.valueobject.OrderItemId;
import kh.edu.istad.platform.order.domain.valueobject.StreetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
@RequiredArgsConstructor
public class InitialOrderUseCase {
    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    public Mono<InitialOrderResult> execute(InitialOrderCommand command) {
        return Mono.defer(() -> {
            log.info("Initiating order: {}", command);

            if (command == null) {
                return Mono.error(new OrderDomainException("InitialOrderCommand must not be null"));
            }
            if (command.deliveryAddress() == null) {
                return Mono.error(new OrderDomainException("Delivery address must not be null"));
            }
            if (command.items() == null || command.items().isEmpty()) {
                return Mono.error(new OrderDomainException("Order items must not be empty"));
            }

            StreetAddress deliveryAddress = new StreetAddress(
                    UUID.randomUUID(),
                    command.deliveryAddress().street(),
                    command.deliveryAddress().postalCode(),
                    command.deliveryAddress().city()
            );

            AtomicInteger itemIndex = new AtomicInteger(1);
            List<OrderItem> items = command.items().stream()
                    .map(itemCmd -> {
                        Product product = Product.Builder.builder()
                                .id(new ProductId(itemCmd.productId()))
                                .price(new Money(itemCmd.price().setScale(2, RoundingMode.HALF_EVEN)))
                                .build();

                        return OrderItem.Builder.builder()
                                .id(new OrderItemId(itemIndex.getAndIncrement()))
                                .product(product)
                                .quantity(itemCmd.quantity())
                                .price(new Money(itemCmd.price().setScale(2, RoundingMode.HALF_EVEN)))
                                .subTotal(new Money(itemCmd.subTotal().setScale(2, RoundingMode.HALF_EVEN)))
                                .build();
                    })
                    .toList();

            Order order = Order.Builder.builder()
                    .customerId(new CustomerId(command.customerId()))
                    .businessId(new BusinessId(command.businessId()))
                    .deliveryAddress(deliveryAddress)
                    .price(new Money(command.price().setScale(2, RoundingMode.HALF_EVEN)))
                    .items(items)
                    .build();

            OrderCreatedEvent event = orderDomainService.initiateOrder(order);

            // Associate initialized order ID to all order items
            order.getItems().forEach(item -> item.setOrderId(order.getId()));

            return orderRepository.save(event.getOrder())
                    .flatMap(savedOrder -> orderEventPublisher.publishOrderCreated(event)
                            .onErrorResume(e -> {
                                log.error("Failed to publish OrderCreatedEvent to Kafka for orderId={}: {}",
                                        savedOrder.getId().value(), e.getMessage());
                                return Mono.empty();
                            })
                            .thenReturn(savedOrder)
                    )
                    .map(savedOrder -> InitialOrderResult.from(savedOrder, "Order initiated successfully"));
        });
    }
}
