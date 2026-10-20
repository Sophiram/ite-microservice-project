package kh.edu.istad.platform.customer.domain.event;

import kh.edu.istad.platform.customer.domain.entity.Customer;

import java.time.ZonedDateTime;

public class CustomerQueriedEvent extends QueryEvent {

    public CustomerQueriedEvent(Customer customer) {
        super(customer);
    }

    public CustomerQueriedEvent(Customer customer, ZonedDateTime queriedAt) {
        super(customer, queriedAt);
    }
}
