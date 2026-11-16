package kh.edu.istad.iam.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kh.edu.istad.iam.application.command.RegisterUserCommand;
import kh.edu.istad.iam.domain.entity.Profile;

public record RegisterForm(
        @NotBlank @Size(max = 50)
        String username,
        @NotBlank @Email @Size(max = 255)
        String email,
        @NotBlank @Size(min = 8, max = 72)
        String password,
        @NotBlank
        String confirmPassword,
        @NotBlank @Size(max = 100)
        String givenName,
        @NotBlank @Size(max = 100)
        String familyName,
        String locale
) {
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }

    public RegisterUserCommand toCommand() {
        String loc = "km".equalsIgnoreCase(locale) ? "km" : "en";
        return new RegisterUserCommand(username, email, password, Profile.of(givenName, familyName, loc));
    }
}
