package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerResult;
import kh.edu.istad.platform.customer.domain.exception.CustomerNotFoundException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.domain.service.CustomerDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateCustomerUseCase {

    private final CustomerDomainService customerDomainService;
    private final CustomerRepository customerRepository;

    public Mono<UpdateCustomerResult> execute(UpdateCustomerCommand command) {
        log.info("Updating customer: {}", command.customerId());

        return customerRepository.findById(new CustomerId(command.customerId()))
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                        "Customer not found with id: " + command.customerId())))
                .flatMap(customer -> {
                    // Pass command values to the domain service (bug fix: was passing customer's own values)
                    customerDomainService.updateCustomer(customer, command.familyName(), command.givenName());
                    return customerRepository.save(customer);
                })
                .map(saved -> new UpdateCustomerResult(
                        saved.getId() != null ? saved.getId().value() : null,
                        saved.getUsername(),
                        saved.getFamilyName(),
                        saved.getGivenName(),
                        saved.getEmail() != null ? saved.getEmail().value() : null,
                        saved.getPhoneNumber() != null ? saved.getPhoneNumber().value() : null,
                        saved.getStatus()
                ));
    }
}
