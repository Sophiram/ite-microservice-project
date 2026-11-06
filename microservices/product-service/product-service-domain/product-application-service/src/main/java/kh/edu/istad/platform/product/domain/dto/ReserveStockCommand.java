package kh.edu.istad.platform.product.domain.dto;

import java.util.List;
import java.util.UUID;

public record ReserveStockCommand(UUID orderId, UUID businessId, List<Item> items) {
    public ReserveStockCommand {
        items = items == null ? List.of() : List.copyOf(items);
    }

    public record Item(UUID productId, int quantity) {
    }
}
