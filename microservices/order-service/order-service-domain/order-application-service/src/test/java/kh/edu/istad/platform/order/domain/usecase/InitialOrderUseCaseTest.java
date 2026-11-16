package kh.edu.istad.platform.order.domain.usecase;

import kh.edu.istad.common.domain.valueobject.OrderStatus;
import kh.edu.istad.platform.order.domain.dto.InitialOrderCommand;
import kh.edu.istad.platform.order.domain.dto.InitialOrderResult;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;
import kh.edu.istad.platform.order.domain.port.out.OrderRepository;
import kh.edu.istad.platform.order.domain.service.OrderDomainService;
import kh.edu.istad.platform.order.domain.service.OrderDomainServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitialOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private kh.edu.istad.platform.order.domain.port.out.message.OrderEventPublisher orderEventPublisher;

    private OrderDomainService orderDomainService;
    private InitialOrderUseCase initialOrderUseCase;

    @BeforeEach
    void setUp() {
        orderDomainService = new OrderDomainServiceImpl();
        initialOrderUseCase = new InitialOrderUseCase(orderDomainService, orderRepository, orderEventPublisher);
    }


    @Test
    void execute_successfulInitialOrder() {
        UUID customerId = UUID.randomUUID();
        UUID businessId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        InitialOrderCommand.OrderAddressCommand address = new InitialOrderCommand.OrderAddressCommand(
                "St. 2004", "12000", "Phnom Penh"
        );

        InitialOrderCommand.OrderItemCommand item = new InitialOrderCommand.OrderItemCommand(
                productId,
                2,
                new BigDecimal("25.00"),
                new BigDecimal("50.00")
        );

        InitialOrderCommand command = new InitialOrderCommand(
                customerId,
                businessId,
                new BigDecimal("50.00"),
                address,
                List.of(item)
        );

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            return Mono.just(saved);
        });
        when(orderEventPublisher.publishOrderCreated(any())).thenReturn(Mono.empty());


        StepVerifier.create(initialOrderUseCase.execute(command))
                .assertNext(result -> {
                    assertNotNull(result.orderId());
                    assertNotNull(result.trackingId());
                    assertEquals(OrderStatus.PENDING, result.orderStatus());
                    assertEquals("Order initiated successfully", result.message());
                })
                .verifyComplete();
    }

    @Test
    void execute_nullCommand_throwsException() {
        StepVerifier.create(initialOrderUseCase.execute(null))
                .expectError(OrderDomainException.class)
                .verify();
    }

    @Test
    void execute_emptyItems_throwsException() {
        InitialOrderCommand command = new InitialOrderCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("50.00"),
                new InitialOrderCommand.OrderAddressCommand("St. 1", "12000", "City"),
                List.of()
        );

        StepVerifier.create(initialOrderUseCase.execute(command))
                .expectError(OrderDomainException.class)
                .verify();
    }

    @Test
    void execute_priceMismatch_throwsException() {
        UUID customerId = UUID.randomUUID();
        UUID businessId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        InitialOrderCommand.OrderAddressCommand address = new InitialOrderCommand.OrderAddressCommand(
                "St. 2004", "12000", "Phnom Penh"
        );

        InitialOrderCommand.OrderItemCommand item = new InitialOrderCommand.OrderItemCommand(
                productId,
                2,
                new BigDecimal("25.00"),
                new BigDecimal("50.00")
        );

        // Given price is 60.00 but items subtotal is 50.00
        InitialOrderCommand command = new InitialOrderCommand(
                customerId,
                businessId,
                new BigDecimal("60.00"),
                address,
                List.of(item)
        );

        StepVerifier.create(initialOrderUseCase.execute(command))
                .expectErrorMatches(throwable -> throwable instanceof OrderDomainException
                        && throwable.getMessage().contains("Price is not equal to order sub total"))
                .verify();
    }
}
