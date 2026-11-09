package kh.edu.istad.iam.application.command;

import java.util.UUID;

public record RevokePermissionFromRoleCommand(
        UUID roleUuid,
        UUID permissionUuid,
        String reason) { }
