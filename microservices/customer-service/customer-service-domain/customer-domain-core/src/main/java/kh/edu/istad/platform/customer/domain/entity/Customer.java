package kh.edu.istad.platform.customer.domain.entity;


import kh.edu.istad.common.domain.entity.AggregateRoot;
import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.exception.CustomerDomainException;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;

import java.util.Objects;
import java.util.UUID;

public class Customer extends AggregateRoot<CustomerId> {
    private final String username;
    private String familyName;
    private String givenName;
    private final Email email;
    private final PhoneNumber phoneNumber;
    private CustomerStatus status;

    // Domain Critical Logic
    public void initiateCustomer(){
        validateCustomer();
        super.setId(new CustomerId(UUID.randomUUID()));
        status = CustomerStatus.ACTIVE;
    }
    private void validateCustomer(){
        if (super.getId() != null){
            throw new CustomerDomainException("Customer id must be null");
        }
        if (status != null){
            throw new CustomerDomainException("Customer status must be null");
        }
    }
    public void updateCustomer(String familyName, String givenName){
        if (familyName == null || givenName == null){
            throw new CustomerDomainException("familName and givenName must not be null");
        }
        this.familyName = familyName;
        this.givenName = givenName;
    }

    public void deactivateCustomer(){
        if (status != CustomerStatus.ACTIVE){
            throw new CustomerDomainException("Could not deactivate because customer is active");
        }
        status = CustomerStatus.INACTIVE;
    }

    public Email getEmail() {
        return email;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public String getUsername() {
        return username;
    }

    private Customer(Builder builder) {
        super.setId(builder.id);
        email = builder.email;
        familyName = builder.familyName;
        givenName = builder.givenName;
        phoneNumber = builder.phoneNumber;
        status = builder.status;
        username = builder.username;
    }


    public static final class Builder {
        private CustomerId id;
        private Email email;
        private String familyName;
        private String givenName;
        private PhoneNumber phoneNumber;
        private CustomerStatus status;
        private String username;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder id(CustomerId val) {
            id = val;
            return this;
        }

        public Builder email(Email val) {
            email = val;
            return this;
        }

        public Builder familyName(String val) {
            familyName = val;
            return this;
        }

        public Builder givenName(String val) {
            givenName = val;
            return this;
        }

        public Builder phoneNumber(PhoneNumber val) {
            phoneNumber = val;
            return this;
        }

        public Builder status(CustomerStatus val) {
            status = val;
            return this;
        }

        public Builder username(String val) {
            username = val;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }
}
