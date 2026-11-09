package kh.edu.istad.iam.application.command;

import java.util.List;
import java.util.UUID;

public record CreateRoleCommand(
        String name,
        String description,
        List<UUID> permissionUuids)
{
        public CreateRoleCommand {
            permissionUuids = permissionUuids == null ? List.of() : List.copyOf(permissionUuids);
        }
}
