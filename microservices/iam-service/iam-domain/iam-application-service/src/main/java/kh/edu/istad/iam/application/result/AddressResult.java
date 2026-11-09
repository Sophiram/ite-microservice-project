package kh.edu.istad.iam.application.result;

import kh.edu.istad.iam.domain.entity.User;

public record AddressResult(String streetAddress, String locality, String region,
                                String postalCode, String country) {
        static AddressResult from(User user) {
            var a = user.getAddress();
            return a == null ? new AddressResult(null, null, null, null, null)
                    : new AddressResult(a.streetAddress(), a.locality(), a.region(), a.postalCode(), a.country());
        }
    }
