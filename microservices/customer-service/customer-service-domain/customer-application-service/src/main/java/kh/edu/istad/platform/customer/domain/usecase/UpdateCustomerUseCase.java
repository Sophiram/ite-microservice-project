package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.UpdateCustomerResult;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.event.CustomerUpdatedEvent;
import kh.edu.istad.platform.customer.domain.exception.CustomerNotFoundException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.domain.service.CustomerDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateCustomerUseCase {

    private final CustomerDomainService customerDomainService;
    private final CustomerRepository customerRepository;

    public UpdateCustomerResult execute(UpdateCustomerCommand command) {
        log.info("update customer usecase: {}", command);

        Customer customer = customerRepository.findById(new CustomerId(command.customerId()))
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + command.customerId()));

        customer.updateCustomer(command.familyName(), command.givenName());
        CustomerUpdatedEvent event = customerDomainService.updateCustomer(customer);
        Customer updatedCustomer = customerRepository.save(event.getCustomer());

        return new UpdateCustomerResult(
                updatedCustomer.getId().value(),
                updatedCustomer.getUsername(),
                updatedCustomer.getFamilyName(),
                updatedCustomer.getGivenName(),
                updatedCustomer.getEmail() != null ? updatedCustomer.getEmail().value() : null,
                updatedCustomer.getPhoneNumber() != null ? updatedCustomer.getPhoneNumber().value() : null,
                updatedCustomer.getStatus()
        );
    }
}
