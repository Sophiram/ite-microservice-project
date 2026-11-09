package kh.edu.istad.iam.application.result;

import java.time.Instant;
import java.util.UUID;

public record UserRoleAuthority(
        UUID uuid,
        String name,
        Instant expiresAt
) { }
