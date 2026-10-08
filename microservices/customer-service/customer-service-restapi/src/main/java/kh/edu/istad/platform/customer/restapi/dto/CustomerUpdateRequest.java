package kh.edu.istad.platform.customer.restapi.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerUpdateRequest(
        @NotBlank(message = "Family name is required")
        String familyName,

        @NotBlank(message = "Given name is required")
        String givenName
) {
}
