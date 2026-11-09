package kh.edu.istad.iam.web.form;

public record ProfileForm(
        String givenName,
        String familyName,
        String middleName,
        String nickname,
        String gender,
        String birthdate,
        String profileUrl,
        String pictureUrl,
        String websiteUrl,
        String zoneinfo,
        String locale,
        String phoneNumber,
        String streetAddress,
        String locality,
        String region,
        String postalCode,
        String country
) { }
