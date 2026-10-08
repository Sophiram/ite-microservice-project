package kh.edu.istad.platform.customer.restapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerInitiateRequest(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Family name is required")
        String familyName,

        @NotBlank(message = "Given name is required")
        String givenName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        String email,

        @NotBlank(message = "Phone number is required")
        String phoneNumber
) {
}
