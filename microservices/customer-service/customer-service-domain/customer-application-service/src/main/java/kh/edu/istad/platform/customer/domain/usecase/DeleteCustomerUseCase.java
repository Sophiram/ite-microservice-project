package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.DeactivateCustomerCommand;
import kh.edu.istad.platform.customer.domain.exception.CustomerNotFoundException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Permanently deletes a customer by ID.
 * Returns Mono<Void> — no response body on DELETE.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DeleteCustomerUseCase {

    private final CustomerRepository customerRepository;

    public Mono<Void> execute(DeactivateCustomerCommand command) {
        log.info("Deleting customer: {}", command.customerId());

        CustomerId customerId = new CustomerId(command.customerId());

        // First verify the customer exists, then delete
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                        "Customer not found with id: " + command.customerId())))
                .flatMap(customer -> customerRepository.deleteById(customerId));
    }
}
