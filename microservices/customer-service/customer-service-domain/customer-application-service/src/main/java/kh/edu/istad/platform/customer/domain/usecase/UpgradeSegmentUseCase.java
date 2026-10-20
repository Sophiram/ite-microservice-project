package kh.edu.istad.platform.customer.domain.usecase;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.UpgradeSegmentCommand;
import kh.edu.istad.platform.customer.domain.dto.UpgradeSegmentResult;
import kh.edu.istad.platform.customer.domain.exception.CustomerNotFoundException;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.domain.service.CustomerDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Upgrades a customer's segment (SILVER → GOLD → PREMIUM).
 * Business rule: segments can only increase — no downgrade allowed.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UpgradeSegmentUseCase {

    private final CustomerDomainService customerDomainService;
    private final CustomerRepository customerRepository;

    public Mono<UpgradeSegmentResult> execute(UpgradeSegmentCommand command) {
        log.info("Upgrading segment for customer {} to {}", command.customerId(), command.newSegment());

        return customerRepository.findById(new CustomerId(command.customerId()))
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                        "Customer not found with id: " + command.customerId())))
                .flatMap(customer -> {
                    // Domain rule enforced inside Customer.upgradeSegment()
                    customerDomainService.upgradeSegment(customer, command.newSegment());
                    return customerRepository.save(customer);
                })
                .map(saved -> new UpgradeSegmentResult(
                        saved.getId() != null ? saved.getId().value() : null,
                        saved.getUsername(),
                        saved.getSegment(),
                        "Segment upgraded to " + saved.getSegment()
                ));
    }
}
