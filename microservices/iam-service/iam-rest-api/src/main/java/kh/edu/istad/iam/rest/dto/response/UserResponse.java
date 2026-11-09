package kh.edu.istad.iam.rest.dto.response;

import java.time.Instant;
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
        List<RoleSummary> roles,
        Instant createdAt,
        Instant updatedAt
) {
        public static UserResponse from(UserResult user) {
            return new UserResponse(user.uuid(), user.username(), user.email(), user.emailVerified(),
                    user.fullName(), user.locale(), user.enabled(), user.roles().stream()
                    .map(role -> new RoleSummary(role.uuid(), role.name())).toList(),
                    user.createdAt(), user.updatedAt());
        }
    }
