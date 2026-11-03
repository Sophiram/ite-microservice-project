package kh.edu.istad.platform.customer.domain.service;

import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.event.CustomerDeactivatedEvent;
import kh.edu.istad.platform.customer.domain.event.CustomerInitiatedEvent;
import kh.edu.istad.platform.customer.domain.event.CustomerUpdatedEvent;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegment;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CustomerDomainServiceImpl implements CustomerDomainService {

    @Override
    public CustomerInitiatedEvent initiateCustomer(Customer customer) {
        customer.initiateCustomer();
        return new CustomerInitiatedEvent(customer, ZonedDateTime.now(ZoneId.of("UTC")));
    }

    @Override
    public CustomerUpdatedEvent updateCustomer(Customer customer, String familyName, String givenName) {
        customer.updateCustomer(familyName, givenName);
        return new CustomerUpdatedEvent(customer, ZonedDateTime.now(ZoneId.of("UTC")));
    }

    @Override
    public CustomerDeactivatedEvent deactivateCustomer(Customer customer) {
        customer.deactivateCustomer();
        return new CustomerDeactivatedEvent(customer, ZonedDateTime.now(ZoneId.of("UTC")));
    }

    @Override
    public CustomerUpdatedEvent activateCustomer(Customer customer) {
        customer.activateCustomer();
        return new CustomerUpdatedEvent(customer, ZonedDateTime.now(ZoneId.of("UTC")));
    }

    @Override
    public CustomerUpdatedEvent upgradeSegment(Customer customer, CustomerSegment newSegment) {
        customer.upgradeSegment(newSegment);
        return new CustomerUpdatedEvent(customer, ZonedDateTime.now(ZoneId.of("UTC")));
    }
}
