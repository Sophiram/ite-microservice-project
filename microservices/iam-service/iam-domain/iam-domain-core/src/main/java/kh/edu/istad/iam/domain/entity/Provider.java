package kh.edu.istad.iam.domain.entity;

import java.util.Locale;

/** Supported social-login providers (identities.provider). */
public enum Provider {
    GOOGLE, GITHUB, FACEBOOK, TELEGRAM;

    /** Spring registration id: google, github, facebook, telegram. */
    public String registrationId() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Provider fromRegistrationId(String registrationId) {
        return valueOf(registrationId.trim().toUpperCase(Locale.ROOT));
    }
}
