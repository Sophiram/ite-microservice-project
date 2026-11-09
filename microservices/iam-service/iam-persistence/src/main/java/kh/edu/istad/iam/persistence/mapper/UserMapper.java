package kh.edu.istad.iam.persistence.mapper;

import kh.edu.istad.iam.domain.entity.Address;
import kh.edu.istad.iam.domain.entity.Profile;
import kh.edu.istad.iam.domain.entity.User;
import kh.edu.istad.iam.persistence.entity.UserEntity;

public final class UserMapper {
    private UserMapper() { }

    public static User toDomain(UserEntity entity) {
        User user = new User();
        user.setUsername(entity.getUsername());
        user.setEmail(entity.getEmail());
        user.setEmailVerified(entity.isEmailVerified());
        user.setPasswordHash(entity.getPasswordHash());
        user.setProfile(new Profile(entity.getGivenName(), entity.getFamilyName(), entity.getMiddleName(),
                entity.getNickname(), entity.getGender(), entity.getBirthdate(), entity.getProfileUrl(),
                entity.getPictureUrl(), entity.getWebsiteUrl(), entity.getZoneinfo(), entity.getLocale(),
                entity.getPhoneNumber()));
        user.setAddress(new Address(entity.getAddressStreetAddress(), entity.getAddressLocality(),
                entity.getAddressRegion(), entity.getAddressPostalCode(), entity.getAddressCountry()));
        user.setPhoneNumberVerified(entity.isPhoneNumberVerified());
        user.setEnabled(entity.isEnabled());
        user.setAccountNonExpired(entity.isAccountNonExpired());
        user.setAccountNonLocked(entity.isAccountNonLocked());
        user.setCredentialsNonExpired(entity.isCredentialsNonExpired());
        user.setFailedLoginAttempts(entity.getFailedLoginAttempts());
        user.setLastLoginAt(entity.getLastLoginAt());
        user.setPasswordChangedAt(entity.getPasswordChangedAt());
        PersistenceMapperSupport.copyBase(entity, user);
        user.setUuid(entity.getUuid());
        return user;
    }

    public static UserEntity toEntity(User domain, UserEntity entity) {
        entity.setUuid(domain.getUuid());
        entity.setUsername(domain.getUsername());
        entity.setEmail(domain.getEmail());
        entity.setEmailVerified(domain.isEmailVerified());
        entity.setPasswordHash(domain.getPasswordHash());
        Profile profile = domain.getProfile();
        entity.setGivenName(profile == null ? "" : valueOrEmpty(profile.givenName()));
        entity.setFamilyName(profile == null ? "" : valueOrEmpty(profile.familyName()));
        entity.setMiddleName(profile == null ? null : profile.middleName());
        entity.setNickname(profile == null ? null : profile.nickname());
        entity.setGender(profile == null ? null : profile.gender());
        entity.setBirthdate(profile == null ? null : profile.birthdate());
        entity.setProfileUrl(profile == null ? null : profile.profileUrl());
        entity.setPictureUrl(profile == null ? null : profile.pictureUrl());
        entity.setWebsiteUrl(profile == null ? null : profile.websiteUrl());
        entity.setZoneinfo(profile == null ? null : profile.zoneinfo());
        entity.setLocale(profile == null || profile.locale() == null || profile.locale().isBlank()
                ? "en" : profile.locale());
        entity.setPhoneNumber(profile == null ? null : profile.phoneNumber());
        Address address = domain.getAddress();
        entity.setAddressStreetAddress(address == null ? null : address.streetAddress());
        entity.setAddressLocality(address == null ? null : address.locality());
        entity.setAddressRegion(address == null ? null : address.region());
        entity.setAddressPostalCode(address == null ? null : address.postalCode());
        entity.setAddressCountry(address == null ? null : address.country());
        entity.setPhoneNumberVerified(domain.isPhoneNumberVerified());
        entity.setEnabled(domain.isEnabled());
        entity.setAccountNonExpired(domain.isAccountNonExpired());
        entity.setAccountNonLocked(domain.isAccountNonLocked());
        entity.setCredentialsNonExpired(domain.isCredentialsNonExpired());
        entity.setFailedLoginAttempts(domain.getFailedLoginAttempts());
        entity.setLastLoginAt(domain.getLastLoginAt());
        entity.setPasswordChangedAt(domain.getPasswordChangedAt());
        PersistenceMapperSupport.copyBase(domain, entity);
        return entity;
    }

    private static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
