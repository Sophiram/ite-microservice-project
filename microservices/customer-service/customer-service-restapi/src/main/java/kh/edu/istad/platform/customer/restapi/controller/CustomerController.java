package kh.edu.istad.platform.customer.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.customer.domain.dto.CustomerFilter;
import kh.edu.istad.platform.customer.domain.dto.CustomerStatistic;
import kh.edu.istad.platform.customer.domain.dto.DeactivateCustomerCommand;
import kh.edu.istad.platform.customer.domain.usecase.*;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegment;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import kh.edu.istad.platform.customer.restapi.dto.*;
import kh.edu.istad.platform.customer.restapi.mapper.CustomerWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/customers")
public class CustomerController {

    private final InitiateCustomerUseCase initiateCustomerUseCase;
    private final QueryCustomerUseCase queryCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeactivateCustomerUseCase deactivateCustomerUseCase;
    private final ActivateCustomerUseCase activateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;
    private final UpgradeSegmentUseCase upgradeSegmentUseCase;
    private final CustomerWebMapper customerWebMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CustomerInitiateResponse> initiateCustomer(
            @Valid @RequestBody CustomerInitiateRequest request
    ) {
        return initiateCustomerUseCase.execute(customerWebMapper.toCommand(request))
                .map(customerWebMapper::toResponse);
    }


    @GetMapping
    public Flux<CustomerResponse> getAllCustomers() {
        return queryCustomerUseCase.findAllCustomers()
                .map(customerWebMapper::toResponse);
    }


    @GetMapping("/{id}")
    public Mono<CustomerResponse> getCustomerById(@PathVariable UUID id) {
        return queryCustomerUseCase.findById(id)
                .map(customerWebMapper::toResponse);
    }

    @GetMapping("/search")
    public Mono<CustomerResponse> getCustomerByUsername(@RequestParam String username) {
        return queryCustomerUseCase.findByUsername(username)
                .map(customerWebMapper::toResponse);
    }

    @PutMapping("/{id}")
    public Mono<CustomerUpdateResponse> updateCustomer(
            @PathVariable UUID id,
            @Valid @RequestBody CustomerUpdateRequest request
    ) {
        return updateCustomerUseCase.execute(customerWebMapper.toCommand(id, request))
                .map(customerWebMapper::toResponse);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCustomer(@PathVariable UUID id) {
        return deleteCustomerUseCase.execute(new DeactivateCustomerCommand(id));
    }

    @PatchMapping("/{id}/deactivate")
    public Mono<CustomerDeactivateResponse> deactivateCustomer(@PathVariable UUID id) {
        return deactivateCustomerUseCase.execute(new DeactivateCustomerCommand(id))
                .map(customerWebMapper::toResponse);
    }

    @PatchMapping("/{id}/activate")
    public Mono<CustomerDeactivateResponse> activateCustomer(@PathVariable UUID id) {
        return activateCustomerUseCase.execute(new DeactivateCustomerCommand(id))
                .map(customerWebMapper::toResponse);
    }

    @PatchMapping("/{id}/segment")
    public Mono<UpgradeSegmentResponse> upgradeSegment(
            @PathVariable UUID id,
            @Valid @RequestBody UpgradeSegmentRequest request
    ) {
        return upgradeSegmentUseCase.execute(customerWebMapper.toCommand(id, request))
                .map(customerWebMapper::toResponse);
    }


    @GetMapping("/filter")
    public Flux<CustomerResponse> filterCustomers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String familyName,
            @RequestParam(required = false) String givenName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) CustomerStatus status,
            @RequestParam(required = false) CustomerSegment segment
    ) {
        CustomerFilter filter = new CustomerFilter(
                username, familyName, givenName, email, phoneNumber, status, segment);
        return queryCustomerUseCase.findByFilter(filter)
                .map(customerWebMapper::toResponse);
    }

    @GetMapping("/statistic")
    public Flux<CustomerStatistic> getStatistics() {
        return queryCustomerUseCase.getStatistics();
    }

    @GetMapping("/report")
    public Flux<CustomerStatistic> getReport() {
        return queryCustomerUseCase.getStatistics();
    }
}
