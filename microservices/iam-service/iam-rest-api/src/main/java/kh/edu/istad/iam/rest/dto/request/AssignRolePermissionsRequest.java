package kh.edu.istad.iam.rest.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AssignRolePermissionsRequest(@NotEmpty List<@NotNull UUID> permissionUuids,
                                               Instant expiresAt, @Size(max = 255) String reason) { }
