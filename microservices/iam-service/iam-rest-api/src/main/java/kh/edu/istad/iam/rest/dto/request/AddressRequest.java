package kh.edu.istad.iam.rest.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kh.edu.istad.iam.domain.entity.Address;

public record AddressRequest(@Size(max = 255) String streetAddress,
                                 @Size(max = 100) String locality,
                                 @Size(max = 100) String region,
                                 @Size(max = 20) String postalCode,
                                 @Pattern(regexp = "[A-Za-z]{2}") String country) {
        public Address domain() { return new Address(streetAddress, locality, region, postalCode, country); }
    }
