package kh.edu.istad.iam.domain.entity;

public record Address(
        String streetAddress,
        String locality,
        String region,
        String postalCode,
        String country
        ) {

    public static Address empty(){
        return new Address(null, null, null, null, null);
    }

    public boolean isEmpty(){
        return streetAddress == null && locality == null
                && region == null && postalCode == null
                && country == null;
    }
}
