package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.DeactivateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.DeactivateCustomerResult;
import kh.edu.istad.platform.customer.domain.exception.CustomerNotFoundException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.domain.service.CustomerDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Reactivates a previously deactivated customer.
 * Reuses DeactivateCustomerCommand (same input: just a customerId).
 * Returns DeactivateCustomerResult (carries status field to confirm ACTIVE).
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ActivateCustomerUseCase {

    private final CustomerDomainService customerDomainService;
    private final CustomerRepository customerRepository;

    public Mono<DeactivateCustomerResult> execute(DeactivateCustomerCommand command) {
        log.info("Activating customer: {}", command.customerId());

        return customerRepository.findById(new CustomerId(command.customerId()))
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                        "Customer not found with id: " + command.customerId())))
                .flatMap(customer -> {
                    customerDomainService.activateCustomer(customer);
                    return customerRepository.save(customer);
                })
                .map(saved -> new DeactivateCustomerResult(
                        saved.getId() != null ? saved.getId().value() : null,
                        saved.getStatus(),
                        "Customer successfully activated"
                ));
    }
}
