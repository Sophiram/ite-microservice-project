package kh.edu.istad.platform.customer.domain.entity;


import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.exception.CustomerDomainException;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegment;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;

import java.util.UUID;

public class Customer extends AggregateRoot<CustomerId> {
    private final String username;
    private String familyName;
    private String givenName;
    private final Email email;
    private final PhoneNumber phoneNumber;
    private CustomerStatus status;

    /**
     * CustomerSegment — Manual Reference approach.
     * Stored as a plain enum inside the Customer document (no @DBRef or @DocumentReference).
     * Cleanest approach for reactive MongoDB — no blocking lazy loading.
     *
     * Comparison of 3 approaches:
     * 1. @DBRef                 → stores { $ref, $id }, NOT recommended in reactive (lazy-loading issues)
     * 2. @DocumentReference     → stores only _id of a referenced doc, needs a separate collection
     * 3. Manual (this approach) → store enum directly in the same document, no joins needed
     */
    private CustomerSegment segment;

    public Customer(Email email, String username, PhoneNumber phoneNumber, String familyName, String givenName, CustomerStatus status) {
        this.email = email;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.familyName = familyName;
        this.givenName = givenName;
        this.status = status;
    }

    public void initiateCustomer() {
        validateCustomer();
        super.setId(new CustomerId(UUID.randomUUID()));
        status = CustomerStatus.ACTIVE;
        segment = CustomerSegment.SILVER;   // All new customers start at SILVER
    }

    private void validateCustomer() {
        if (super.getId() != null) {
            throw new CustomerDomainException("Customer id must be null before initiation");
        }
        if (status != null) {
            throw new CustomerDomainException("Customer status must be null before initiation");
        }
    }


    public void updateCustomer(String familyName, String givenName) {
        if (familyName == null || familyName.isBlank()) {
            throw new CustomerDomainException("familyName must not be blank");
        }
        if (givenName == null || givenName.isBlank()) {
            throw new CustomerDomainException("givenName must not be blank");
        }
        this.familyName = familyName;
        this.givenName = givenName;
    }

    public void deactivateCustomer() {
        if (status != CustomerStatus.ACTIVE) {
            throw new CustomerDomainException("Customer is not ACTIVE, cannot deactivate");
        }
        status = CustomerStatus.INACTIVE;
    }


    public void activateCustomer() {
        if (status != CustomerStatus.INACTIVE) {
            throw new CustomerDomainException("Customer is not INACTIVE, cannot activate");
        }
        status = CustomerStatus.ACTIVE;
    }


    public void upgradeSegment(CustomerSegment newSegment) {
        if (newSegment == null) {
            throw new CustomerDomainException("Segment must not be null");
        }
        if (this.segment != null && newSegment.ordinal() <= this.segment.ordinal()) {
            throw new CustomerDomainException(
                "Cannot downgrade or stay at same segment: " + this.segment + " → " + newSegment
            );
        }
        this.segment = newSegment;
    }


    public Email getEmail()               { return email; }
    public String getFamilyName()         { return familyName; }
    public String getGivenName()          { return givenName; }
    public PhoneNumber getPhoneNumber()   { return phoneNumber; }
    public CustomerStatus getStatus()     { return status; }
    public String getUsername()           { return username; }
    public CustomerSegment getSegment()   { return segment; }


    private Customer(Builder builder) {
        super.setId(builder.id);
        email = builder.email;
        familyName = builder.familyName;
        givenName = builder.givenName;
        phoneNumber = builder.phoneNumber;
        status = builder.status;
        username = builder.username;
        segment = builder.segment;
    }

    public static final class Builder {
        private CustomerId id;
        private Email email;
        private String familyName;
        private String givenName;
        private PhoneNumber phoneNumber;
        private CustomerStatus status;
        private String username;
        private CustomerSegment segment;

        private Builder() {}

        public static Builder builder() { return new Builder(); }

        public Builder id(CustomerId val)               { id = val;          return this; }
        public Builder email(Email val)                  { email = val;       return this; }
        public Builder familyName(String val)            { familyName = val;  return this; }
        public Builder givenName(String val)             { givenName = val;   return this; }
        public Builder phoneNumber(PhoneNumber val)      { phoneNumber = val; return this; }
        public Builder status(CustomerStatus val)        { status = val;      return this; }
        public Builder username(String val)              { username = val;    return this; }
        public Builder segment(CustomerSegment val)      { segment = val;     return this; }

        public Customer build() { return new Customer(this); }
    }
}
