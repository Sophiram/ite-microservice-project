package kh.edu.istad.iam.application.command;

import java.util.UUID;
import kh.edu.istad.iam.domain.entity.Effect;

public record RevokePermissionFromUserCommand(
        UUID userUuid,
        UUID permissionUuid,
        Effect effect,
        String reason) { }
