package kh.edu.istad.platform.customer.domain.dto;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegment;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;

/**
 * Dynamic filter used by GET /api/customers/filter.
 * All fields are optional — only non-null fields are applied to the query.
 */
public record CustomerFilter(
        String username,
        String familyName,
        String givenName,
        String email,
        String phoneNumber,
        CustomerStatus status,
        CustomerSegment segment  // Task 2: also filter by segment (SILVER/GOLD/PREMIUM)
) {
}
