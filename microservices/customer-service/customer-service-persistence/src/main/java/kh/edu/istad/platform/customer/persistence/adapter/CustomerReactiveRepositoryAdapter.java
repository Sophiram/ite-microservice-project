package kh.edu.istad.platform.customer.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.CustomerId;
import kh.edu.istad.platform.customer.domain.dto.CustomerFilter;
import kh.edu.istad.platform.customer.domain.dto.CustomerStatistic;
import kh.edu.istad.platform.customer.domain.entity.Customer;
import kh.edu.istad.platform.customer.domain.port.out.CustomerRepository;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerSegment;
import kh.edu.istad.platform.customer.domain.valueobject.CustomerStatus;
import kh.edu.istad.platform.customer.persistence.document.CustomerDocument;
import kh.edu.istad.platform.customer.persistence.mapper.CustomerPersistenceMapper;
import kh.edu.istad.platform.customer.persistence.repository.CustomerMongoRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CustomerReactiveRepositoryAdapter implements CustomerRepository {

    private final CustomerMongoRepository customerMongoRepository;
    private final CustomerPersistenceMapper customerPersistenceMapper;
    private final ReactiveMongoTemplate reactiveMongoTemplate;


    @Override
    public Mono<Customer> save(Customer customer) {
        CustomerDocument doc = customerPersistenceMapper.toDocument(customer);
        return reactiveMongoTemplate.save(doc)
                .flatMap(savedDoc -> Mono.justOrEmpty(customerPersistenceMapper.toDomain(savedDoc)));
    }

    @Override
    public Flux<Customer> findAll() {
        return customerMongoRepository.findAll()
                .flatMap(doc -> Mono.justOrEmpty(customerPersistenceMapper.toDomain(doc)));
    }

    @Override
    public Mono<Customer> findById(CustomerId customerId) {
        if (customerId == null || customerId.value() == null) {
            return Mono.empty();
        }
        return customerMongoRepository.findById(customerId.value().toString())
                .flatMap(doc -> Mono.justOrEmpty(customerPersistenceMapper.toDomain(doc)));
    }

    @Override
    public Mono<Void> deleteById(CustomerId customerId) {
        if (customerId == null || customerId.value() == null) {
            return Mono.empty();
        }
        return customerMongoRepository.deleteById(customerId.value().toString());
    }

    @Override
    public Mono<Customer> findByUsername(String username) {
        return customerMongoRepository.findByUsername(username)
                .flatMap(doc -> Mono.justOrEmpty(customerPersistenceMapper.toDomain(doc)));
    }


    @Override
    public Flux<Customer> findByFilter(CustomerFilter filter) {
        Query query = new Query();

        if (filter != null) {
            if (filter.username() != null && !filter.username().isBlank()) {
                query.addCriteria(Criteria.where("username").regex(filter.username(), "i"));
            }
            if (filter.familyName() != null && !filter.familyName().isBlank()) {
                query.addCriteria(Criteria.where("familyName").regex(filter.familyName(), "i"));
            }
            if (filter.givenName() != null && !filter.givenName().isBlank()) {
                query.addCriteria(Criteria.where("givenName").regex(filter.givenName(), "i"));
            }
            if (filter.email() != null && !filter.email().isBlank()) {
                query.addCriteria(Criteria.where("email").regex(filter.email(), "i"));
            }
            if (filter.phoneNumber() != null && !filter.phoneNumber().isBlank()) {
                query.addCriteria(Criteria.where("phoneNumber").regex(filter.phoneNumber(), "i"));
            }
            if (filter.status() != null) {
                query.addCriteria(Criteria.where("status").is(filter.status()));
            }
            if (filter.segment() != null) {
                query.addCriteria(Criteria.where("segment").is(filter.segment()));
            }
        }

        log.info("Dynamic filter query: {}", query);
        return reactiveMongoTemplate.find(query, CustomerDocument.class)
                .flatMap(doc -> Mono.justOrEmpty(customerPersistenceMapper.toDomain(doc)));
    }

    @Override
    public Flux<Customer> query() {
        return findAll();
    }

    // Aggregation

    /**
     * Groups customers by BOTH status AND segment, then counts totals.
     *
     * Equivalent MongoDB shell query:
     * db.customers.aggregate([
     *   { $match: { status: { $exists: true, $ne: null } } },
     *   { $group: { _id: { status: "$status", segment: "$segment" }, total: { $sum: 1 } } },
     *   { $project: { status: "$_id.status", segment: "$_id.segment", total: 1, _id: 0 } },
     *   { $sort: { status: 1, segment: 1 } }
     * ])
     *
     * Example result:
     * { "status": "ACTIVE",   "segment": "SILVER", "total": 8 }
     * { "status": "ACTIVE",   "segment": "GOLD",   "total": 3 }
     * { "status": "INACTIVE", "segment": "SILVER", "total": 2 }
     */
    @Override
    public Flux<CustomerStatistic> getStatistics() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("status").exists(true).ne(null)),
            Aggregation.group("status", "segment").count().as("total"),
            Aggregation.project("total")
                    .and("_id.status").as("status")
                    .and("_id.segment").as("segment"),

            Aggregation.sort(Sort.by("status").and(Sort.by("segment")))
        );

        return reactiveMongoTemplate
                .aggregate(aggregation, "customers", CustomerStatisticAggResult.class)
                .map(result -> {
                    // Safely convert status String -> CustomerStatus enum
                    CustomerStatus status = null;
                    if (result.getStatus() != null) {
                        try {
                            status = CustomerStatus.valueOf(result.getStatus());
                        } catch (IllegalArgumentException e) {
                            log.warn("Unknown status in aggregation: {}", result.getStatus());
                        }
                    }
                    // Safely convert segment String -> CustomerSegment enum
                    CustomerSegment segment = null;
                    if (result.getSegment() != null) {
                        try {
                            segment = CustomerSegment.valueOf(result.getSegment());
                        } catch (IllegalArgumentException e) {
                            log.warn("Unknown segment in aggregation: {}", result.getSegment());
                        }
                    }
                    return new CustomerStatistic(status, segment, result.getTotal());
                });
    }

    /**
     * Internal POJO to capture each aggregation pipeline row.
     * MongoDB maps { status: "ACTIVE", segment: "SILVER", total: 5 } -> this class.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class CustomerStatisticAggResult {
        private String status;
        private String segment;
        private long total;
    }
}
