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

@Component
@Slf4j
@RequiredArgsConstructor
public class InitiateCustomerUseCase {

    private final CustomerDomainService customerDomainService;
    private final CustomerRepository customerRepository;

    public InitiateCustomerResult execute(InitiateCustomerCommand command) {
        log.info("initiate customer usecase: {}", command);

        Customer customer = Customer.Builder.builder()
                .username(command.username())
                .familyName(command.familyName())
                .givenName(command.givenName())
                .email(new Email(command.email()))
                .phoneNumber(new PhoneNumber(command.phoneNumber()))
                .build();

        CustomerInitiatedEvent event = customerDomainService.initiateCustomer(customer);
        Customer savedCustomer = customerRepository.save(event.getCustomer());

        return new InitiateCustomerResult(
                savedCustomer.getId().value(),
                savedCustomer.getUsername(),
                savedCustomer.getFamilyName(),
                savedCustomer.getGivenName(),
                savedCustomer.getEmail() != null ? savedCustomer.getEmail().value() : null,
                savedCustomer.getPhoneNumber() != null ? savedCustomer.getPhoneNumber().value() : null
        );
    }
}
