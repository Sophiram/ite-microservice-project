package kh.edu.istad.iam.application.command;

import java.util.UUID;

public record RevokeRoleFromUserCommand(
        UUID userUuid,
        UUID roleUuid,
        String reason) { }
