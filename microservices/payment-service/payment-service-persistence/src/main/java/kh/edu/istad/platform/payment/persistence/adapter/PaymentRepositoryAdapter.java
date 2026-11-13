package kh.edu.istad.platform.payment.persistence.adapter;

import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.payment.domain.entity.Payment;
import kh.edu.istad.platform.payment.domain.port.out.PaymentRepository;
import kh.edu.istad.platform.payment.domain.valueobject.PaymentId;
import kh.edu.istad.platform.payment.persistence.entity.PaymentEntity;
import kh.edu.istad.platform.payment.persistence.mapper.PaymentPersistenceMapper;
import kh.edu.istad.platform.payment.persistence.repository.PaymentReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentReactiveRepository paymentReactiveRepository;
    private final R2dbcEntityTemplate entityTemplate;

    @Override
    @Transactional
    public Mono<Payment> save(Payment payment) {
        log.info("Saving payment: id={}", payment.getId() != null ? payment.getId().value() : null);

        PaymentEntity entity = PaymentPersistenceMapper.toEntity(payment);
        if (entity == null || entity.getId() == null) {
            return Mono.empty();
        }

        return entityTemplate.select(PaymentEntity.class)
                .matching(Query.query(Criteria.where("id").is(entity.getId())))
                .exists()
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return entityTemplate.update(entity).thenReturn(payment);
                    } else {
                        return entityTemplate.insert(entity).thenReturn(payment);
                    }
                });
    }

    @Override
    public Mono<Payment> findById(PaymentId paymentId) {
        if (paymentId == null || paymentId.value() == null) {
            return Mono.empty();
        }
        return paymentReactiveRepository.findById(paymentId.value())
                .map(PaymentPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Payment> findByOrderId(OrderId orderId) {
        if (orderId == null || orderId.value() == null) {
            return Mono.empty();
        }
        return paymentReactiveRepository.findByOrderId(orderId.value())
                .map(PaymentPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Payment> findAll() {
        return paymentReactiveRepository.findAll()
                .map(PaymentPersistenceMapper::toDomain);
    }
}
