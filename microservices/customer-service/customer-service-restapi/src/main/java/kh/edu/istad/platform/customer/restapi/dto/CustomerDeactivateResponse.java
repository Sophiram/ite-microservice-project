package kh.edu.istad.platform.customer.restapi.dto;

import java.util.UUID;

public record CustomerDeactivateResponse(
        UUID customerId,
        String status,
        String message
) {
}
