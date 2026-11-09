package kh.edu.istad.iam.rest.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import kh.edu.istad.iam.domain.entity.Effect;

public record GrantPermissionsToUserRequest(
        @NotEmpty
        List<@NotNull UUID> permissionUuids,

        @NotNull Effect effect,
        Instant expiresAt,

        @Size(max = 255)
        String reason
) { }
