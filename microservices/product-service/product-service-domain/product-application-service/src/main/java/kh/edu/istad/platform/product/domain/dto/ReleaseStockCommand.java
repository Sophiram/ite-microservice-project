package kh.edu.istad.platform.product.domain.dto;

import java.util.UUID;

public record ReleaseStockCommand(UUID orderId, String reason) {
}
