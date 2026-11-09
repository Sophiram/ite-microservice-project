package kh.edu.istad.iam.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import kh.edu.istad.iam.domain.entity.Address;
import kh.edu.istad.iam.domain.entity.Profile;

public record UpdateUserProfileRequest(
            @Size(max = 100)
            String givenName,
            @Size(max = 100)
            String familyName,
            @Size(max = 100)
            String middleName,
            @Size(max = 100)
            String nickname,
            @Pattern(regexp = "female|male|other")
            String gender,
            LocalDate birthdate,
            @Size(max = 500)
            String profileUrl,
            @Size(max = 500)
            String pictureUrl,
            @Size(max = 500)
            String websiteUrl,
            @Size(max = 50)
            String zoneinfo,
            @Pattern(regexp = "en|km")
            String locale,
            @Pattern(regexp = "^$|^\\+[1-9][0-9]{7,14}$")
            String phoneNumber,
            @Valid AddressRequest address)
{
        public Profile profile() {
            return new Profile(givenName, familyName, middleName, nickname, gender, birthdate,
                    profileUrl, pictureUrl, websiteUrl, zoneinfo, locale, phoneNumber);
        }
        public Address addressDomain() { return address == null ? null : address.domain(); }
    }
