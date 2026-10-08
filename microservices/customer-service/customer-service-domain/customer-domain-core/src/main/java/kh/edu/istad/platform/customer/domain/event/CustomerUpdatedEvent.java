package kh.edu.istad.platform.customer.domain.event;

import kh.edu.istad.common.domain.event.DomainEvent;
import kh.edu.istad.platform.customer.domain.entity.Customer;

import java.time.ZonedDateTime;

public class CustomerUpdatedEvent implements DomainEvent<Customer>

    {
        private final Customer customer;
        private final ZonedDateTime updatedAt;

    public CustomerUpdatedEvent(Customer customer, ZonedDateTime updatedAt) {
        this.customer = customer;
        this.updatedAt = updatedAt;
    }

        public Customer getCustomer() {
        return customer;
    }

        public ZonedDateTime updatedAt() {
        return updatedAt;
    }
    }