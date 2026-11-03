package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerCommand;
import kh.edu.istad.platform.customer.domain.dto.InitiateCustomerResult;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.event.CustomerInitiatedEvent;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.domain.service.CustomerDomainService;
import kh.edu.istad.platform.customer.domain.valueobject.Email;
import kh.edu.istad.platform.customer.domain.valueobject.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class InitiateCustomerUseCase {

    private final CustomerDomainService customerDomainService;
    private final CustomerRepository customerRepository;


    public Mono<InitiateCustomerResult> execute(InitiateCustomerCommand command) {
        log.info("Initiating customer: {}", command);

        Customer customer = Customer.Builder.builder()
                .username(command.username())
                .familyName(command.familyName())
                .givenName(command.givenName())
                .email(command.email() != null ? new Email(command.email()) : null)
                .phoneNumber(command.phoneNumber() != null ? new PhoneNumber(command.phoneNumber()) : null)
                .build();

        CustomerInitiatedEvent event = customerDomainService.initiateCustomer(customer);

        return customerRepository.save(event.getCustomer())
                .map(savedCustomer -> new InitiateCustomerResult(
                        savedCustomer.getId() != null ? savedCustomer.getId().value() : null,
                        savedCustomer.getUsername(),
                        savedCustomer.getFamilyName(),
                        savedCustomer.getGivenName(),
                        savedCustomer.getEmail() != null ? savedCustomer.getEmail().value() : null,
                        savedCustomer.getPhoneNumber() != null ? savedCustomer.getPhoneNumber().value() : null
                ));
    }

    public Mono<Customer> create(Customer customer) {
        customer.initiateCustomer();
        return customerRepository.save(customer);
    }
}
