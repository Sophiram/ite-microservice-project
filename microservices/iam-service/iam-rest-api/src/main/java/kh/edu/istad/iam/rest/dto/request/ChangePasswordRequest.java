package kh.edu.istad.iam.rest.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank
        String currentPassword,

        @NotBlank @Size(min = 8, max = 128)
        String newPassword,

        @NotBlank
        String confirmPassword
) {
        @AssertTrue(message = "newPassword and confirmPassword must match")
        public boolean isPasswordConfirmed() {
            return newPassword != null && newPassword.equals(confirmPassword);
        }
    }
