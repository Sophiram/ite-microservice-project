package kh.edu.istad.platform.customer.domain.service;

import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.event.CustomerDeactivatedEvent;
import kh.edu.istad.platform.customer.domain.event.CustomerInitiatedEvent;
import kh.edu.istad.platform.customer.domain.event.CustomerUpdatedEvent;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegment;

public interface CustomerDomainService {
    CustomerInitiatedEvent initiateCustomer(Customer customer);
    CustomerUpdatedEvent updateCustomer(Customer customer, String familyName, String givenName);
    CustomerDeactivatedEvent deactivateCustomer(Customer customer);
    CustomerUpdatedEvent activateCustomer(Customer customer);
    CustomerUpdatedEvent upgradeSegment(Customer customer, CustomerSegment newSegment);
}
