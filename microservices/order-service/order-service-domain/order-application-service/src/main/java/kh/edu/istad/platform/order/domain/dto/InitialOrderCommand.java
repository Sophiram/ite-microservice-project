package kh.edu.istad.platform.order.domain.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record InitialOrderCommand(
        UUID customerId,
        UUID businessId,
        BigDecimal price,
        OrderAddressCommand deliveryAddress,
        List<OrderItemCommand> items
) {
    public record OrderAddressCommand(
            String street,
            String postalCode,
            String city
    ) {}

    public record OrderItemCommand(
            UUID productId,
            Integer quantity,
            BigDecimal price,
            BigDecimal subTotal
    ) {}
}
