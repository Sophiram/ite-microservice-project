package kh.edu.istad.iam.rest.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kh.edu.istad.iam.application.command.RegisterUserCommand;
import kh.edu.istad.iam.domain.entity.Profile;

public record RegisterUserRequest(
            @NotBlank @Size(max = 50)
            String username,

            @NotBlank @Email @Size(max = 255)
            String email,

            @NotBlank @Size(min = 8, max = 128)
            String password,

            @NotBlank
            String confirmPassword,

            @NotBlank @Size(max = 100)
            String givenName,

            @NotBlank @Size(max = 100)
            String familyName,

            @Pattern(regexp = "en|km")
            String locale) {
        @AssertTrue(message = "password and confirmPassword must match")
        public boolean isPasswordConfirmed() {
            return password != null && password.equals(confirmPassword);
        }
        public RegisterUserCommand command() {
            return new RegisterUserCommand(username, email, password,
                    Profile.of(givenName, familyName, locale == null ? "en" : locale));
        }
    }
