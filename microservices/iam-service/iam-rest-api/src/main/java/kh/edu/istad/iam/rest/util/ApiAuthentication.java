package kh.edu.istad.iam.rest.util;

import java.util.UUID;
import org.springframework.security.core.Authentication;

public final class ApiAuthentication {
    private ApiAuthentication() { }

    public static UUID userUuid(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }
}
