package kh.edu.istad.iam.domain.entity;

import java.time.LocalDate;

/** OIDC "profile" / "phone" standard claims stored on users. */
public record Profile(
        String givenName,
        String familyName,
        String middleName,
        String nickname,
        String gender,
        LocalDate birthdate,
        String profileUrl,
        String pictureUrl,
        String websiteUrl,
        String zoneinfo,
        String locale,
        String phoneNumber) {

    public static Profile of(String givenName, String familyName, String locale) {
        return new Profile(givenName, familyName, null, null, null, null,
                null, null, null, null, locale, null);
    }

    /** OIDC "name" = given + family. */
    public String fullName() {
        return ((givenName == null ? "" : givenName) + " "
                + (familyName == null ? "" : familyName)).trim();
    }
}
