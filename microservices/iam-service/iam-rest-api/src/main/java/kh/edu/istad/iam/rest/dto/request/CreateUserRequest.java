package kh.edu.istad.iam.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import kh.edu.istad.iam.application.command.CreateUserCommand;
import kh.edu.istad.iam.domain.entity.Profile;

public record CreateUserRequest(
            @NotBlank @Size(max = 50)
            String username,

            @NotBlank @Email @Size(max = 255)
            String email,

            @NotBlank @Size(min = 8, max = 128)
            String password,

            @NotBlank @Size(max = 100)
            String givenName,

            @NotBlank @Size(max = 100)
            String familyName,

            @Pattern(regexp = "female|male|other")
            String gender,

            LocalDate birthdate,

            @Pattern(regexp = "^$|^\\+[1-9][0-9]{7,14}$")
            String phoneNumber,

            @Size(max = 50)
            String zoneinfo,

            @Pattern(regexp = "en|km")
            String locale,

            @Valid AddressRequest address,

            List<UUID> roleUuids
)
{
        public CreateUserCommand command() {
            Profile profile = new Profile(givenName, familyName, null, null, gender, birthdate,
                    null, null, null, zoneinfo, locale == null ? "en" : locale, phoneNumber);
            return new CreateUserCommand(username, email, password, profile,
                    address == null ? null : address.domain(), roleUuids);
        }
    }
