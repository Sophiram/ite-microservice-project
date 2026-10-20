package kh.edu.istad.platform.customer.domain.event;

import kh.edu.istad.common.domain.event.DomainEvent;
import kh.edu.istad.platform.customer.domain.entity.Customer;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class QueryEvent implements DomainEvent<Customer> {
    private final Customer customer;
    private final ZonedDateTime queriedAt;

    public QueryEvent(Customer customer) {
        this(customer, ZonedDateTime.now(ZoneId.of("UTC")));
    }

    public QueryEvent(Customer customer, ZonedDateTime queriedAt) {
        this.customer = customer;
        this.queriedAt = queriedAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public ZonedDateTime getQueriedAt() {
        return queriedAt;
    }
}
