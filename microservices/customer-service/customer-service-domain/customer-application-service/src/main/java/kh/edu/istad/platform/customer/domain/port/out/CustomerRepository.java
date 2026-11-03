package kh.edu.istad.platform.customer.domain.port.out;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.CustomerFilter;
import kh.edu.istad.platform.customer.domain.dto.CustomerStatistic;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerRepository {
    Mono<Customer> save(Customer customer);
    Flux<Customer> findAll();

    Mono<Customer> findById(CustomerId customerId);

    Mono<Void> deleteById(CustomerId customerId);
    Mono<Customer> findByUsername(String username);

    Flux<Customer> query();
    Flux<Customer> findByFilter(CustomerFilter filter);

    // Task 3: Aggregation — group customers by status and count totals
    Flux<CustomerStatistic> getStatistics();
}

