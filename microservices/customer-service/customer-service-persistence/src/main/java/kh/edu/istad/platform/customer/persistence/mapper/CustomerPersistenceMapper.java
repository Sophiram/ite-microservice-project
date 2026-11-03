package kh.edu.istad.platform.customer.persistence.mapper;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;
import kh.edu.istad.platform.customer.persistence.document.CustomerDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerPersistenceMapper {

    Logger log = LoggerFactory.getLogger(CustomerPersistenceMapper.class);

    // Document → Domain
    //
    // Customer uses a private Builder pattern (no setters).
    // Written manually as a default method using Customer.Builder.
    //

    default Customer toDomain(CustomerDocument doc) {
        if (doc == null) return null;

        CustomerId customerId = null;
        if (doc.getId() != null) {
            try {
                customerId = new CustomerId(UUID.fromString(doc.getId()));
            } catch (IllegalArgumentException e) {
                log.warn("Skipping legacy document with non-UUID id='{}'. " +
                         "Please migrate or delete this record from MongoDB.", doc.getId());
                return null;
            }
        }

        Email email = null;
        if (doc.getEmail() != null) {
            email = new Email(doc.getEmail());
        }

        PhoneNumber phoneNumber = null;
        if (doc.getPhoneNumber() != null) {
            phoneNumber = new PhoneNumber(doc.getPhoneNumber());
        }

        return Customer.Builder.builder()
                .id(customerId)
                .username(doc.getUsername())
                .familyName(doc.getFamilyName())
                .givenName(doc.getGivenName())
                .email(email)
                .phoneNumber(phoneNumber)
                .status(doc.getStatus())
                .segment(doc.getSegment())
                .build();
    }

    // Domain → Document
    @Mapping(source = "id.value",           target = "id")
    @Mapping(source = "email.value",        target = "email")
    @Mapping(source = "phoneNumber.value",  target = "phoneNumber")
    @Mapping(source = "segment",            target = "segment")
    CustomerDocument toDocument(Customer customer);
}
