package kh.edu.istad.platform.customer.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.customer.domain.dto.DeactivateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.DeactivateCustomerResult;
import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerResult;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerResult;
import kh.edu.istad.platform.customer.domain.usecase.DeactivateCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.InitiateCustomerUseCase;
import kh.edu.istad.platform.customer.domain.usecase.UpdateCustomerUseCase;
import kh.edu.istad.platform.customer.restapi.dto.*;
import kh.edu.istad.platform.customer.restapi.mapper.CustomerWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/customers")
public class CustomerController {

    private final InitiateCustomerUseCase initiateCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeactivateCustomerUseCase deactivateCustomerUseCase;
    private final CustomerWebMapper customerWebMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CustomerInitiateResponse initiateCustomer(@Valid @RequestBody CustomerInitiateRequest customerInitiateRequest){
        InitiateCustomerResult result = initiateCustomerUseCase.execute(
                customerWebMapper.toCommand(customerInitiateRequest)
        );
        return customerWebMapper.toResponse(result);
    }

    @PutMapping("/{id}")
    public CustomerUpdateResponse updateCustomer(
            @PathVariable("id") UUID id,
            @Valid @RequestBody CustomerUpdateRequest customerUpdateRequest
    ) {
        UpdateCustomerResult result = updateCustomerUseCase.execute(
                customerWebMapper.toCommand(id, customerUpdateRequest)
        );
        return customerWebMapper.toResponse(result);
    }

    @PatchMapping("/{id}/deactivate")
    public CustomerDeactivateResponse deactivateCustomer(@PathVariable("id") UUID id) {
        DeactivateCustomerResult result = deactivateCustomerUseCase.execute(
                new DeactivateCustomerCommand(id)
        );
        return customerWebMapper.toResponse(result);
    }
}

