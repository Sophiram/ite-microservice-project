package kh.edu.istad.iam.application.result;

import java.util.List;
import java.util.UUID;

public record AuthUserResult(
        Long id,
        UUID uuid,
        String username,
        String passwordHash,
        boolean enabled,
        boolean accountNonExpired,
        boolean accountNonLocked,
        boolean credentialsNonExpired,
        List<String> authorities,
        UserClaims claims
) {
        public AuthUserResult {
            authorities = List.copyOf(authorities);
        }
    }
