package kh.edu.istad.platform.customer.restapi.mapper;

import kh.edu.istad.platform.customer.domain.dto.*;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegment;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;
import kh.edu.istad.platform.customer.restapi.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerWebMapper {

    //  Request → Command

    InitiateCustomerCommand toCommand(CustomerInitiateRequest request);

    default UpdateCustomerCommand toCommand(UUID customerId, CustomerUpdateRequest request) {
        return new UpdateCustomerCommand(customerId, request.familyName(), request.givenName());
    }

    default UpgradeSegmentCommand toCommand(UUID customerId, UpgradeSegmentRequest request) {
        return new UpgradeSegmentCommand(customerId, request.segment());
    }

    // ─── Value object helpers for MapStruct ──────────────────────────────────

    default Email toEmail(String email) {
        return email != null ? new Email(email) : null;
    }

    default PhoneNumber toPhoneNumber(String phone) {
        return phone != null ? new PhoneNumber(phone) : null;
    }

    // Domain → Response

    CustomerInitiateResponse toResponse(InitiateCustomerResult result);

    CustomerUpdateResponse toResponse(UpdateCustomerResult result);

    CustomerDeactivateResponse toResponse(DeactivateCustomerResult result);

    CustomerResponse toResponse(CustomerResult result);

    UpgradeSegmentResponse toResponse(UpgradeSegmentResult result);
}
