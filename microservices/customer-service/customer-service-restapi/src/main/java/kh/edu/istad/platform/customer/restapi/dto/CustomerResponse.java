package kh.edu.istad.platform.customer.restapi.dto;

import java.util.UUID;

/**
 * Response DTO returned to API consumers for customer read operations.
 * Includes Task 2 field: segment (SILVER / GOLD / PREMIUM).
 */
public record CustomerResponse(
        UUID customerId,
        String username,
        String familyName,
        String givenName,
        String email,
        String phoneNumber,
        String status,
        String segment   // Task 2: CustomerSegment stored as String (SILVER/GOLD/PREMIUM)
) {
}
