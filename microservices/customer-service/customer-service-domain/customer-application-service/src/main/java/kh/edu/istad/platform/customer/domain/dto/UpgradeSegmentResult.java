package kh.edu.istad.platform.customer.domain.dto;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegment;

import java.util.UUID;

/**
 * Result returned after upgrading a customer's segment.
 */
public record UpgradeSegmentResult(
        UUID customerId,
        String username,
        CustomerSegment segment,
        String message
) {
}
