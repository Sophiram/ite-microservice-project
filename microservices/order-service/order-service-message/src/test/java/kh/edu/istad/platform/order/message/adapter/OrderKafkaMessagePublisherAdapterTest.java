package kh.edu.istad.platform.order.message.adapter;

import kh.edu.istad.common.domain.valueobject.BusinessId;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.order.domain.entity.Order;
import kh.edu.istad.platform.order.domain.event.OrderCreatedEvent;
import kh.edu.istad.platform.order.message.producer.OrderEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.SendResult;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderKafkaMessagePublisherAdapterTest {

    @Mock
    private OrderEventProducer orderEventProducer;

    private OrderKafkaMessagePublisherAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new OrderKafkaMessagePublisherAdapter(orderEventProducer);
    }

    @Test
    void publishOrderCreated_successfullyPublishesEvent() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID businessId = UUID.randomUUID();

        Order order = Order.Builder.builder()
                .id(new OrderId(orderId))
                .customerId(new CustomerId(customerId))
                .businessId(new BusinessId(businessId))
                .price(new Money(new BigDecimal("100.00")))
                .items(List.of())
                .build();

        OrderCreatedEvent domainEvent = new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of("UTC")));

        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(null);
        when(orderEventProducer.publish(any())).thenReturn(future);

        StepVerifier.create(adapter.publishOrderCreated(domainEvent))
                .verifyComplete();

        ArgumentCaptor<kh.edu.istad.platform.order.message.event.OrderCreatedEvent> captor =
                ArgumentCaptor.forClass(kh.edu.istad.platform.order.message.event.OrderCreatedEvent.class);
        verify(orderEventProducer).publish(captor.capture());

        kh.edu.istad.platform.order.message.event.OrderCreatedEvent captured = captor.getValue();
        assertEquals(orderId, captured.orderId());
        assertEquals(customerId, captured.customerId());
        assertEquals(businessId, captured.businessId());
        assertEquals(new BigDecimal("100.00"), captured.totalAmount());
        assertNotNull(captured.createdAt());
    }
}
