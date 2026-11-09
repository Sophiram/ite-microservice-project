package kh.edu.istad.iam.rest.dto.response;

import java.time.Instant;
import java.util.UUID;
import kh.edu.istad.iam.application.result.IdentityResult;

public record IdentityResponse(
        UUID uuid,
        String provider,
        String email,
        boolean emailVerified,
        String displayName,
        String username,
        String pictureUrl,
        Instant linkedAt
) {
        public static IdentityResponse from(IdentityResult identity) {
            return new IdentityResponse(identity.uuid(), identity.provider(), identity.email(),
                    identity.emailVerified(), identity.displayName(), identity.username(),
                    identity.pictureUrl(), identity.linkedAt());
        }
    }
