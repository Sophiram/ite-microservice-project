package kh.edu.istad.iam.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import kh.edu.istad.iam.application.result.RoleSummary;
import kh.edu.istad.iam.application.result.UserResult;

public record UserResponse(
        UUID uuid,
        String username,
        String email,
        boolean emailVerified,
        String name,
        String locale,
        boolean enabled,
        @JsonProperty("accountNonLocked")
        boolean accountNonLocked,
        @JsonProperty("locked")
        boolean locked,
        String givenName,
        String familyName,
        String middleName,
        String nickname,
        String gender,
        LocalDate birthdate,
        String profileUrl,
        String pictureUrl,
        String websiteUrl,
        String zoneinfo,
        String phoneNumber,
        List<RoleSummary> roles,
        Instant createdAt,
        Instant updatedAt
) {
        public static UserResponse from(UserResult user) {
            var p = user.profile();
            boolean nonLocked = user.accountNonLocked();
            return new UserResponse(
                    user.uuid(),
                    user.username(),
                    user.email(),
                    user.emailVerified(),
                    user.fullName(),
                    user.locale(),
                    user.enabled(),
                    nonLocked,
                    !nonLocked,
                    p != null ? p.givenName() : null,
                    p != null ? p.familyName() : null,
                    p != null ? p.middleName() : null,
                    p != null ? p.nickname() : null,
                    p != null ? p.gender() : null,
                    p != null ? p.birthdate() : null,
                    p != null ? p.profileUrl() : null,
                    p != null ? p.pictureUrl() : null,
                    p != null ? p.websiteUrl() : null,
                    p != null ? p.zoneinfo() : null,
                    p != null ? p.phoneNumber() : null,
                    user.roles().stream()
                            .map(role -> new RoleSummary(role.uuid(), role.name())).toList(),
                    user.createdAt(),
                    user.updatedAt());
        }
    }
