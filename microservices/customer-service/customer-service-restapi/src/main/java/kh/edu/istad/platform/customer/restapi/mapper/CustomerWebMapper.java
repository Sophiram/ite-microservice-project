package kh.edu.istad.platform.customer.restapi.mapper;

import kh.edu.istad.platform.customer.domain.dto.DeactivateCustomerResult;
import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerResult;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerResult;
import kh.edu.istad.platform.customer.restapi.dto.CustomerDeactivateResponse;
import kh.edu.istad.platform.customer.restapi.dto.CustomerInitiateRequest;
import kh.edu.istad.platform.customer.restapi.dto.CustomerInitiateResponse;
import kh.edu.istad.platform.customer.restapi.dto.CustomerUpdateRequest;
import kh.edu.istad.platform.customer.restapi.dto.CustomerUpdateResponse;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CustomerWebMapper {

    InitiateCustomerCommand toCommand(CustomerInitiateRequest request);

    CustomerInitiateResponse toResponse(InitiateCustomerResult result);

    default UpdateCustomerCommand toCommand(UUID customerId, CustomerUpdateRequest request) {
        return new UpdateCustomerCommand(customerId, request.familyName(), request.givenName());
    }

    CustomerUpdateResponse toResponse(UpdateCustomerResult result);

    CustomerDeactivateResponse toResponse(DeactivateCustomerResult result);
}

