package kh.edu.istad.iam.application.command;

import java.util.UUID;

public record UpdatePermissionCommand(
        UUID permissionUuid,
        String description) { }
