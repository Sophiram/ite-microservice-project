package kh.edu.istad.platform.customer.domain.dto;

import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;

import java.util.UUID;

public record DeactivateCustomerResult(
        UUID customerId,
        CustomerStatus status,
        String message
) {
}
