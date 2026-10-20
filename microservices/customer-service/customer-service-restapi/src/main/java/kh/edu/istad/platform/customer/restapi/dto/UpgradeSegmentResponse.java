package kh.edu.istad.platform.customer.restapi.dto;

import java.util.UUID;

/**
 * Response for segment upgrade operations.
 */
public record UpgradeSegmentResponse(
        UUID customerId,
        String username,
        String segment,
        String message
) {
}
