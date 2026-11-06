package kh.edu.istad.platform.product.restapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductRequest(
        @NotNull UUID businessId,
        @NotBlank String sku,
        @NotBlank String name,
        String description,
        @NotNull @DecimalMin("0.0") BigDecimal unitPrice,
        @NotBlank String currency,
        @Min(0) int availableQuantity
) {
}
