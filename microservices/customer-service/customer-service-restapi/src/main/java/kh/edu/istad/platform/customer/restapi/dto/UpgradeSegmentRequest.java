package kh.edu.istad.platform.customer.restapi.dto;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegment;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for PATCH /api/customers/{id}/segment
 * Specifies the new target segment (SILVER, GOLD, or PREMIUM).
 */
public record UpgradeSegmentRequest(
        @NotNull(message = "Segment is required")
        CustomerSegment segment
) {
}
