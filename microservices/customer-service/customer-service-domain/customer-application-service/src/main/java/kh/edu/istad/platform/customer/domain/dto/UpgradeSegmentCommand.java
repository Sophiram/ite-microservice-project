package kh.edu.istad.platform.customer.domain.dto;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegment;

import java.util.UUID;

/**
 * Command to upgrade a customer's segment.
 * Business rule: segments can only move SILVER → GOLD → PREMIUM (no downgrade).
 */
public record UpgradeSegmentCommand(
        UUID customerId,
        CustomerSegment newSegment
) {
}
