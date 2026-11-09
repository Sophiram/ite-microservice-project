package kh.edu.istad.iam.application.usecase.user;

import java.time.Clock;
import java.util.List;
import kh.edu.istad.iam.application.port.out.AssignmentRepository;
import kh.edu.istad.iam.application.result.UserClaims;
import kh.edu.istad.iam.domain.entity.Address;
import kh.edu.istad.iam.domain.entity.Profile;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.domain.entity.UserRole;

final class UserUseCaseSupport {
    static List<UserRole> activeUserRoles(Long userId, AssignmentRepository assignments, Clock clock) {
        if (assignments == null || userId == null) return List.of();
        return assignments.findUserRoles(userId).stream()
                .filter(assignment -> assignment.isActive(clock.instant())).toList();
    }

    static Profile merge(Profile current, Profile patch) {
        if (patch == null) return current;
        if (current == null) return patch;
        return new Profile(value(patch.givenName(), current.givenName()),
                value(patch.familyName(), current.familyName()), value(patch.middleName(), current.middleName()),
                value(patch.nickname(), current.nickname()), value(patch.gender(), current.gender()),
                patch.birthdate() == null ? current.birthdate() : patch.birthdate(),
                value(patch.profileUrl(), current.profileUrl()), value(patch.pictureUrl(), current.pictureUrl()),
                value(patch.websiteUrl(), current.websiteUrl()), value(patch.zoneinfo(), current.zoneinfo()),
                value(patch.locale(), current.locale()), value(patch.phoneNumber(), current.phoneNumber()));
    }

    static Address merge(Address current, Address patch) {
        Address base = current == null ? Address.empty() : current;
        return new Address(value(patch.streetAddress(), base.streetAddress()),
                value(patch.locality(), base.locality()), value(patch.region(), base.region()),
                value(patch.postalCode(), base.postalCode()), value(patch.country(), base.country()));
    }

    static String value(String patch, String current) { return patch == null ? current : patch; }

    static UserClaims userClaims(User user) {
        Profile p = user.getProfile();
        Address a = user.getAddress() == null ? Address.empty() : user.getAddress();
        java.util.Map<String, String> address = new java.util.LinkedHashMap<>();
        put(address, "street_address", a.streetAddress());
        put(address, "locality", a.locality());
        put(address, "region", a.region());
        put(address, "postal_code", a.postalCode());
        put(address, "country", a.country());
        return new UserClaims(user.getEmail(), user.isEmailVerified(), p == null ? null : p.givenName(),
                p == null ? null : p.familyName(), p == null ? null : p.middleName(),
                p == null ? null : p.nickname(), user.getUsername(), p == null ? null : p.profileUrl(),
                p == null ? null : p.pictureUrl(), p == null ? null : p.websiteUrl(),
                p == null ? null : p.gender(), p == null || p.birthdate() == null ? null : p.birthdate().toString(),
                p == null ? null : p.zoneinfo(), p == null ? null : p.locale(), user.getUpdatedAt(),
                p == null ? null : p.phoneNumber(), user.isPhoneNumberVerified(), address);
    }

    static void put(java.util.Map<String, String> map, String key, String value) {
        if (value != null && !value.isBlank()) map.put(key, value);
    }
}
