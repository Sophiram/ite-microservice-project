package kh.edu.istad.iam.application.command;

import java.util.Map;
import kh.edu.istad.iam.domain.entity.Provider;

public record FederatedIdentityCommand(
        Provider provider,
        String subject,
        String username,
        String email,
        boolean emailVerified,
        String displayName,
        String pictureUrl,
        Map<String, Object> attributes)
{
        public FederatedIdentityCommand {
            if (attributes == null || attributes.isEmpty()) {
                attributes = Map.of();
            } else {
                java.util.Map<String, Object> safe = new java.util.LinkedHashMap<>();
                attributes.forEach((k, v) -> {
                    if (k != null && v != null) {
                        safe.put(k, v);
                    }
                });
                attributes = java.util.Collections.unmodifiableMap(safe);
            }
        }
    }
