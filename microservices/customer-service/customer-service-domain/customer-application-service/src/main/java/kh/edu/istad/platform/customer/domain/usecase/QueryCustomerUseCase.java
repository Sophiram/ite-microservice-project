package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.CustomerFilter;
import kh.edu.istad.platform.customer.domain.dto.CustomerResult;
import kh.edu.istad.platform.customer.domain.dto.CustomerStatistic;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.exception.CustomerNotFoundException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueryCustomerUseCase {

    private final CustomerRepository customerRepository;


    public Flux<CustomerResult> findAllCustomers() {
        return customerRepository.findAll()
                .map(this::toResult);
    }

    public Mono<CustomerResult> findById(UUID customerId) {
        return customerRepository.findById(new CustomerId(customerId))
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                        "Customer not found with id: " + customerId)))
                .map(this::toResult);
    }

    public Mono<CustomerResult> findByUsername(String username) {
        return customerRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                        "Customer not found with username: " + username)))
                .map(this::toResult);
    }


    public Flux<CustomerResult> findByFilter(CustomerFilter filter) {
        log.info("Filtering customers by: {}", filter);
        return customerRepository.findByFilter(filter)
                .map(this::toResult);
    }


    public Flux<CustomerStatistic> getStatistics() {
        log.info("Getting customer statistics grouped by status and segment");
        return customerRepository.getStatistics();
    }

//    Helper
    public CustomerResult toResult(Customer customer) {
        UUID id      = customer.getId() != null ? customer.getId().value() : null;
        String email = customer.getEmail() != null ? customer.getEmail().value() : null;
        String phone = customer.getPhoneNumber() != null ? customer.getPhoneNumber().value() : null;

        return new CustomerResult(
                id,
                customer.getUsername(),
                customer.getFamilyName(),
                customer.getGivenName(),
                email,
                phone,
                customer.getStatus(),
                customer.getSegment()
        );
    }
}
