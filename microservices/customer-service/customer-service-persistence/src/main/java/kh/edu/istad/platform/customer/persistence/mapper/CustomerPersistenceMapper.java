package kh.edu.istad.platform.customer.persistence.mapper;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;
import kh.edu.istad.platform.customer.persistence.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceMapper {

    public CustomerEntity toEntity(Customer customer) {
        if (customer == null) {
            return null;
        }
        return CustomerEntity.builder()
                .id(customer.getId() != null ? customer.getId().value() : null)
                .username(customer.getUsername())
                .familyName(customer.getFamilyName())
                .givenName(customer.getGivenName())
                .email(customer.getEmail() != null ? customer.getEmail().value() : null)
                .phoneNumber(customer.getPhoneNumber() != null ? customer.getPhoneNumber().value() : null)
                .status(customer.getStatus())
                .build();
    }

    public Customer toDomain(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }
        return Customer.Builder.builder()
                .id(entity.getId() != null ? new CustomerId(entity.getId()) : null)
                .username(entity.getUsername())
                .familyName(entity.getFamilyName())
                .givenName(entity.getGivenName())
                .email(entity.getEmail() != null ? new Email(entity.getEmail()) : null)
                .phoneNumber(entity.getPhoneNumber() != null ? new PhoneNumber(entity.getPhoneNumber()) : null)
                .status(entity.getStatus())
                .build();
    }
}
