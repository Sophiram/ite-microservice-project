package kh.edu.istad.iam.application.result;

import java.time.Instant;
import java.util.UUID;
import kh.edu.istad.iam.domain.entity.Identity;

public record IdentityResult(Long id, UUID uuid, Long userId, String provider, String subject,
                                 String email, boolean emailVerified, String displayName,
                                 String username, String pictureUrl, Instant linkedAt) {
        public static IdentityResult from(Identity identity) {
            return new IdentityResult(identity.getId(), identity.getUuid(), identity.getUserId(),
                    identity.getProvider().name(), identity.getProviderSubject(), identity.getEmail(),
                    identity.isEmailVerified(), identity.getDisplayName(), identity.getUsername(),
                    identity.getPictureUrl(), identity.getLinkedAt());
        }
    }
