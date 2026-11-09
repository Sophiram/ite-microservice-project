package kh.edu.istad.iam.application.result;

import java.time.LocalDate;
import kh.edu.istad.iam.domain.entity.User;

public record UserProfile(String givenName, String familyName, String middleName, String nickname,
                              String gender, LocalDate birthdate, String profileUrl, String pictureUrl,
                              String websiteUrl, String zoneinfo, String phoneNumber) {
        static UserProfile from(User user) {
            var p = user.getProfile();
            return p == null ? new UserProfile(null, null, null, null, null, null, null, null, null, null, null)
                    : new UserProfile(p.givenName(), p.familyName(), p.middleName(), p.nickname(), p.gender(),
                    p.birthdate(), p.profileUrl(), p.pictureUrl(), p.websiteUrl(), p.zoneinfo(), p.phoneNumber());
        }
    }
