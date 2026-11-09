package kh.edu.istad.iam.application.result;

import java.time.Instant;
import java.util.Map;

public record UserClaims(
        String email,
        boolean emailVerified,
        String givenName,
        String familyName,
        String middleName,
        String nickname,
        String preferredUsername,
        String profile,
        String picture,
        String website,
        String gender,
        String birthdate,
        String zoneinfo,
        String locale,
        Instant updatedAt,
        String phoneNumber,
        boolean phoneNumberVerified,
        Map<String, String> address
) {
        public UserClaims {
            address = java.util.Map.copyOf(address);
        }
    }
