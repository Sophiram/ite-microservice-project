package kh.edu.istad.iam.application.command;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import kh.edu.istad.iam.domain.entity.Effect;

public record GrantPermissionsToUserCommand(
        UUID userUuid,
        List<UUID> permissionUuids,
        Effect effect,
        Instant expiresAt,
        String reason)
{
        public GrantPermissionsToUserCommand {
            permissionUuids = List.copyOf(permissionUuids);
        }
}
